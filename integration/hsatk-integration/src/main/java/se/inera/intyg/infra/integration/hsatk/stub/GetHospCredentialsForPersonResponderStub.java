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
package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforperson.v1.rivtabp21.GetHospCredentialsForPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HCPSpecialityCodesType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HealthCareProfessionalLicenceType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.IIType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.RestrictionType;

import java.util.stream.Collectors;

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
                response.getRestrictions().addAll(hsaPerson.getRestrictions()
                        .stream().map(this::toRestrictionType).collect(Collectors.toList()));
            }

            if (hsaPerson.getSpecialities() != null) {
                response.getHealthCareProfessionalLicenceSpeciality().addAll(hsaPerson.getSpecialities()
                        .stream().map(this::toHCPSpecialityType).collect(Collectors.toList()));
            }

            if (hsaPerson.getHealthCareProfessionalLicenceType() != null) {
                response.getHealthCareProfessionalLicence().addAll(hsaPerson.getHealthCareProfessionalLicenceType()
                        .stream().map(this::toHealthCareProfessionalLicenceType).collect(Collectors.toList()));
            }
        }
        return response;
    }

    private RestrictionType toRestrictionType(HsaPerson.Restrictions restrictions) {
        RestrictionType restrictionType = new RestrictionType();

        restrictionType.setRestrictionCode(restrictions.getRestrictionCode());
        restrictionType.setRestrictionName(restrictions.getRestrictionName());

        return restrictionType;
    }

    private HCPSpecialityCodesType toHCPSpecialityType(HsaPerson.Speciality speciality) {
        HCPSpecialityCodesType hcpSpecialityCodesType = new HCPSpecialityCodesType();

        hcpSpecialityCodesType.setSpecialityCode(speciality.getSpecialityCode());
        hcpSpecialityCodesType.setSpecialityName(speciality.getSpecialityName());

        return hcpSpecialityCodesType;
    }

    private HealthCareProfessionalLicenceType toHealthCareProfessionalLicenceType(HsaPerson.HealthCareProfessionalLicenceType licenceType) {
        HealthCareProfessionalLicenceType healthCareProfessionalLicenceType = new HealthCareProfessionalLicenceType();

        healthCareProfessionalLicenceType.setHealthCareProfessionalLicenceCode(licenceType.getHealthCareProfessionalLicenceCode());
        healthCareProfessionalLicenceType.setHealthCareProfessionalLicenceName(licenceType.getHealthCareProfessionalLicenceName());

        return healthCareProfessionalLicenceType;
    }

}
