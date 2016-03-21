/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.webcert.integration.pu.stub;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileType;
import se.riv.population.residentmaster.lookupresidentforfullprofileresponder.v11.LookupResidentForFullProfileResponderInterface;
import se.riv.population.residentmaster.types.v1.ResidentType;


public class LookupResidentForFullProfileWsStub implements LookupResidentForFullProfileResponderInterface {

    @Autowired
    private ResidentStore personer;

    @Override
    public LookupResidentForFullProfileResponseType lookupResidentForFullProfile(String logicalAddress, LookupResidentForFullProfileType parameters) {
        validate(logicalAddress, parameters);
        LookupResidentForFullProfileResponseType response = new LookupResidentForFullProfileResponseType();
        for (String id : parameters.getPersonId()) {
            ResidentType residentPost = personer.get(id);

            if (residentPost != null) {
                response.getResident().add(residentPost);
            }
        }
        return response;
    }

    private void validate(String logicalAddress, LookupResidentForFullProfileType parameters) {
        List<String> messages = new ArrayList<>();
        if (logicalAddress == null || logicalAddress.length() == 0) {
            messages.add("logicalAddress can not be null or empty");
        }
        if (parameters == null) {
            messages.add("LookupResidentForFullProfileType can not be null");
        } else {
            if (parameters.getPersonId().isEmpty()) {
                messages.add("At least one personId must be supplied");
            }
            if (parameters.getLookUpSpecification() == null) {
                messages.add("LookupSpecification must be included");
            }
        }

        if (messages.size() > 0) {
            throw new IllegalArgumentException(StringUtils.collectionToCommaDelimitedString(messages));
        }
    }
}