/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.monitoring.MonitoringConfiguration;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MonitoringConfiguration.class})
public class MarkerFilterTest {

    @Test
    public void single_marker() {
        MarkerFilter mf = new MarkerFilter();
        mf.setMarker("Monitoring");

        assertTrue(mf.markersToMatch.contains(MarkerFilter.MONITORING));
    }

    @Test
    public void multiple_markers() {
        MarkerFilter mf = new MarkerFilter();
        mf.setMarkers("Monitoring, Validation");

        assertEquals(2, mf.markersToMatch.size());
        assertTrue(mf.markersToMatch.contains(MarkerFilter.MONITORING));
        assertTrue(mf.markersToMatch.contains(MarkerFilter.VALIDATION));

    }


    @Test
    public void event_accept() {
        ILoggingEvent event = Mockito.mock(ILoggingEvent.class);

        Mockito.when(event.getMarker()).thenReturn(MarkerFilter.MONITORING);
        MarkerFilter mf = new MarkerFilter();
        mf.setMarkers("Monitoring, Validation");
        mf.setOnMatch(FilterReply.ACCEPT);
        mf.start();

        assertEquals(FilterReply.ACCEPT, mf.decide(event));
    }

    @Test
    public void event_deny() {
        ILoggingEvent event = Mockito.mock(ILoggingEvent.class);

        Mockito.when(event.getMarker()).thenReturn(MarkerFilter.VALIDATION);
        MarkerFilter mf = new MarkerFilter();
        mf.setMarkers("Monitoring");
        mf.setOnMatch(FilterReply.ACCEPT);
        mf.setOnMismatch(FilterReply.DENY);
        mf.start();

        assertEquals(FilterReply.DENY, mf.decide(event));
    }
}
