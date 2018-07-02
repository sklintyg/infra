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

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import io.prometheus.client.Summary;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Aspect("pertarget(se.inera.intyg.infra.monitoring.annotation.MethodTimer.timeable())")
@Scope("prototype")
@ControllerAdvice
public class MethodTimer {
    private final ReadWriteLock summaryLock = new ReentrantReadWriteLock();
    private final HashMap<String, Summary> summaries = new HashMap<>();

    private static CharMatcher keyReplaceMatcher =  CharMatcher.anyOf(" (),.");

    @Pointcut("@annotation(se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod)")
    public void annotatedMethod() {

    }

    @Pointcut("annotatedMethod()")
    public void timeable() {

    }

    private PrometheusTimeMethod getAnnotation(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        assert (pjp.getSignature() instanceof MethodSignature);
        MethodSignature signature = (MethodSignature) pjp.getSignature();

        PrometheusTimeMethod annot = AnnotationUtils.findAnnotation(pjp.getTarget().getClass(), PrometheusTimeMethod.class);
        if (annot != null) {
            return annot;
        }

        // When target is an AOP interface proxy but annotation is on class method (rather than Interface method).
        final String name = signature.getName();
        final Class[] parameterTypes = signature.getParameterTypes();

        return AnnotationUtils.findAnnotation(pjp.getTarget().getClass().getDeclaredMethod(name, parameterTypes),
                PrometheusTimeMethod.class);
    }

    private Summary ensureSummary(ProceedingJoinPoint pjp, String key) throws IllegalStateException {
        PrometheusTimeMethod annot;
        try {
            annot = getAnnotation(pjp);
        } catch (NoSuchMethodException | NullPointerException e) {
            throw new IllegalStateException("Annotation could not be found for pjp \"" + pjp.toShortString() + "\"", e);
        }

        assert (annot != null);

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
        final String key = keyReplaceMatcher.replaceFrom(pjp.getSignature().toString(), "_");

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
}
