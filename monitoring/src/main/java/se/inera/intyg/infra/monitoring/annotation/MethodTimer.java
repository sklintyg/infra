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

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;


import java.util.HashMap;
import java.util.HashSet;
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

import com.google.common.base.Strings;

import io.prometheus.client.Summary;

@Aspect("pertarget(se.inera.intyg.infra.monitoring.annotation.MethodTimer.timeable())")
@Scope("prototype")
@ControllerAdvice
public class MethodTimer {
    private final ReadWriteLock summaryLock = new ReentrantReadWriteLock();
    private final HashMap<String, Summary> summaries = new HashMap<>();
    private final HashSet<String> nameSet = new HashSet<>();

    @Pointcut("@annotation(se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod)")
    public void timeable() {
    }

    @Around("timeable()")
    public Object timeMethod(final ProceedingJoinPoint pjp) throws Throwable {

        final Signature signature = pjp.getSignature();
        final String key = signature.toLongString();

        Summary summary = getSummary(key);
        if (summary == null) {
            summary = ensureSummary(pjp, key, toDisplayName(signature));
        }

        final Summary.Timer t = summary.startTimer();
        try {
            return pjp.proceed();
        } finally {
            t.observeDuration();
        }
    }

    //
    Summary getSummary(final String key) {
        final Lock r = summaryLock.readLock();
        r.lock();
        try {
            return summaries.get(key);
        } finally {
            r.unlock();
        }
    }

    //
    PrometheusTimeMethod getAnnotation(final ProceedingJoinPoint pjp) {
        try {
            final Class targetClass = pjp.getTarget().getClass();
            final MethodSignature signature = (MethodSignature) pjp.getSignature();
            PrometheusTimeMethod annotation = findAnnotation(targetClass, PrometheusTimeMethod.class);
            if (annotation == null) {
                // When target is an AOP interface proxy but annotation is on class method (rather than Interface method).
                annotation = findAnnotation(targetClass.getDeclaredMethod(signature.getName(),
                        signature.getParameterTypes()),
                        PrometheusTimeMethod.class);
            }
            return annotation;
        } catch (NoSuchMethodException | NullPointerException e) {
            throw new IllegalStateException("Annotation could not be found for pjp \"" + pjp.toShortString() + "\"", e);
        }
    }

    //
    synchronized Summary ensureSummary(final ProceedingJoinPoint pjp,
                                  final String key,
                                  final String methodDisplayName) throws IllegalStateException {
        Summary summary = summaries.get(key);
        if (summary != null) {
            return summary;
        }

        final PrometheusTimeMethod annotation = getAnnotation(pjp);
        final String name = annotation.name();
        // make sure no duplicates exists
        final String registerName = ensureUniqueName(Strings.isNullOrEmpty(name) ? methodDisplayName : name);

        summary = Summary.build()
                .name(registerName)
                .help(annotation.help())
                .register();

        summaries.put(key, summary);

        return summary;
    }

    //
    String ensureUniqueName(final String startName) {
        int n = 1;
        String name = startName;
        while (nameSet.contains(name)) {
            name = startName + "_" + n++;
        }
        nameSet.add(name);
        return name;
    }

    // Returns a java class dot method name prefixed with api_
    String toDisplayName(final Signature signature) {
        final String cls = signature.getDeclaringTypeName();
        int index = cls.lastIndexOf('.');
        return "api_" + ((index > 0) ? cls.substring(index + 1) : cls) + "_" + signature.getName();
    }
}
