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
package se.inera.intyg.infra.integration.pu.stub;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.LookupProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetPersonsForProfileWsStubTest {

    @Mock
    private StubResidentStore residentStore;

    @InjectMocks
    private GetPersonsForProfileResponderInterface ws = new GetPersonsForProfileWsStub();

    @Test(expected = IllegalArgumentException.class)
    public void nullAddressThrowsException() {
        ws.getPersonsForProfile(null, defaultRequest());
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyAddressThrowsException() {
        ws.getPersonsForProfile("", defaultRequest());
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullParametersThrowsException() {
        ws.getPersonsForProfile("address", null);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noPersonIdThrowsException() {
        GetPersonsForProfileType parameters = defaultRequest();
        parameters.getPersonId().clear();
        ws.getPersonsForProfile("address", parameters);
        fail();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noLookupSpecificationThrowsException() {
        GetPersonsForProfileType parameters = defaultRequest();
        parameters.setProfile(null);
        ws.getPersonsForProfile("address", parameters);
        fail();
    }

    @Test
    public void personIdParametersReturned() {
        IIType iitype = new IIType();
        iitype.setExtension("191212121212");
        PersonRecordType resident = new PersonRecordType();
        resident.setPersonalIdentity(iitype);
        when(residentStore.getResident("191212121212")).thenReturn(resident);
        GetPersonsForProfileType parameters = defaultRequest();
        GetPersonsForProfileResponseType address = ws.getPersonsForProfile("address", parameters);
        assertEquals(1, address.getRequestedPersonRecord().size());
        assertEquals("191212121212", address.getRequestedPersonRecord().get(0).getRequestedPersonalIdentity().getExtension());
    }

    @Test
    public void testLookupLimit() throws Exception {
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        for (int i = 0; i < 501; i++) {
            IIType iitype = new IIType();
            iitype.setExtension("191212121212");
            parameters.getPersonId().add(iitype);
        }
        parameters.setProfile(LookupProfileType.P_1);
        GetPersonsForProfileResponseType address = ws.getPersonsForProfile("address", parameters);
        assertEquals(500, address.getRequestedPersonRecord().size());
    }

    private GetPersonsForProfileType defaultRequest() {
        GetPersonsForProfileType parameters = new GetPersonsForProfileType();
        IIType iitype = new IIType();
        iitype.setExtension("191212121212");
        parameters.getPersonId().add(iitype);
        parameters.setProfile(LookupProfileType.P_1);
        return parameters;
    }

}
