/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.monitoring.annotation;

import com.google.common.base.Strings;
import io.prometheus.client.Summary;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.ControllerAdvice;


import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

@Aspect("pertarget(se.inera.intyg.infra.monitoring.annotation.MethodTimer.timeable())")
@Scope("prototype")
@ControllerAdvice
public class MethodTimer {
    private final ReadWriteLock summaryLock = new ReentrantReadWriteLock();
    private final HashMap<String, Summary> summaries = new HashMap<>();

    static final String P2 = "se.inera.";
    static final String P1 = P2 + "intyg.";


    @Pointcut("@annotation(se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod)")
    public void timeable() {
    }

    //
    private PrometheusTimeMethod getAnnotation(final Class targetClass, final MethodSignature signature) throws NoSuchMethodException {
        PrometheusTimeMethod annot = findAnnotation(targetClass, PrometheusTimeMethod.class);
        if (annot == null) {
            // When target is an AOP interface proxy but annotation is on class method (rather than Interface method).
            annot = findAnnotation(targetClass.getDeclaredMethod(signature.getName(), signature.getParameterTypes()),
                    PrometheusTimeMethod.class);
        }
        return annot;
    }

    //
    private Summary ensureSummary(final ProceedingJoinPoint pjp, final String key) throws IllegalStateException {
        final PrometheusTimeMethod annot;
        try {
            annot = getAnnotation(pjp.getTarget().getClass(), (MethodSignature) pjp.getSignature());
        } catch (NoSuchMethodException | NullPointerException e) {
            throw new IllegalStateException("Annotation could not be found for pjp \"" + pjp.toShortString() + "\"", e);
        }

        // We use a writeLock here to guarantee no concurrent reads.
        final Lock writeLock = summaryLock.writeLock();
        writeLock.lock();
        try {
            // Check one last time with full mutual exclusion in case multiple readers got null before creation.
            Summary summary = summaries.get(key);
            if (summary != null) {
                return summary;
            }

            final String name = annot.name();

            summary = Summary.build()
                    .name(Strings.isNullOrEmpty(name) ? key : name)
                    .help(annot.help())
                    .register();

            // Even a rehash of the underlying table will not cause issues as we mutually exclude readers while we
            // perform our updates.
            summaries.put(key, summary);

            return summary;
        } finally {
            writeLock.unlock();
        }
    }

    @Around("timeable()")
    public Object timeMethod(final ProceedingJoinPoint pjp) throws Throwable {
        final Signature signature = pjp.getSignature();
        final String key = signatureToKeyName(signature.getDeclaringTypeName(), signature.getName(), pjp.getArgs());

        Summary summary;
        final Lock r = summaryLock.readLock();
        r.lock();
        try {
            summary = summaries.get(key);
        } finally {
            r.unlock();
        }

        if (summary == null) {
            summary = ensureSummary(pjp, key);
        }

        final Summary.Timer t = summary.startTimer();

        try {
            return pjp.proceed();
        } finally {
            t.observeDuration();
        }
    }

    // strips usual prefixes and replaces dots to underscores
    private static String clean(final String declaringType) {
        String type;
        if (declaringType.startsWith(P1)) {
            type = declaringType.substring(P1.length());
        } else if (declaringType.startsWith(P2)) {
            type = declaringType.substring(P2.length());
        } else {
            type = declaringType;
        }
        return type.replace('.', '_');
    }

    // returns a prometheus compatible metrics name formatted as "api_[class_method]_calls".
    // skip common non-differentiating package names to get a terse and unique name.
    static String signatureToKeyName(final String declaringType, final String method, final Object[] args) {
        final StringBuilder sb = new StringBuilder(declaringType.length() + 16);
        sb.append("api_").append(clean(declaringType));
        sb.append('_').append(method);
        if (args.length > 0) {
            sb.append("_arg").append(String.valueOf(args.length));
        }
        return sb.append("_calls").toString();
    }
}
