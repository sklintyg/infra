/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import org.apache.ignite.cache.spring.SpringCacheManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.cache.core.BasicCacheConfiguration;
import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.LookupProfileType;

import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:PUServiceTest/test-context.xml")
public class PUServiceTest {

    @Autowired
    private PUServiceImpl service;

    @Autowired
    private GetPersonsForProfileResponderInterface residentService;

    private SpringCacheManager cacheManager;

    @Autowired
    private BasicCacheConfiguration basicCacheConfiguration;

    private static IIType iiType = new IIType();

    @BeforeClass
    public static void setupIIType() {
        iiType.setExtension("191212121212");
    }

    @Before
    public void setup() {
        cacheManager = basicCacheConfiguration.cacheManager();
        service.clearCache();
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
    public void checkExistingPersonWithFullAddress() {
        Person person = service.getPerson(new Personnummer("19121212-1212")).getPerson();
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
    }

    @Test
    public void checkExistingPersonWithMinimalAddress() {
        Person person = service.getPerson(new Personnummer("20121212-1212")).getPerson();
        assertEquals("Lilltolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Storgatan 1", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
    }

    @Test
    public void checkExistingPersonWithMellannamn() {
        Person person = service.getPerson(new Personnummer("19520614-2597")).getPerson();
        assertEquals("Per Peter", person.getFornamn());
        assertEquals("Pärsson", person.getEfternamn());
        assertEquals("Svensson", person.getMellannamn());
    }

    @Test
    public void checkExistingPersonWithoutAddress() {
        Person person = service.getPerson(new Personnummer("19520529-2260")).getPerson();
        assertEquals("Maria Lousie", person.getFornamn());
        assertEquals("Pärsson", person.getEfternamn());
        assertNull(person.getPostadress());
        assertNull(person.getPostnummer());
        assertNull(person.getPostort());
    }

    @Test
    public void checkNonExistingPerson() {
        Person person = service.getPerson(new Personnummer("19121212-7169")).getPerson();
        assertNull(person);
    }

    @Test
    public void checkConfidentialPerson() {
        Person person = service.getPerson(new Personnummer("19540123-2540")).getPerson();
        assertEquals("Maj", person.getFornamn());
        assertEquals("Pärsson", person.getEfternamn());
        assertEquals("KUNGSGATAN 5", person.getPostadress());
        assertEquals("41234", person.getPostnummer());
        assertEquals("GÖTEBORG", person.getPostort());
        assertTrue(person.isSekretessmarkering());
    }

    @Test
    public void checkDeadPerson() {
        Person person = service.getPerson(new Personnummer("19000525-9809")).getPerson();
        assertEquals("Tod", person.getFornamn());
        assertEquals("Svensson", person.getEfternamn());
        assertEquals("Tolvansson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
        assertTrue(person.isAvliden());
    }

    @Test
    public void checkCachedPerson() throws Exception {
        String logicalAddress = "${putjanst.logicaladdress}";

        // Create mock
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        parameters.setProfile(LookupProfileType.P_1);
        parameters.getPersonId().add(iiType);

        GetPersonsForProfileType parameters2 = new GetPersonsForProfileType();
        parameters2.setProfile(LookupProfileType.P_1);
        parameters2.getPersonId().add(iiType);

        System.err.println("Are they equal: " + parameters.equals(parameters2));

        GetPersonsForProfileResponseType response = residentService.getPersonsForProfile(logicalAddress, parameters);
        GetPersonsForProfileResponderInterface mockResidentService = mock(GetPersonsForProfileResponderInterface.class);

        when(mockResidentService.getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class))).thenReturn(response);
        //ReflectionTestUtils.setField(((Advised) service).getTargetSource().getTarget(), "service", mockResidentService);
        service.setService(mockResidentService);

        // First request should call the lookup service
        Person person = service.getPerson(new Personnummer("19121212-1212")).getPerson();
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());

        // This request should be cached
        person = service.getPerson(new Personnummer("19121212-1212")).getPerson();
        // lookupResidentForFullProfile should still only be called once
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        // person information should still be the same
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());

//        ReflectionTestUtils.setField(((Advised) service).getTargetSource().getTarget(), "service", residentService);
        service.setService(residentService);
    }

    @Test
    public void dontCachePersonLookupError() throws Exception {
        String logicalAddress = "${putjanst.logicaladdress}";

        // Create mock
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        parameters.setProfile(LookupProfileType.P_1);
        parameters.getPersonId().add(iiType);

        GetPersonsForProfileResponseType response = residentService.getPersonsForProfile(logicalAddress, parameters);
        GetPersonsForProfileResponderInterface mockResidentService = mock(GetPersonsForProfileResponderInterface.class);

        when(mockResidentService.getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class)))
                .thenThrow(new SOAPFaultException(SOAPFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createFault()))
                .thenThrow(new WebServiceException())
                .thenReturn(response);
       // ReflectionTestUtils.setField(((Advised) service).getTargetSource().getTarget(), "service", mockResidentService);
        service.setService(mockResidentService);

        // First request should call the lookup service
        PersonSvar personsvar = service.getPerson(new Personnummer("19121212-1212"));
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals(personsvar.getStatus(), PersonSvar.Status.ERROR);
        assertNull(personsvar.getPerson());

        // since first request returned an error this request should call the lookup service again
        personsvar = service.getPerson(new Personnummer("19121212-1212"));
        // lookupResidentForFullProfile should still only be called once
        verify(mockResidentService, times(2)).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals(personsvar.getStatus(), PersonSvar.Status.ERROR);
        assertNull(personsvar.getPerson());

        // the third attempt will go through and should return real data
        personsvar = service.getPerson(new Personnummer("19121212-1212"));
        // lookupResidentForFullProfile should still only be called once
        verify(mockResidentService, times(3)).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals(personsvar.getStatus(), PersonSvar.Status.FOUND);
        Person person = personsvar.getPerson();
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());

        // the fourth attempt will return cached data, lookupResidentForFullProfile should only be called 3 times total
        personsvar = service.getPerson(new Personnummer("19121212-1212"));
        // lookupResidentForFullProfile should still only be called once
        verify(mockResidentService, times(3)).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals(personsvar.getStatus(), PersonSvar.Status.FOUND);
        person = personsvar.getPerson();
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());

       // ReflectionTestUtils.setField(((Advised) service).getTargetSource().getTarget(), "service", residentService);
        service.setService(residentService);
    }
}
