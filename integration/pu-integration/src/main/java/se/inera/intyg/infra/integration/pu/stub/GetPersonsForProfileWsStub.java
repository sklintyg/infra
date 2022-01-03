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
package se.inera.intyg.infra.integration.pu.stub;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofile.v3.rivtabp21.GetPersonsForProfileResponderInterface;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileResponseType;
import se.riv.strategicresourcemanagement.persons.person.getpersonsforprofileresponder.v3.GetPersonsForProfileType;
import se.riv.strategicresourcemanagement.persons.person.v3.IIType;
import se.riv.strategicresourcemanagement.persons.person.v3.PersonRecordType;
import se.riv.strategicresourcemanagement.persons.person.v3.RequestedPersonRecordType;

public class GetPersonsForProfileWsStub implements GetPersonsForProfileResponderInterface {

    private static final int LIMIT_GET_PERSONS_FOR_PROFILE = 500;

    @Autowired
    private StubResidentStore personer;

    @Override
    public GetPersonsForProfileResponseType getPersonsForProfile(String logicalAddress,
        GetPersonsForProfileType parameters) {

        validate(logicalAddress, parameters);
        GetPersonsForProfileResponseType response = new GetPersonsForProfileResponseType();
        for (String id : parameters.getPersonId().stream().map(pid -> pid.getExtension()).collect(Collectors.toList())) {
            PersonRecordType residentPost = personer.getResident(id);

            if (response.getRequestedPersonRecord().size() < LIMIT_GET_PERSONS_FOR_PROFILE) {
                // TKB Regel #1: En producent skall alltid i responset svara med requestedPersonalIdentity samt PersonRecord.
                // Vilket innebär att om konsumenten skickar in 3st personidentiteter men det endast finns 2st personposter som motsvarar
                // anropet så skall likväl 3 respons erhållas, varav 1 i så fall med en tom PersonRecord.
                if (residentPost == null) {
                    RequestedPersonRecordType requestedPersonRecordType = new RequestedPersonRecordType();
                    IIType peronalIndentity = new IIType();
                    peronalIndentity.setExtension(id);
                    requestedPersonRecordType.setRequestedPersonalIdentity(peronalIndentity);
                    response.getRequestedPersonRecord().add(requestedPersonRecordType);
                } else {
                    RequestedPersonRecordType requestedPersonRecordType = new RequestedPersonRecordType();
                    requestedPersonRecordType.setPersonRecord(residentPost);
                    requestedPersonRecordType.setRequestedPersonalIdentity(residentPost.getPersonalIdentity());
                    response.getRequestedPersonRecord().add(requestedPersonRecordType);
                }
            }
        }
        return response;
    }

    private void validate(String logicalAddress, GetPersonsForProfileType parameters) {
        List<String> messages = new ArrayList<>();
        if (logicalAddress == null || logicalAddress.length() == 0) {
            messages.add("logicalAddress can not be null or empty");
        }
        if (parameters == null) {
            messages.add("GetPersonsForProfileType can not be null");
        } else {
            if (parameters.getPersonId().isEmpty()) {
                messages.add("At least one personId must be supplied");
            }
            if (parameters.getProfile() == null) {
                messages.add("LookupSpecification must be included");
            }
        }

        if (messages.size() > 0) {
            throw new IllegalArgumentException(Joiner.on(",").join(messages));
        }
    }
}
