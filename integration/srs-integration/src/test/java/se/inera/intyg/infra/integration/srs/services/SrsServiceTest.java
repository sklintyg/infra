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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.infra.integration.srs.model.SjukskrivningsGrad;
import se.inera.intyg.infra.integration.srs.model.SrsQuestion;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.infra.integration.srs.stub.GetConsentStub;
import se.inera.intyg.infra.integration.srs.stub.GetPredictionQuestionsStub;
import se.inera.intyg.infra.integration.srs.stub.repository.ConsentRepository;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:SrsServiceTest/test-context.xml")
public class SrsServiceTest {

    @Autowired
    private SrsService service;

    @Autowired
    private ConsentRepository consentRepository;

    private Utdatafilter utdatafilter;

    @Before
    public void setup() {
        consentRepository.clear();
        utdatafilter = new Utdatafilter();
        utdatafilter.setPrediktion(false);
        utdatafilter.setAtgardsrekommendation(false);
        utdatafilter.setStatistik(false);
        utdatafilter.setFmbinformation(false);
    }

    @Test
    public void testNone() throws Exception {
        SrsResponse response = service.getSrs("intygId", new Personnummer("191212121212"), "M18", utdatafilter, Collections.emptyList(),
                SjukskrivningsGrad.HELT_NEDSATT);
        assertNull(response.getStatistikBild());
        assertNull(response.getAtgarder());
        assertNull(response.getLevel());
    }

    @Test
    public void testSrsPrediktion() throws Exception {
        utdatafilter.setPrediktion(true);
        SrsResponse response = service.getSrs("intygId", new Personnummer("191212121212"), "M18", utdatafilter, Collections.emptyList(),
                SjukskrivningsGrad.HELT_NEDSATT);
        assertNotNull(response);
        assertTrue(response.getLevel() >= 0);
        assertTrue(response.getLevel() < 4);
        assertNull(response.getAtgarder());
    }

    @Test
    public void testSrsStatistik() throws Exception {
        utdatafilter.setStatistik(true);
        SrsResponse response = service.getSrs("intygId", new Personnummer("191212121212"), "M18", utdatafilter, Collections.emptyList(),
                SjukskrivningsGrad.HELT_NEDSATT);
        assertNotNull(response.getStatistikBild());
        assertNull(response.getAtgarder());
        assertNull(response.getLevel());
        assertEquals("http://localhost/images/M18", response.getStatistikBild());
    }

    @Test
    public void testSrsPrediktionAndAtgardRekommendation() throws Exception {
        utdatafilter.setPrediktion(true);
        utdatafilter.setAtgardsrekommendation(true);
        SrsResponse response = service.getSrs("intygId", new Personnummer("191212121212"), "M18", utdatafilter, Collections.emptyList(),
                SjukskrivningsGrad.HELT_NEDSATT);
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
        SrsResponse response = service.getSrs("intygId", new Personnummer("191212121212"), "M18", utdatafilter, Collections.emptyList(),
                SjukskrivningsGrad.HELT_NEDSATT);
        assertNotNull(response);
        assertTrue(response.getLevel() >= 0);
        assertTrue(response.getLevel() < 4);
        assertNotNull(response.getAtgarder().get(0));
        assertNotNull(response.getAtgarder().get(1));
        assertNotNull(response.getAtgarder().get(2));
        assertNotNull(response.getStatistikBild());
        assertEquals("http://localhost/images/M18", response.getStatistikBild());
    }

    @Test
    public void testGetQuestions() {
        // Use reflection to spy on the stub to make sure we are using the correct request
        GetPredictionQuestionsResponderInterface spy = Mockito.spy(new GetPredictionQuestionsStub());
        ReflectionTestUtils.setField(service, "getPrediction", spy);

        final String diagnosisCode = "diagnosisCode";
        List<SrsQuestion> response = service.getQuestions(diagnosisCode);
        assertFalse(CollectionUtils.isEmpty(response));

        ArgumentCaptor<GetPredictionQuestionsRequestType> captor = ArgumentCaptor.forClass(GetPredictionQuestionsRequestType.class);

        verify(spy).getPredictionQuestions(captor.capture());
        verifyNoMoreInteractions(spy);

        assertEquals(diagnosisCode, captor.getValue().getDiagnos().getCode());
        assertEquals("1.2.752.116.1.1.1.1.3", captor.getValue().getDiagnos().getCodeSystem());
    }

    @Test
    public void testGetConsent() throws InvalidPersonNummerException {
        // Use reflection to spy on the stub to make sure we are using the correct request
        final String hsaId = "hsa";
        final Personnummer persNr = new Personnummer("1912121212");
        Samtyckesstatus response = service.getConsent(hsaId, persNr);
        assertNotNull(response);
        assertEquals(Samtyckesstatus.INGET, response);
    }

    @Test
    public void testSetConsent() throws Exception {
        final String hsaId = "hsa";
        final Personnummer persNr = new Personnummer("1912121212");
        ResultCodeEnum response = service.setConsent(hsaId, persNr, true);
        assertNotNull(response);
        assertEquals(ResultCodeEnum.OK, response);
    }
}
