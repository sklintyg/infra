/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.services;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.integration.hsa.model.AgandeForm;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;
import se.inera.intyg.infra.integration.hsa.stub.HsaPerson;
import se.inera.intyg.infra.integration.hsa.stub.HsaServiceStub;
import se.inera.intyg.infra.integration.hsa.stub.Medarbetaruppdrag;

/**
 * @author andreaskaltenbach
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/HsaOrganizationsServiceTest/test-context.xml")
public class HsaOrganizationsServiceTest {

    private static final String PERSON_HSA_ID = "person-123";

    private static final String CENTRUM_VAST = "centrum-vast";
    private static final String CENTRUM_OST = "centrum-ost";
    private static final String CENTRUM_NORR = "centrum-norr";
    private static final String FORNAMN = "Gunilla";
    private static final String EFTERNAMN = "Gunillasdotter";
    private static final String BEFATTNINGSKOD = "bef-123";
    private static final String FORSKRIVARKOD = "frskrkd-321";
    private static ObjectMapper mapper = new ObjectMapper();

    {
        mapper.findAndRegisterModules();
    }

    @Autowired
    private HsaOrganizationsService service;

    @Autowired
    private HsaServiceStub serviceStub;

    @Before
    public void init() {
        addHosPerson(PERSON_HSA_ID);
    }

    @Test
    public void testEmptyResultSet() {

        Collection<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertTrue(vardgivare.isEmpty());
    }

    @Test
    public void testSingleEnhetWithoutMottagningar() {
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList(CENTRUM_NORR));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        Vardgivare vg = vardgivare.get(0);
        assertEquals("vastmanland", vg.getId());
        assertEquals("Landstinget Västmanland", vg.getNamn());
        assertEquals(1, vg.getVardenheter().size());

        Vardenhet enhet = vg.getVardenheter().get(0);
        assertEquals("centrum-norr", enhet.getId());
        assertEquals(AgandeForm.PRIVAT, enhet.getAgandeForm());
        assertEquals("Vårdcentrum i Norr", enhet.getNamn());
        assertTrue(enhet.getMottagningar().isEmpty());
        assertEquals("arbetsplatskod_centrum-norr", enhet.getArbetsplatskod());
    }

    @Test
    public void ifEnhetHasNoArbetsplatskodThenDefaultShouldBeAssumed() {
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList(CENTRUM_VAST));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        Vardgivare vg = vardgivare.get(0);
        Vardenhet enhet = vg.getVardenheter().get(0);
        assertEquals("0000000", enhet.getArbetsplatskod());
    }

    @Test
    public void fetchArbetsplatskod() {
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList(CENTRUM_NORR));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        Vardgivare vg = vardgivare.get(0);
        Vardenhet enhet = vg.getVardenheter().get(0);
        assertEquals("arbetsplatskod_centrum-norr", enhet.getArbetsplatskod());
    }

    @Test
    public void isPrivateForPrivateUnit() {
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList(CENTRUM_NORR));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        Vardgivare vg = vardgivare.get(0);
        Vardenhet enhet = vg.getVardenheter().get(0);
        assertEquals(AgandeForm.PRIVAT, enhet.getAgandeForm());
    }

    @Test
    public void isPrivateForNonPrivateUnit() {
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList(CENTRUM_VAST));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        Vardgivare vg = vardgivare.get(0);
        Vardenhet enhet = vg.getVardenheter().get(0);
        assertEquals(AgandeForm.OFFENTLIG, enhet.getAgandeForm());
    }

    @Test
    public void testMultipleEnheter() {

        // Load with 3 MIUs belonging to the same vårdgivare
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList(CENTRUM_VAST, CENTRUM_OST, CENTRUM_NORR));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        Vardgivare vg = vardgivare.get(0);
        assertEquals(3, vg.getVardenheter().size());

        // Assert that mottagningar was loaded for 'centrum-ost'
        Vardenhet centrumOst = getVardenhetById(CENTRUM_OST, vg.getVardenheter());
        assertEquals(1, centrumOst.getMottagningar().size());

        // Assert that vårdenheter is sorted alphabetically
        List<String> correct = Arrays.asList("Vårdcentrum i Norr", "Vårdcentrum i Väst", "Vårdcentrum i Öst");

        List<String> vardenheterNames = new ArrayList<String>();
        for (Vardenhet ve : vg.getVardenheter()) {
            vardenheterNames.add(ve.getNamn());
        }

        assertThat(vardenheterNames, is(correct));

    }

    private Vardenhet getVardenhetById(final String vardenhetId, List<Vardenhet> vardenheter) {

        Predicate<Vardenhet> pred = new Predicate<Vardenhet>() {
            public boolean apply(Vardenhet v) {
                return vardenhetId.equalsIgnoreCase(v.getId());
            }
        };

        Optional<Vardenhet> results = Iterables.tryFind(vardenheter, pred);

        return (results.isPresent()) ? results.get() : null;
    }

    @Test
    public void testMultipleVardgivare() throws IOException {

        // Load with another vardgivare, which gives two vardgivare available
        addVardgivare("HsaOrganizationsServiceTest/landstinget-ostmanland.json");

        // Assign Gunilla one MIU from each vardgivare
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList(CENTRUM_NORR));
        addMedarbetaruppdrag(PERSON_HSA_ID, "ostmanland", asList("vardcentrum-1"));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();

        assertEquals(2, vardgivare.size());

        // Assert that vardgivare is sorted alphabetically
        List<String> correct = Arrays.asList("Landstinget Västmanland", "Landstinget Östmanland");

        List<String> vardgivareNames = new ArrayList<String>();
        for (Vardgivare vg : vardgivare) {
            vardgivareNames.add(vg.getNamn());
        }

        assertThat(vardgivareNames, is(correct));
    }

    private void addMedarbetaruppdrag(String hsaId, String vardgivare, List<String> enhetIds) {
        List<Medarbetaruppdrag.Uppdrag> uppdrag = new ArrayList<>();
        for (String enhet : enhetIds) {
            uppdrag.add(new Medarbetaruppdrag.Uppdrag(vardgivare, enhet));
        }
        serviceStub.getMedarbetaruppdrag().add(new Medarbetaruppdrag(hsaId, uppdrag));
    }

    private void addHosPerson(String hsaId) {
        HsaPerson hsaPerson = new HsaPerson(hsaId, FORNAMN, EFTERNAMN);
        hsaPerson.setBefattningsKod(BEFATTNINGSKOD);
        hsaPerson.setForskrivarKod(FORSKRIVARKOD);

        serviceStub.addHsaPerson(hsaPerson);
    }

    private void addVardgivare(String file) throws IOException {
        Vardgivare vardgivare = mapper.readValue(new ClassPathResource(file).getFile(), Vardgivare.class);
        serviceStub.getVardgivare().add(vardgivare);
    }

    @Before
    public void setupVardgivare() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
    }

    @After
    public void cleanupServiceStub() {
        serviceStub.getVardgivare().clear();
        serviceStub.getMedarbetaruppdrag().clear();
    }

    @Test
    public void testInactiveEnhetFiltering() throws IOException {

        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");

        // Assign Gunilla 5 MIUs where 2 is inactive (finito and futuro)
        addMedarbetaruppdrag(PERSON_HSA_ID, "upp-och-ner", asList("finito", "here-and-now", "futuro", "still-open", "will-shutdown"));

        List<Vardgivare> vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivareList.size());
        assertEquals("upp-och-ner", vardgivareList.get(0).getId());
        assertEquals(3, vardgivareList.get(0).getVardenheter().size());
    }

    @Test
    public void testInactiveEnhetFilteringEmptyVardgivare() throws IOException {

        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");

        // Assign Gunilla 5 MIUs where 2 is inactive (finito and futuro)
        addMedarbetaruppdrag(PERSON_HSA_ID, "upp-och-ner", asList("finito", "futuro"));

        List<Vardgivare> vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(0, vardgivareList.size());
    }

    @Test
    public void testInactiveMottagningFiltering() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");

        addMedarbetaruppdrag(PERSON_HSA_ID, "upp-och-ner", asList("with-subs"));

        List<Vardgivare> vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivareList.size());

        Vardgivare vardgivare = vardgivareList.get(0);
        assertEquals(1, vardgivare.getVardenheter().size());

        List<Mottagning> mottagningar = vardgivare.getVardenheter().get(0).getMottagningar();
        assertEquals(3, mottagningar.size());

        assertEquals("mottagning-here-and-now", mottagningar.get(0).getId());
        assertEquals("mottagning-still-open", mottagningar.get(1).getId());
        assertEquals("mottagning-will-shutdown", mottagningar.get(2).getId());
    }

    @Test
    public void testUppdragFiltering() {

        // user has a different medarbetaruppdrag ändamål 'Animatör' in one enhet
        serviceStub.getMedarbetaruppdrag()
            .add(new Medarbetaruppdrag(PERSON_HSA_ID, asList(new Medarbetaruppdrag.Uppdrag("centrum-ost", "Animatör"))));

        List<Vardgivare> vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();

        // no authorized vardgivere should be returned
        assertTrue(vardgivareList.isEmpty());
    }

    @Test
    public void mottagningListasMenFinnsEj() throws IOException {
        // WEBCERT-749

        addVardgivare("HsaOrganizationsServiceTest/landstinget-inkonsistent.json");

        addMedarbetaruppdrag(PERSON_HSA_ID, "landsting-inkonsistent", asList("enhet1"));
        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();

        assertEquals(1, vardgivare.size());
        assertTrue(vardgivare.get(0).getVardenheter().get(0).getMottagningar().isEmpty());
    }

    @Test
    public void medarbetarUppdragPaEnhetSomInteFinns() throws IOException {
        // WEBCERT-1167
        addMedarbetaruppdrag(PERSON_HSA_ID, "vastmanland", asList("enhet-finns-ej"));
        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(0, vardgivare.size());
    }

    @Test
    public void hamtaVardenhet() {
        Vardenhet vardenhet = service.getVardenhet(CENTRUM_VAST);
        assertNotNull(vardenhet);
        assertEquals(CENTRUM_VAST, vardenhet.getId());
        assertEquals(2, vardenhet.getMottagningar().size());
    }

    @Test
    public void testGetHsaIdForAktivaUnderenheter() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
        List<String> underenheter = service.getHsaIdForAktivaUnderenheter(CENTRUM_VAST);
        assertTrue(underenheter.contains("dialys"));
        assertTrue(underenheter.contains("akuten"));
    }

    @Test
    public void testGetHsaIdForInAktivaUnderenheter() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");
        List<String> underenheter = service.getHsaIdForAktivaUnderenheter("with-subs");
        assertTrue(underenheter.contains("mottagning-here-and-now"));
        assertTrue(underenheter.contains("mottagning-still-open"));
        assertTrue(underenheter.contains("mottagning-will-shutdown"));
        assertFalse(underenheter.contains("mottagning-futuro"));
        assertFalse(underenheter.contains("mottagning-finito"));
    }

    @Test
    public void testGetCareGiverIdForCareUnit() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
        String vardGivareHsaId = service.getVardgivareOfVardenhet("centrum-vast");
        assertEquals("vastmanland", vardGivareHsaId);
    }

    @Test
    public void testGetVardgivareInfo() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
        Vardgivare vg = service.getVardgivareInfo("vastmanland");
        assertEquals("vastmanland", vg.getId());
        assertEquals("Landstinget Västmanland", vg.getNamn());
    }
}
