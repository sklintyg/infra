/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.pu.services;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static se.inera.intyg.infra.integration.pu.model.PersonSvar.Status.FOUND;
import static se.inera.intyg.infra.integration.pu.model.PersonSvar.Status.NOT_FOUND;

import java.util.Arrays;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration("classpath:PUServiceTest/test-context.xml")
@ActiveProfiles({"prod"})
public class PUServiceTestPersonInProdProfileTest {

    //bootstrap-personer/nollattan.testsson.json (with testIndicator true)
    final Personnummer PERSON_WITH_TESTINDICATOR = createPnr("19080809-9808");

    //bootstrap-personer/anita.norden.json (with testIndicator false)
    final Personnummer PERSON_WITHOUT_TESTINDICATOR = createPnr("19900511-2389");


    @Autowired
    private PUServiceImpl service;

    @Autowired
    private GetPersonsForProfileResponderInterface residentService;

    private static Personnummer createPnr(String pnr) {
        return Personnummer.createPersonnummer(pnr).get();
    }

    @Before
    public void setup() {
        service.setService(residentService);
    }

    @Test
    public void checkTestPersonWithProdProfileShouldReturnNotFoundForExistingTestPerson() {

        final PersonSvar testPersonSvar = service.getPerson(PERSON_WITH_TESTINDICATOR);
        assertEquals(NOT_FOUND, testPersonSvar.getStatus());
        assertNull(testPersonSvar.getPerson());
    }

    @Test
    public void checkTestPersonWithProdProfileShouldReturnFoundForExistingNonTestPerson() {
        final PersonSvar testPersonSvar = service.getPerson(PERSON_WITHOUT_TESTINDICATOR);
        assertEquals(FOUND, testPersonSvar.getStatus());
        assertNotNull(testPersonSvar.getPerson());
    }

    @Test
    public void checkTestPersons() {

        final Personnummer nonexistingperson = createPnr("19000000-0000");
        final Map<Personnummer, PersonSvar> persons = service
            .getPersons(Arrays.asList(PERSON_WITH_TESTINDICATOR, PERSON_WITHOUT_TESTINDICATOR, nonexistingperson));
        assertEquals(3, persons.size());
        assertEquals(NOT_FOUND, persons.get(PERSON_WITH_TESTINDICATOR).getStatus());
        assertEquals(FOUND, persons.get(PERSON_WITHOUT_TESTINDICATOR).getStatus());
        assertEquals(NOT_FOUND, persons.get(nonexistingperson).getStatus());
    }

    @Test
    public void checkTestPersonsWithWarmCache() {

        final Personnummer nonexistingperson = createPnr("19000000-0000");
        service.getPersons(Arrays.asList(PERSON_WITH_TESTINDICATOR, PERSON_WITHOUT_TESTINDICATOR, nonexistingperson));

        final Map<Personnummer, PersonSvar> persons = service
            .getPersons(Arrays.asList(PERSON_WITH_TESTINDICATOR, PERSON_WITHOUT_TESTINDICATOR, nonexistingperson));
        assertEquals(3, persons.size());
        assertEquals(NOT_FOUND, persons.get(PERSON_WITH_TESTINDICATOR).getStatus());
        assertEquals(FOUND, persons.get(PERSON_WITHOUT_TESTINDICATOR).getStatus());
        assertEquals(NOT_FOUND, persons.get(nonexistingperson).getStatus());
    }

}
