/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforperson.v1.rivtabp21.GetHospCredentialsForPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.IIType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.RestrictionType;

public class GetHospCredentialsForPersonResponderStub implements GetHospCredentialsForPersonResponderInterface {
    @Autowired
    HsaServiceStub hsaServiceStub;

    @Override
    public GetHospCredentialsForPersonResponseType getHospCredentialsForPerson(String logicalAddress,
                                                                               GetHospCredentialsForPersonType parameters) {

        GetHospCredentialsForPersonResponseType response = new GetHospCredentialsForPersonResponseType();
        HsaPerson hsaPerson = hsaServiceStub.getHsaPerson(parameters.getPersonalIdentityNumber());

        if (hsaPerson != null) {
            IIType iiType = new IIType();
            iiType.setExtension(hsaPerson.getPersonalIdentityNumber());
            response.setPersonalIdentityNumber(iiType);
            response.setPersonalPrescriptionCode(hsaPerson.getPersonalPrescriptionCode());
            response.getEducationCode().addAll(hsaPerson.getEducationCodes());

            if (hsaPerson.getRestrictions() != null) {
                for (HsaPerson.Restrictions restriction : hsaPerson.getRestrictions()) {
                    RestrictionType restrictionType = new RestrictionType();
                    restrictionType.setRestrictionCode(restriction.getRestrictionCode());
                    restrictionType.setRestrictionName(restriction.getRestrictionName());
                    response.getRestrictions().add(restrictionType);
                }
            }
        }
        return response;
    }

}
