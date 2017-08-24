/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.srs.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.schemas.contract.Personnummer;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:SrsServiceTest/test-context.xml")
public class SrsServiceTest {

    @Autowired
    private SrsService service;

    private Utdatafilter utdatafilter;

    @Before
    public void setup() {
        utdatafilter = new Utdatafilter();
        utdatafilter.setPrediktion(false);
        utdatafilter.setAtgardsrekommendation(false);
        utdatafilter.setStatistik(false);
        utdatafilter.setFmbinformation(false);
    }

    @Test
    public void testNone() throws Exception {
        SrsResponse response = service.getSrs(new Personnummer("191212121212"), "M18", utdatafilter);
        assertNull(response.getStatistikBild());
        assertNull(response.getAtgarder());
        assertNull(response.getLevel());
    }

    @Test
    public void testSrsPrediktion() throws Exception {
        utdatafilter.setPrediktion(true);
        SrsResponse response = service.getSrs(new Personnummer("191212121212"), "M18", utdatafilter);
        assertNotNull(response);
        assertTrue(response.getLevel() >= 0);
        assertTrue(response.getLevel() < 4);
        assertNull(response.getAtgarder());
    }

    @Test
    public void testSrsStatistik() throws Exception {
        utdatafilter.setStatistik(true);
        SrsResponse response = service.getSrs(new Personnummer("191212121212"), "M18", utdatafilter);
        assertNotNull(response.getStatistikBild());
        assertNull(response.getAtgarder());
        assertNull(response.getLevel());
        assertEquals("http://localhost/images/M18", response.getStatistikBild());
    }

    @Test
    public void testSrsPrediktionAndAtgardRekommendation() throws Exception {
        utdatafilter.setPrediktion(true);
        utdatafilter.setAtgardsrekommendation(true);
        SrsResponse response = service.getSrs(new Personnummer("191212121212"), "M18", utdatafilter);
        assertNotNull(response);
        assertTrue(response.getLevel() >= 0);
        assertTrue(response.getLevel() < 4);
        assertNotNull(response.getAtgarder().get(0));
        assertNotNull(response.getAtgarder().get(1));
        assertNotNull(response.getAtgarder().get(2));
    }

    @Test
    public void testSrsAll() throws Exception {
        utdatafilter.setAtgardsrekommendation(true);
        utdatafilter.setPrediktion(true);
        utdatafilter.setStatistik(true);
        utdatafilter.setFmbinformation(true);
        SrsResponse response = service.getSrs(new Personnummer("191212121212"), "M18", utdatafilter);
        assertNotNull(response);
        assertTrue(response.getLevel() >= 0);
        assertTrue(response.getLevel() < 4);
        assertNotNull(response.getAtgarder().get(0));
        assertNotNull(response.getAtgarder().get(1));
        assertNotNull(response.getAtgarder().get(2));
        assertNotNull(response.getStatistikBild());
        assertEquals("http://localhost/images/M18", response.getStatistikBild());
    }

}
