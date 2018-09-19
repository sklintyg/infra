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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import se.inera.intyg.infra.monitoring.MonitoringConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MonitoringConfiguration.class})
public class LogbackTest {
    static Logger LOG = LoggerFactory.getLogger(LogbackTest.class);

    @Autowired
    LogMDCHelper logMDCHelper;

    Appender mockedAppender = Mockito.mock(Appender.class);

    public LogbackTest() {
        ((ch.qos.logback.classic.Logger) LOG).addAppender(mockedAppender);
    }

    // returns stdout as a string (default encoding)
    String captureStdout(final Runnable runnable) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintStream old = System.out;
        System.setOut(new PrintStream(out));
        try {
            runnable.run();
        } finally {
            System.out.flush();
            System.setOut(old);
        }
        return new String(out.toByteArray());
    }

    @Test
    public void log_event() {

        LOG.error("Hello");
        ArgumentCaptor<Appender> ac = ArgumentCaptor.forClass(Appender.class);

        Mockito.verify(mockedAppender).doAppend(ac.capture());

        assertEquals(1, ac.getAllValues().size());

        LoggingEvent le = (LoggingEvent) ac.getAllValues().get(0);

        assertEquals("Hello", le.getMessage());
        assertEquals("ERROR", le.getLevel().levelStr);
    }

    @Test
    public void log_content_context() {
        String out = captureStdout(() -> LOG.info("Hello"));

        ArgumentCaptor<Appender> ac = ArgumentCaptor.forClass(Appender.class);
        Mockito.verify(mockedAppender).doAppend(ac.capture());
        LoggingEvent le = (LoggingEvent) ac.getAllValues().get(0);

        assertEquals("Process and console appender should be triggered (2 records)", 2, out.split(System.lineSeparator()).length);
        assertTrue(out.contains("[process,-,"));
        assertTrue(out.contains("[console,-,"));
        assertTrue(out.endsWith(": Hello" + System.lineSeparator()));
    }

    @Test
    public void log_with_explicit_request_info() {
        final String traceId = logMDCHelper.traceHeader();
        final String sessionInfo = "NO SESSION";
        Closeable trace = logMDCHelper.withSessionInfo(sessionInfo).withTraceId(traceId).openTrace();
        try {
            String out = captureStdout(() -> LOG.info("Hello"));
            assertTrue(out.contains("[process," + sessionInfo + "," + traceId));
        } finally {
            IOUtils.closeQuietly(trace);
        }
    }

   @Test
   public void log_with_lambda_implicit_trace_id() {
        logMDCHelper.run(() -> {
            String out = captureStdout(() -> LOG.info(MarkerFilter.MONITORING, "Marker test"));

            String regex = String.format("^.* \\[monitoring,-,([%s)]+)\\].*$", String.valueOf(LogMDCHelper.BASE62CHARS));
            Matcher m = Pattern.compile(regex).matcher(out);

            assertTrue(m.find());
            assertEquals(LogMDCHelper.IDLEN, m.group(1).length());
        });
   }

    @Test
    public void log_marker() {
        String out = captureStdout(() -> LOG.info(MarkerFilter.MONITORING, "Marker test"));
        assertTrue(out.contains("[monitoring,-,-]"));
        assertEquals("Monitor appender only should be triggered (1 record)", 1, out.split(System.lineSeparator()).length);
    }
}
