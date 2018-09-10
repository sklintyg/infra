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
package se.inera.intyg.infra.monitoring.logging;

import java.io.Closeable;
import java.nio.CharBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.base.Strings;

public class LogMDCHelper {
    static final int IDLEN = 8;
    static final String TRACEID = "req.traceId";
    static final String SESSIONINFO = "req.sessionInfo";
    static final char[] BASE62CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    static final LogMDCRequestInfo EMPTY_REQUEST_INFO = new LogMDCRequestInfo() {
        @Override
        public String getTraceId() {
            return null;
        }

        @Override
        public String getSessionInfo() {
            return null;
        }
    };

    /**
     * Enables an app to define logging tags such as trace-id and session info.
     */
    public interface LogMDCRequestInfo {
        String getTraceId();
        String getSessionInfo();
    }

    @Autowired(required = false)
    LogMDCRequestInfo logMDCRequestInfo;

    @Value("${log.trace.header:x-trace-id}")
    String header;

    @PostConstruct
    void postConstruct() {
        if (logMDCRequestInfo == null) {
            logMDCRequestInfo = EMPTY_REQUEST_INFO;
        }
    }

    /**
     * Returns the trace HTTP header name.
     */
    public String traceHeader() {
        return this.header;
    }

    /**
     * Opens a trace.
     *
     * @return the trace to close when done.
     */
    public Closeable openTrace() {
        return openTrace(logMDCRequestInfo);
    }

    /**
     * Returns if an explicitly defined info bean exists.
     */
    boolean isCustomized() {
        return (logMDCRequestInfo != EMPTY_REQUEST_INFO);
    }

    /**
     * Opens a trace.
     *
     * @param requestInfo the request info to use.
     * @return the trace to close when done.
     */
    public Closeable openTrace(final LogMDCRequestInfo requestInfo) {
        final String traceId = Strings.isNullOrEmpty(requestInfo.getTraceId()) ? traceId(IDLEN) : requestInfo.getTraceId();
        MDC.put(TRACEID, traceId);
        if (requestInfo.getSessionInfo() != null) {
            MDC.put(SESSIONINFO, requestInfo.getSessionInfo());
        }

        return () -> closeTrace();
    }

    /**
     * Runs a code block with an unique trace.
     */
    public void run(final Runnable runnable) {
        final Closeable trace = openTrace();
        try {
            runnable.run();
        } finally {
            IOUtils.closeQuietly(trace);
        }
    }

    // Clean-up.
    void closeTrace() {
        MDC.remove(TRACEID);
        MDC.remove(SESSIONINFO);
    }

    /**
     * Returns a trace id.
     *
     * @param len the length to generate.
     * @return the trace id.
     */
    static final String traceId(final int len) {
        final CharBuffer charBuffer = CharBuffer.allocate(len);
        IntStream.generate(() -> ThreadLocalRandom.current().nextInt(BASE62CHARS.length))
                .limit(len)
                .forEach(value -> charBuffer.append(BASE62CHARS[value]));
        return charBuffer.rewind().toString();
    }
}
