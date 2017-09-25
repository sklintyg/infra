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
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.srs.model.SrsQuestion;
import se.inera.intyg.infra.integration.srs.model.SrsQuestionResponse;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.infra.integration.srs.stub.GetPredictionQuestionsStub;
import se.inera.intyg.infra.integration.srs.stub.repository.ConsentRepository;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.util.Arrays;
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
    }

    @Test
    public void testNone() throws Exception {
        SrsResponse response = service.getSrs(createUser(), "intygId", new Personnummer("191212121212"), "M18", utdatafilter,
                Arrays.asList(SrsQuestionResponse.create("questionId", "answerId")));
        assertNull(response.getStatistikBild());
        assertNull(response.getAtgarderObs());
        assertNull(response.getAtgarderRek());
        assertNull(response.getPredictionLevel());
        assertNull(response.getPredictionDescription());
    }

    @Test
    public void testSrsPrediktion() throws Exception {
        utdatafilter.setPrediktion(true);
        SrsResponse response = service.getSrs(createUser(), "intygId", new Personnummer("191212121212"), "M18", utdatafilter,
                Arrays.asList(SrsQuestionResponse.create("questionId", "answerId")));
        assertNotNull(response);
        assertEquals(new Integer(1), response.getPredictionLevel());
        assertEquals("test", response.getPredictionDescription());
        assertNull(response.getAtgarderRek());
        assertNull(response.getAtgarderObs());
    }

    @Test
    public void testSrsStatistik() throws Exception {
        utdatafilter.setStatistik(true);
        SrsResponse response = service.getSrs(createUser(), "intygId", new Personnummer("191212121212"), "M18", utdatafilter,
                Arrays.asList(SrsQuestionResponse.create("questionId", "answerId")));
        assertNotNull(response.getStatistikBild());
        assertNull(response.getAtgarderRek());
        assertNull(response.getAtgarderObs());
        assertNull(response.getPredictionLevel());
        assertNull(response.getPredictionDescription());
    }

    @Test
    public void testSrsPrediktionAndAtgardRekommendation() throws Exception {
        utdatafilter.setPrediktion(true);
        utdatafilter.setAtgardsrekommendation(true);
        SrsResponse response = service.getSrs(createUser(), "intygId", new Personnummer("191212121212"), "M18", utdatafilter,
                Arrays.asList(SrsQuestionResponse.create("questionId", "answerId")));
        assertNotNull(response);
        assertEquals(new Integer(1), response.getPredictionLevel());
        assertEquals("test", response.getPredictionDescription());
        assertNotNull(response.getAtgarderRek().get(0));
        assertNotNull(response.getAtgarderRek().get(1));
        assertNotNull(response.getAtgarderRek().get(2));
    }

    @Test
    public void testSrsAll() throws Exception {
        utdatafilter.setAtgardsrekommendation(true);
        utdatafilter.setPrediktion(true);
        utdatafilter.setStatistik(true);
        SrsResponse response = service.getSrs(createUser(), "intygId", new Personnummer("191212121212"), "M18", utdatafilter,
                Arrays.asList(SrsQuestionResponse.create("questionId", "answerId")));
        assertNotNull(response);
        assertEquals(new Integer(1), response.getPredictionLevel());
        assertEquals("test", response.getPredictionDescription());

        assertEquals(3, response.getAtgarderRek().size());
        assertNotNull(response.getAtgarderRek().get(0));
        assertEquals("Atgardsforslag REK 1", response.getAtgarderRek().get(0));
        assertEquals("Atgardsforslag REK 2", response.getAtgarderRek().get(1));
        assertEquals("Atgardsforslag REK 3", response.getAtgarderRek().get(2));

        assertEquals(3, response.getAtgarderObs().size());
        assertNotNull(response.getAtgarderObs().get(0));
        assertEquals("Atgardsforslag OBS 1", response.getAtgarderObs().get(0));
        assertEquals("Atgardsforslag OBS 2", response.getAtgarderObs().get(1));
        assertEquals("Atgardsforslag OBS 3", response.getAtgarderObs().get(2));
        assertNotNull(response.getStatistikBild());
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

    @Test
    public void testGetDiagnosisCodes() {
        List<String> response = service.getAllDiagnosisCodes();
        assertNotNull(response);
        assertEquals(3, response.size());
        assertTrue(response.contains("M18"));
        assertTrue(response.contains("J20"));
        assertTrue(response.contains("Q10"));
    }

    private IntygUser createUser() {
        IntygUser user = new IntygUser("hsaId");
        Vardgivare vg = new Vardgivare();
        vg.setId("vgId");
        user.setValdVardgivare(vg);
        Vardenhet ve = new Vardenhet();
        ve.setId("veId");
        user.setValdVardenhet(ve);
        return user;
    }
}
