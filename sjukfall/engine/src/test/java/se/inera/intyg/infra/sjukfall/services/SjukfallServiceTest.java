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
package se.inera.intyg.infra.sjukfall.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.infra.sjukfall.dto.Sjukfall;
import se.inera.intyg.infra.sjukfall.dto.Vardenhet;
import se.inera.intyg.infra.sjukfall.dto.Vardgivare;
import se.inera.intyg.infra.sjukfall.engine.AktivtIntyg;
import se.inera.intyg.infra.sjukfall.engine.AktivtIntygResolver;
import se.inera.intyg.infra.sjukfall.testdata.AktivtIntygGenerator;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;


/**
 * Created by martin on 11/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallServiceTest {
    private static final String LOCATION_INTYGSDATA = "classpath:SjukfallServiceTest/intygsdata-engine.csv";

    private static final String DIAGNOS_KOD = "J1012";

    private static List<IntygData> intygDataList;
    private static List<Sjukfall> sjukfallList;

    private LocalDate activeDate = LocalDate.parse("2016-02-16");
    private LocalDate tolvanBirthdate = LocalDate.parse("1912-12-12");

    @Spy
    private AktivtIntygResolver resolver;

    @InjectMocks
    private SjukfallServiceImplTest testee = new SjukfallServiceImplTest();

    @Before
    public void init() throws IOException {
        // Load test data
        AktivtIntygGenerator generator = new AktivtIntygGenerator(LOCATION_INTYGSDATA);
        intygDataList = generator.generate().get();
        assertInit(intygDataList, 33);

        sjukfallList = testee.beraknaSjukfall(intygDataList, getIntygParametrar(5, activeDate));
        assertInit(sjukfallList, 11);
    }

    // ~ ======================================================================================================== ~
    // ~ Specification för test av sjukfall #1 - #12 finns på URL:
    // ~ https://inera-certificate.atlassian.net/wiki/pages/viewpage.action?pageId=39747618
    // ~ ======================================================================================================== ~

    @Test
    public void testCalculateSjukfall1() {
        assertSjukfall("19791110-9291", "2016-02-01", "2016-02-20", 2, 19);
    }

    @Test
    public void testCalculateSjukfall2() {
        assertSjukfall("19791123-9262", "2016-02-01", "2016-02-20", 2, 19);
    }

    @Test
    public void testCalculateSjukfall3() {
        assertSjukfall("19791212-9280", "2016-02-01", "2016-02-25", 3, 24);
    }

    @Test
    public void testCalculateSjukfall4() {
        assertSjukfall("19800113-9297", "2016-02-01", "2016-02-25", 3, 24);
    }

    @Test
    public void testCalculateSjukfall5() {
        assertSjukfall("19800124-9286", "2016-02-12", "2016-02-25", 2, 14);
    }

    @Test
    public void testCalculateSjukfall6() {
        assertSjukfall("19800207-9294", "2016-02-12", "2016-02-25", 2, 14);
    }

    @Test
    public void testCalculateSjukfall7() {
        assertSjukfall("19800228-9224", "2016-02-01", "2016-02-25", 0, 0);
    }

    @Test
    public void testCalculateSjukfall8() {
        assertSjukfall("19961110-2394", "2016-02-01", "2016-02-19", 3, 15);
    }

    @Test
    public void testCalculateSjukfall9() {
        assertSjukfall("19961111-2385", "2016-02-15", "2016-03-04", 3, 15);
    }

    @Test
    public void testCalculateSjukfall10() {
        assertSjukfall("19571109-2642", "2016-02-15", "2016-02-19", 1, 5);
    }

    @Test
    public void testCalculateSjukfall11() {
        assertSjukfall("19630206-2846", "2016-02-01", "2016-03-04", 4, 29);
    }

    @Test
    public void testCalculateSjukfall12() {
        assertSjukfall("19710301-1032", "2016-02-15", "2016-03-04", 3, 19);
    }

    @Test
    public void testDiagnos() {
        String fullstandigtNamn = "Anders Andersson";
        String id = "19121212-1212";

        IntygData intyg = new IntygData();
        intyg.setPatientId(id);
        intyg.setPatientNamn(fullstandigtNamn);
        intyg.setDiagnosKod(DIAGNOS_KOD);

        DiagnosKod diagnosKod = testee.getDiagnosKod(intyg);

        assertEquals(DIAGNOS_KOD, diagnosKod.getCode());
    }


    // - - -  Private scope  - - -

    private static void assertInit(List<?> list, int expectedListSize) {
        assertTrue("Expected " + expectedListSize + " but was + " + list.size(), list.size() == expectedListSize);
    }

    private static void assertSjukfall(String patientId, String startDatum, String slutDatum, int antalIntyg, int effektivSjukskrivningslangd) {
        Sjukfall sjukfall = sjukfallList.stream().
                filter(o -> o.getPatient().getId().equals(patientId)).findFirst().orElse(null);

        if (antalIntyg == 0) {
            assertNull(sjukfall);
            return;
        }

        assertTrue(sjukfall.getStart().isEqual(LocalDate.parse(startDatum)));
        assertTrue(sjukfall.getSlut().isEqual(LocalDate.parse(slutDatum)));
        assertTrue(sjukfall.getIntyg() == antalIntyg);
        assertTrue(sjukfall.getDagar() == effektivSjukskrivningslangd);
    }

    private IntygParametrar getIntygParametrar(int maxIntygsGlapp, LocalDate aktivtDatum) {
        IntygParametrar parametrar = new IntygParametrar();
        parametrar.setMaxIntygsGlapp(maxIntygsGlapp);
        parametrar.setAktivtDatum(aktivtDatum);
        return parametrar;
    }

    private class SjukfallServiceImplTest extends SjukfallEngineServiceImpl {
        public SjukfallServiceImplTest() {
            super();
            // 2016-02-11
            final int date = 1455203622;
            clock = Clock.fixed(Instant.ofEpochSecond(date), ZoneId.of("Europe/Paris"));
        }

        @Override
        Sjukfall buildSjukfall(List<AktivtIntyg> values, AktivtIntyg aktivtIntyg, LocalDate aktivtDatum) {
            Vardgivare vardgivare = new Vardgivare(" IFV1239877878-0000 ", "Webcert-Vårdgivare1");
            Vardenhet vardenhet = new Vardenhet(" IFV1239877878-1045 ", "Webcert-Enhet2");
            Lakare lakare = new Lakare(aktivtIntyg.getLakareId(), aktivtIntyg.getLakareNamn());

            Sjukfall sjukfall = super.buildSjukfall(values, aktivtIntyg, aktivtDatum);
            sjukfall.setVardgivare(vardgivare);
            sjukfall.setVardenhet(vardenhet);
            sjukfall.setLakare(lakare);
            return sjukfall;
        }
    }
}
