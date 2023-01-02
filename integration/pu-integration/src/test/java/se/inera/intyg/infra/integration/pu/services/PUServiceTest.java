/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.infra.integration.pu.model.PersonSvar.Status.NOT_FOUND;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.integration.pu.model.Person;
import se.inera.intyg.infra.integration.pu.model.PersonSvar;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.LookupProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.NamePartType;
import se.riv.strategicresourcemanagement.persons.person.v3.NameType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;
import se.riv.strategicresourcemanagement.persons.person.v3.RequestedPersonRecordType;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration("classpath:PUServiceTest/test-context.xml")
@ActiveProfiles({"test"})
public class PUServiceTest {

    @Autowired
    private PUServiceImpl service;

    @Autowired
    private GetPersonsForProfileResponderInterface residentService;

    @Autowired
    private Cache puCache;

    static String logicalAddress = "${putjanst.logicaladdress}";

    private static IIType iiType = new IIType();

    @BeforeClass
    public static void setupIITy() {
        iiType.setExtension("191212121212");
    }

    @Before
    public void setup() {
        puCache.clear();
        service.clearCache();
        // Some tests uses mocked residentService, reset here
        service.setService(residentService);
    }

    @Test
    public void checkExistingPersonWithFullAddress() {
        Person person = service.getPerson(createPnr("19121212-1212")).getPerson();
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
    }

    @Test
    public void checkExistingPersonWithMinimalAddress() {
        Person person = service.getPerson(createPnr("20121212-1212")).getPerson();
        assertEquals("Lilltolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Storgatan 1", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
    }

    @Test
    public void checkExistingPersonWithMellannamn() {
        Person person = service.getPerson(createPnr("19520614-2597")).getPerson();
        assertEquals("Per Peter", person.getFornamn());
        assertEquals("Pärsson", person.getEfternamn());
        assertEquals("Svensson", person.getMellannamn());
    }

    @Test
    public void checkExistingPersonWithoutAddress() {
        Person person = service.getPerson(createPnr("19520529-2260")).getPerson();
        assertEquals("Maria Louise", person.getFornamn());
        assertEquals("Pärsson", person.getEfternamn());
        assertNull(person.getPostadress());
        assertNull(person.getPostnummer());
        assertNull(person.getPostort());
    }

    @Test
    public void checkNonExistingPerson() {
        PersonSvar svar = service.getPerson(createPnr("19121212-7169"));
        assertNull(svar.getPerson());
        assertEquals(NOT_FOUND, svar.getStatus());
    }

    @Test
    public void checkNoneExistingPersons() {
        List<Personnummer> pnrs = Arrays.asList(createPnr("19121212-7169"), createPnr("19971230-2380"),
            createPnr("19980919-2397"), createPnr("19981029-2392"));

        // Create mock
        GetPersonsForProfileType parameters = service.buildPersonsForProfileRequest(pnrs);
        GetPersonsForProfileResponseType response = residentService.getPersonsForProfile(logicalAddress, parameters);
        GetPersonsForProfileResponderInterface mockResidentService = mock(GetPersonsForProfileResponderInterface.class);

        when(mockResidentService.getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class))).thenReturn(response);
        service.setService(mockResidentService);

        // Make the call
        Map<Personnummer, PersonSvar> persons = service.getPersons(pnrs);

        // Verify
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));

        // Assert size
        assertEquals(4, persons.size());

        // Assert content
        persons.entrySet().stream().forEach(entry -> {
            assertNotNull(entry.getValue());
            assertNull(entry.getValue().getPerson());
            assertEquals(NOT_FOUND, entry.getValue().getStatus());
        });

    }

    @Test
    public void checkSomeExistingPersons() {
        List<Personnummer> pnrs = Arrays.asList(createPnr("19520614-2597"), createPnr("19971230-2380"),
            createPnr("20121212-1212"), createPnr("19981029-2392"));

        // Create mock
        GetPersonsForProfileType parameters = service.buildPersonsForProfileRequest(pnrs);
        GetPersonsForProfileResponseType response = residentService.getPersonsForProfile(logicalAddress, parameters);
        GetPersonsForProfileResponderInterface mockResidentService = mock(GetPersonsForProfileResponderInterface.class);

        when(mockResidentService.getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class))).thenReturn(response);
        service.setService(mockResidentService);

        // Make the call
        Map<Personnummer, PersonSvar> persons = service.getPersons(pnrs);

        // Verify
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));

        // Assert size
        assertEquals(4, persons.size());

        // Assert content
        persons.forEach((key, value) -> {
            assertNotNull(value);
            if (key.getPersonnummerWithDash().equals("19520614-2597") || key.getPersonnummerWithDash().equals("20121212-1212")) {
                assertNotNull(value.getPerson());
                assertEquals(PersonSvar.Status.FOUND, value.getStatus());
            } else {
                assertNull(value.getPerson());
                assertEquals(NOT_FOUND, value.getStatus());
            }
        });
    }

    @Test
    public void checkConfidentialPerson() {
        Person person = service.getPerson(createPnr("19540123-2540")).getPerson();
        assertEquals("Maj", person.getFornamn());
        assertEquals("Pärsson", person.getEfternamn());
        assertEquals("KUNGSGATAN 5", person.getPostadress());
        assertEquals("41234", person.getPostnummer());
        assertEquals("GÖTEBORG", person.getPostort());
        assertTrue(person.isSekretessmarkering());
    }

    @Test
    public void checkIsProtectedPopulationRecord() {
        Person person = service.getPerson(createPnr("20051231-2398")).getPerson();
        assertTrue(person.isSekretessmarkering());
    }

    @Test
    public void checkDeadPerson() {
        Person person = service.getPerson(createPnr("19000525-9809")).getPerson();
        assertEquals("Tod", person.getFornamn());
        assertEquals("Svensson", person.getEfternamn());
        assertEquals("Tolvansson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
        assertTrue(person.isAvliden());
    }

    @Test
    public void checkCachedPerson() throws Exception {
        // Create mock
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        parameters.setProfile(LookupProfileType.P_1);
        parameters.getPersonId().add(iiType);

        GetPersonsForProfileResponseType response = residentService.getPersonsForProfile(logicalAddress, parameters);
        GetPersonsForProfileResponderInterface mockResidentService = mock(GetPersonsForProfileResponderInterface.class);

        when(mockResidentService.getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class))).thenReturn(response);
        // ReflectionTestUtils.setField(((Advised) service).getTargetSource().getTarget(), "service", mockResidentService);
        service.setService(mockResidentService);

        // First request should call the lookup service
        Person person = service.getPerson(createPnr("19121212-1212")).getPerson();
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());

        // This request should be cached
        person = service.getPerson(createPnr("19121212-1212")).getPerson();
        // lookupResidentForFullProfile should still only be called once
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        // person information should still be the same
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
    }

    @Test
    public void dontCachePersonLookupError() throws Exception {
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
        PersonSvar personsvar = service.getPerson(createPnr("19121212-1212"));
        verify(mockResidentService).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals(personsvar.getStatus(), PersonSvar.Status.ERROR);
        assertNull(personsvar.getPerson());

        // since first request returned an error this request should call the lookup service again
        personsvar = service.getPerson(createPnr("19121212-1212"));
        // lookupResidentForFullProfile should still only be called once
        verify(mockResidentService, times(2)).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals(personsvar.getStatus(), PersonSvar.Status.ERROR);
        assertNull(personsvar.getPerson());

        // the third attempt will go through and should return real data
        personsvar = service.getPerson(createPnr("19121212-1212"));
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
        personsvar = service.getPerson(createPnr("19121212-1212"));
        // lookupResidentForFullProfile should still only be called once
        verify(mockResidentService, times(3)).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));
        assertEquals(personsvar.getStatus(), PersonSvar.Status.FOUND);
        person = personsvar.getPerson();
        assertEquals("Tolvan", person.getFornamn());
        assertEquals("Tolvansson", person.getEfternamn());
        assertEquals("Svensson, Storgatan 1, PL 1234", person.getPostadress());
        assertEquals("12345", person.getPostnummer());
        assertEquals("Småmåla", person.getPostort());
    }

    @Test
    public void testGetPersonsSinglePage() throws Exception {
        GetPersonsForProfileResponderInterface mockResidentService = createGetPersonsMock();
        service.setService(mockResidentService);

        List<Personnummer> pnrList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            pnrList.add(createPnr("19121212-" + Strings.padStart(Integer.toString(i), 4, '0')));
        }

        Map<Personnummer, PersonSvar> response = service.getPersons(pnrList);

        verify(mockResidentService, times(1)).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));

        assertEquals(response.size(), 500);
    }

    @Test
    public void testGetPersonsWithPaging() throws Exception {

        // Create a mock returning max 500 entries
        GetPersonsForProfileResponderInterface mockResidentService = createGetPersonsMock();
        service.setService(mockResidentService);

        // Create request requesting 1001 entries
        List<Personnummer> pnrList = new ArrayList<>();
        for (int i = 0; i < 1001; i++) {
            pnrList.add(createPnr("19121212-" + Strings.padStart(Integer.toString(i), 4, '0')));
        }

        Map<Personnummer, PersonSvar> response = service.getPersons(pnrList);

        verify(mockResidentService, times(3)).getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class));

        // Verify all requested personnummer are present and has correct name
        assertEquals(response.size(), 1001);
        for (Personnummer requestedPnr : pnrList) {
            PersonSvar personSvar = response.get(requestedPnr);

            assertEquals(PersonSvar.Status.FOUND, personSvar.getStatus());
            Person person = personSvar.getPerson();
            assertEquals(requestedPnr, person.getPersonnummer());
            assertEquals("Testpersonnamn " + requestedPnr.getPersonnummer(), person.getFornamn());
        }
    }

    private GetPersonsForProfileResponderInterface createGetPersonsMock() {
        // Create a mock returning max 500 entries
        GetPersonsForProfileResponderInterface mockResidentService = mock(GetPersonsForProfileResponderInterface.class);
        GetPersonsForProfileResponseType mockResponse = new GetPersonsForProfileResponseType();
        when(mockResidentService.getPersonsForProfile(anyString(), any(GetPersonsForProfileType.class)))
            .thenAnswer((invocation) -> {
                GetPersonsForProfileType request = invocation.getArgument(1);
                GetPersonsForProfileResponseType response = new GetPersonsForProfileResponseType();
                int responseCount = Math.min(request.getPersonId().size(), 500);
                for (int i = 0; i < responseCount; i++) {
                    IIType personId = request.getPersonId().get(i);
                    RequestedPersonRecordType requestedPersonRecordType = new RequestedPersonRecordType();
                    requestedPersonRecordType.setRequestedPersonalIdentity(personId);
                    PersonRecordType personRecordType = new PersonRecordType();
                    NameType nameType = new NameType();
                    NamePartType namePartType = new NamePartType();
                    namePartType.setName("Testpersonnamn " + personId.getExtension());
                    nameType.setGivenName(namePartType);
                    personRecordType.setName(nameType);
                    requestedPersonRecordType.setPersonRecord(personRecordType);
                    response.getRequestedPersonRecord().add(requestedPersonRecordType);
                }
                return response;
            });
        return mockResidentService;
    }

    private Personnummer createPnr(String pnr) {
        return Personnummer.createPersonnummer(pnr).get();
    }

}
