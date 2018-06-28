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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;

public class LogMDCHelper {
    static final int IDLEN = 8;
    static final String LOCALHOST = "env.localHost";
    static final String TRACEID = "req.traceId";
    static final char[] BASE62CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    @Value("${log.trace.header:x-trace-id}")
    private String header;

    public LogMDCHelper() {
        MDC.setContextMap(Maps.newHashMap(ImmutableMap.of(LOCALHOST, localHost())));
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
        return openTrace(null);
    }

    /**
     * Opens a trace.
     *
     * @param traceId the trace id to use.
     * @return the trace to close when done.
     */
    public Closeable openTrace(final String traceId) {
        final String localHost = MDC.get(LOCALHOST);
        if (Strings.isNullOrEmpty(localHost)) {
            MDC.put(LOCALHOST, localHost());
        }

        MDC.put(TRACEID, Strings.isNullOrEmpty(traceId) ? traceId(IDLEN) : traceId);

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

    String localHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
        }
        return "unknown";
    }
}
