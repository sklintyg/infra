/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.integration.hsa.model.SelectableVardenhet;
import se.inera.intyg.infra.monitoring.MonitoringConfiguration;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.infra.security.common.model.Role;

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

    @Autowired
    LogMDCServletFilter logMDCServletFilter;

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
    public void logEventTest() {

        LOG.error("Hello");
        ArgumentCaptor<Appender> ac = ArgumentCaptor.forClass(Appender.class);

        Mockito.verify(mockedAppender).doAppend(ac.capture());

        assertEquals(1, ac.getAllValues().size());

        LoggingEvent le = (LoggingEvent) ac.getAllValues().get(0);

        assertEquals("Hello", le.getMessage());
        assertEquals("ERROR", le.getLevel().levelStr);
    }

    @Test
    public void logContenContextTest() {
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
    public void logExplicitTraceIdTest() {
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
    public void logImplicitTraceIdTest() {
        logMDCHelper.run(() -> {
            String out = captureStdout(() -> LOG.info(MarkerFilter.MONITORING, "Marker test"));

            String regex = String.format("^.* \\[monitoring,-,([%s)]+)\\,noUser].*$", String.valueOf(LogMDCHelper.BASE62CHARS));
            Matcher m = Pattern.compile(regex).matcher(out);

            assertTrue(m.find());
            assertEquals(LogMDCHelper.IDLEN, m.group(1).length());
        });
    }

    @Test
    public void logMarkerTest() {
        String out = captureStdout(() -> LOG.info(MarkerFilter.MONITORING, "Marker test"));
        assertTrue(out.contains("[monitoring,-,-,noUser]"));
        assertEquals("Monitor appender only should be triggered (1 record)", 1, out.split(System.lineSeparator()).length);
    }

    @Test
    public void logAuthenticatedPrincipalTest() throws IOException {
        Authentication authentication = Mockito.mock(Authentication.class);
        IntygUser intygUser = Mockito.mock(IntygUser.class);
        when(intygUser.getHsaId()).thenReturn("hsaId");
        when(intygUser.getOrigin()).thenReturn("origin");
        when(intygUser.getRoles()).thenReturn(Collections.singletonMap("role", Mockito.mock(Role.class)));

        SelectableVardenhet vg = Mockito.mock(SelectableVardenhet.class);
        when(vg.getId()).thenReturn("vgId");
        when(intygUser.getValdVardgivare()).thenReturn(vg);

        SelectableVardenhet ve = Mockito.mock(SelectableVardenhet.class);
        when(ve.getId()).thenReturn("sevId");
        when(intygUser.getValdVardenhet()).thenReturn(ve);

        when(authentication.getPrincipal()).thenReturn(intygUser);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        Cookie sessionCookie = new Cookie("SESSION", "sessionCookieValue");
        when(mockedRequest.getCookies()).thenReturn(new Cookie[]{new Cookie("test", "test"), sessionCookie});

        Closeable c = logMDCServletFilter.open(mockedRequest);
        String out = captureStdout(() -> LOG.info(MarkerFilter.MONITORING, "Auth Test"));
        assertTrue(out.contains("hsaId,sevId,origin,role,vgId]"));
        assertTrue(out.contains(sessionCookie.getValue()));
        c.close();
    }

}
