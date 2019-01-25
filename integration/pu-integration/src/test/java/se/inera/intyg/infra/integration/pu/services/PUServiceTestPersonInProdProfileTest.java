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
package se.inera.intyg.infra.integration.pu.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static se.inera.intyg.infra.integration.pu.model.PersonSvar.Status.NOT_FOUND;


import java.io.File;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration("classpath:PUServiceTest/test-context.xml")
public class PUServiceTestPersonInProdProfileTest {

    @Autowired
    private PUServiceImpl service;

    @Autowired
    private GetPersonsForProfileResponderInterface residentService;

    @Autowired
    private Cache puCache;

    private static IIType iiType = new IIType();

    @BeforeClass
    public static void setupIITy() {
        Properties properties = System.getProperties();
        properties.setProperty("spring.profiles.active", "prod");

        iiType.setExtension("191212121212");
    }

    @Before
    public void setup() {

        puCache.clear();
        service.clearCache();
        // Some tests uses mocked residentService, reset here
        service.setService(residentService);
    }

    @Before
    @After
    public void init() {
        File dataFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "residentstore.data");
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }

    @Test
    public void checkTestPersonWithProdProfileShouldReturnNotFound() {

        //Set the active profile to 'prod'. Try searching for Nolåttan Testsson.
        Properties properties = System.getProperties();
        properties.setProperty("spring.profiles.active", "prod");

        final PersonSvar testPersonSvar = service.getPerson(createPnr("19080809-9808"));
        assertEquals(NOT_FOUND, testPersonSvar.getStatus());
        assertNull(testPersonSvar.getPerson());
    }

    private Personnummer createPnr(String pnr) {
        return Personnummer.createPersonnummer(pnr).get();
    }

}