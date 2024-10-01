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
package se.inera.intyg.infra.integration.hsatk.services.legacy;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.inera.intyg.infra.integration.hsatk.model.legacy.AgandeForm;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.hsatk.stub.HsaServiceStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.stub.model.CredentialInformation.Commission;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson.PaTitle;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/HsaOrganizationsServiceTest/test-context.xml")
class HsaOrganizationsServiceTest {

    private static final String PERSON_HSA_ID = "person-123";

    private static final String CENTRUM_VAST = "centrum-vast";
    private static final String CENTRUM_OST = "centrum-ost";
    private static final String CENTRUM_NORR = "centrum-norr";
    private static final String FORNAMN = "Gunilla";
    private static final String EFTERNAMN = "Gunillasdotter";
    private static final String BEFATTNINGSKOD = "bef-123";
    private static final String FORSKRIVARKOD = "frskrkd-321";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private HsaOrganizationsService service;

    @Autowired
    private HsaServiceStub serviceStub;

    @BeforeEach
    public void init() throws IOException {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        addHosPerson();
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
    }

    @AfterEach
    public void cleanupServiceStub() {
        serviceStub.getCareProvider().clear();
        serviceStub.getCredentialInformation().clear();
        serviceStub.getHsaPerson().clear();
    }

    @Test
    void testEmptyResultSet() {
        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertTrue(vardgivare.isEmpty());
    }

    @Test
    void testSingleEnhetWithoutMottagningar() {
        addMedarbetaruppdrag("vastmanland", List.of(CENTRUM_NORR));

        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        final var vg = vardgivare.get(0);
        assertEquals("vastmanland", vg.getId());
        assertEquals("Landstinget Västmanland", vg.getNamn());
        assertEquals(1, vg.getVardenheter().size());

        final var enhet = vg.getVardenheter().get(0);
        assertEquals("centrum-norr", enhet.getId());
        assertEquals(AgandeForm.PRIVAT, enhet.getAgandeForm());
        assertEquals("Vårdcentrum i Norr", enhet.getNamn());
        assertTrue(enhet.getMottagningar().isEmpty());
        assertEquals("arbetsplatskod_centrum-norr", enhet.getArbetsplatskod());
    }

    @Test
    void ifEnhetHasNoArbetsplatskodThenDefaultShouldBeAssumed() {
        addMedarbetaruppdrag("vastmanland", List.of(CENTRUM_VAST));

        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        final var vg = vardgivare.get(0);
        final var enhet = vg.getVardenheter().get(0);
        assertEquals("0000000", enhet.getArbetsplatskod());
    }

    @Test
    void fetchArbetsplatskod() {
        addMedarbetaruppdrag("vastmanland", List.of(CENTRUM_NORR));

        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        final var vg = vardgivare.get(0);
        final var enhet = vg.getVardenheter().get(0);
        assertEquals("arbetsplatskod_centrum-norr", enhet.getArbetsplatskod());
    }

    @Test
    void isPrivateForPrivateUnit() {
        addMedarbetaruppdrag("vastmanland", List.of(CENTRUM_NORR));

        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        final var vg = vardgivare.get(0);
        final var enhet = vg.getVardenheter().get(0);
        assertEquals(AgandeForm.PRIVAT, enhet.getAgandeForm());
    }

    @Test
    void isPrivateForNonPrivateUnit() {
        addMedarbetaruppdrag("vastmanland", List.of(CENTRUM_VAST));

        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        final var vg = vardgivare.get(0);
        final var enhet = vg.getVardenheter().get(0);
        assertEquals(AgandeForm.OFFENTLIG, enhet.getAgandeForm());
    }

    @Test
    void testMultipleEnheter() {

        // Load with 3 MIUs belonging to the same vårdgivare
        addMedarbetaruppdrag("vastmanland", asList(CENTRUM_VAST, CENTRUM_OST, CENTRUM_NORR));

        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivare.size());

        final var vg = vardgivare.get(0);
        assertEquals(3, vg.getVardenheter().size());

        // Assert that mottagningar was loaded for 'centrum-ost'
        final var centrumOst = getVardenhetById(vg.getVardenheter());
        assert centrumOst != null;
        assertEquals(1, centrumOst.getMottagningar().size());

        // Assert that vårdenheter is sorted alphabetically
        final var correct = List.of("Vårdcentrum i Norr", "Vårdcentrum i Väst", "Vårdcentrum i Öst");

        final var vardenheterNames = new ArrayList<>();
        for (Vardenhet ve : vg.getVardenheter()) {
            vardenheterNames.add(ve.getNamn());
        }

        assertEquals(correct, vardenheterNames);

    }

    @Test
    void testMultipleVardgivare() throws IOException {

        // Load with another vardgivare, which gives two vardgivare available
        addVardgivare("HsaOrganizationsServiceTest/landstinget-ostmanland.json");

        // Assign Gunilla one MIU from each vardgivare
        addMedarbetaruppdrag("vastmanland", List.of(CENTRUM_NORR));
        addMedarbetaruppdrag("ostmanland", List.of("vardcentrum-1"));

        List<Vardgivare> vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();

        assertEquals(2, vardgivare.size());

        // Assert that vardgivare is sorted alphabetically
        final var correct = asList("Landstinget Västmanland", "Landstinget Östmanland");

        final var vardgivareNames = new ArrayList<String>();
        for (Vardgivare vg : vardgivare) {
            vardgivareNames.add(vg.getNamn());
        }

        assertEquals(correct, vardgivareNames);
    }

    private void addMedarbetaruppdrag(String hsaId, String vardgivare, List<String> enhetIds) {
        List<CredentialInformation.Commission> uppdrag = new ArrayList<>();
        for (String enhet : enhetIds) {
            CredentialInformation.Commission commission = new CredentialInformation
                .Commission(enhet, CredentialInformation.VARD_OCH_BEHANDLING);
            commission.setHealthCareProviderHsaId(vardgivare);
            uppdrag.add(commission);
        }
        serviceStub.updateCredentialInformation(new CredentialInformation(hsaId, uppdrag));
    }

    private void addHosPerson(String hsaId) {
        HsaPerson hsaPerson = new HsaPerson(hsaId, FORNAMN, EFTERNAMN);
        HsaPerson.PaTitle paTitle = new HsaPerson.PaTitle();
        paTitle.setTitleCode(BEFATTNINGSKOD);
        paTitle.setTitleName(BEFATTNINGSKOD);
        hsaPerson.setPaTitle(Arrays.asList(paTitle));
        hsaPerson.setPersonalPrescriptionCode(FORSKRIVARKOD);

        serviceStub.addHsaPerson(hsaPerson);
    }

    private void addVardgivare(String file) throws IOException {
        CareProviderStub careProviderStub = mapper.readValue(new ClassPathResource(file).getFile(), CareProviderStub.class);
        serviceStub.addCareProvider(careProviderStub);
    }

    @Before
    public void setupVardgivare() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
    }

    @After
    public void cleanupServiceStub() {
        serviceStub.getCareProvider().clear();
        serviceStub.getCredentialInformation().clear();
        serviceStub.getHsaPerson().clear();
    }

    @Test
    void testInactiveEnhetFiltering() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");

        // Assign Gunilla 5 MIUs where 2 is inactive (finito and futuro)
        addMedarbetaruppdrag("upp-och-ner", asList("finito", "here-and-now", "futuro", "still-open", "will-shutdown"));

        final var vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivareList.size());
        assertEquals("upp-och-ner", vardgivareList.get(0).getId());
        assertEquals(3, vardgivareList.get(0).getVardenheter().size());
    }

    @Test
    void testInactiveEnhetFilteringEmptyVardgivare() throws IOException {

        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");

        // Assign Gunilla 5 MIUs where 2 is inactive (finito and futuro)
        addMedarbetaruppdrag("upp-och-ner", asList("finito", "futuro"));

        final var vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(0, vardgivareList.size());
    }

    @Test
    void testInactiveMottagningFiltering() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");

        addMedarbetaruppdrag("upp-och-ner", List.of("with-subs"));

        final var vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(1, vardgivareList.size());

        final var vardgivare = vardgivareList.get(0);
        assertEquals(1, vardgivare.getVardenheter().size());

        final var mottagningar = vardgivare.getVardenheter().get(0).getMottagningar();
        assertEquals(3, mottagningar.size());

        assertEquals("mottagning-here-and-now", mottagningar.get(0).getId());
        assertEquals("mottagning-still-open", mottagningar.get(1).getId());
        assertEquals("mottagning-will-shutdown", mottagningar.get(2).getId());
    }

    @Test
    void testUppdragFiltering() {

        // user has a different medarbetaruppdrag ändamål 'Animatör' in one enhet
        serviceStub.addCredentialInformation(new CredentialInformation(PERSON_HSA_ID,
            asList(new CredentialInformation.Commission("centrum-ost", "Animatör"))));

        final var vardgivareList = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();

        // no authorized vardgivere should be returned
        assertTrue(vardgivareList.isEmpty());
    }

    @Test
    void mottagningListasMenFinnsEj() throws IOException {
        // WEBCERT-749

        addVardgivare("HsaOrganizationsServiceTest/landstinget-inkonsistent.json");
        addMedarbetaruppdrag("landsting-inkonsistent", List.of("enhet1"));
        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();

        assertEquals(1, vardgivare.size());
        assertTrue(vardgivare.get(0).getVardenheter().get(0).getMottagningar().isEmpty());
    }

    @Test
    void medarbetarUppdragPaEnhetSomInteFinns() {
        // WEBCERT-1167
        addMedarbetaruppdrag("vastmanland", List.of("enhet-finns-ej"));
        final var vardgivare = service.getAuthorizedEnheterForHosPerson(PERSON_HSA_ID).getVardgivare();
        assertEquals(0, vardgivare.size());
    }

    @Test
    void hamtaVardenhet() {
        final var vardenhet = service.getVardenhet(CENTRUM_VAST);
        assertNotNull(vardenhet);
        assertEquals(CENTRUM_VAST, vardenhet.getId());
        assertEquals(2, vardenhet.getMottagningar().size());
    }

    @Test
    void testGetHsaIdForAktivaUnderenheter() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
        final var underenheter = service.getHsaIdForAktivaUnderenheter(CENTRUM_VAST);
        assertTrue(underenheter.contains("dialys"));
        assertTrue(underenheter.contains("akuten"));
    }

    @Test
    void testGetHsaIdForInAktivaUnderenheter() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-upp-och-ner.json");
        final var underenheter = service.getHsaIdForAktivaUnderenheter("with-subs");
        assertTrue(underenheter.contains("mottagning-here-and-now"));
        assertTrue(underenheter.contains("mottagning-still-open"));
        assertTrue(underenheter.contains("mottagning-will-shutdown"));
        assertFalse(underenheter.contains("mottagning-futuro"));
        assertFalse(underenheter.contains("mottagning-finito"));
    }

    @Test
    void testGetCareGiverIdForCareUnit() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
        final var vardGivareHsaId = service.getVardgivareOfVardenhet("centrum-vast");
        assertEquals("vastmanland", vardGivareHsaId);
    }

    @Test
    void testGetVardgivareInfo() throws IOException {
        addVardgivare("HsaOrganizationsServiceTest/landstinget-vastmanland.json");
        final var vg = service.getVardgivareInfo("vastmanland");
        assertEquals("vastmanland", vg.getId());
        assertEquals("Landstinget Västmanland", vg.getNamn());
    }

    private Vardenhet getVardenhetById(List<Vardenhet> vardenheter) {
        return vardenheter.stream().filter(v -> CENTRUM_OST.equalsIgnoreCase(v.getId())).findFirst()
            .orElse(null);
    }

    private void addMedarbetaruppdrag(String vardgivare, List<String> enhetIds) {
        final var uppdrag = new ArrayList<Commission>();
        for (String enhet : enhetIds) {
            final var commission = new Commission(enhet, CredentialInformation.VARD_OCH_BEHANDLING);
            commission.setHealthCareProviderHsaId(vardgivare);
            uppdrag.add(commission);
        }
        serviceStub.updateCredentialInformation(new CredentialInformation(PERSON_HSA_ID, uppdrag));
    }

    private void addHosPerson() {
        final var hsaPerson = new HsaPerson(PERSON_HSA_ID, FORNAMN, EFTERNAMN);
        final var paTitle = new PaTitle();
        paTitle.setTitleCode(BEFATTNINGSKOD);
        paTitle.setTitleName(BEFATTNINGSKOD);
        hsaPerson.setPaTitle(List.of(paTitle));
        hsaPerson.setPersonalPrescriptionCode(FORSKRIVARKOD);

        serviceStub.addHsaPerson(hsaPerson);
    }

    private void addVardgivare(String file) throws IOException {
        final var careProviderStub = OBJECT_MAPPER.readValue(new ClassPathResource(file).getFile(), CareProviderStub.class);
        serviceStub.addCareProvider(careProviderStub);
    }
}
