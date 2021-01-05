/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

//CHECKSTYLE:OFF LineLength

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v2.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.employee.v2.PaTitleType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;

//CHECKSTYLE:ON LineLength

/**
 * Created by eriklupander on 2015-12-03.
 */
public class GetEmployeeResponderStub implements GetEmployeeIncludingProtectedPersonResponderInterface {

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Override
    public GetEmployeeIncludingProtectedPersonResponseType getEmployeeIncludingProtectedPerson(String logicalAddress,
        GetEmployeeIncludingProtectedPersonType getEmployeeIncludingProtectedPersonType) {
        GetEmployeeIncludingProtectedPersonResponseType response = new GetEmployeeIncludingProtectedPersonResponseType();
        String personHsaId = getEmployeeIncludingProtectedPersonType.getPersonHsaId();
        if (personHsaId == null) {
            personHsaId = getEmployeeIncludingProtectedPersonType.getPersonalIdentityNumber();
        }
        HsaPerson hsaPerson = hsaServiceStub.getHsaPerson(personHsaId);
        if (hsaPerson == null) {
            return response;
        }

        PersonInformationType person = new PersonInformationType();
        person.setTitle(hsaPerson.getTitle());
        person.setPersonHsaId(hsaPerson.getHsaId());
        person.setGivenName(hsaPerson.getGivenName());
        person.setMiddleAndSurName(hsaPerson.getMiddleAndSurname());
        person.setProtectedPerson(hsaPerson.isProtectedPerson());
        person.setAge(hsaPerson.getAge());
        person.setGender(hsaPerson.getGender());

        for (String healthCareProfessionalLicence : hsaPerson.getHealthCareProfessionalLicence()) {
            person.getHealthCareProfessionalLicence().add(healthCareProfessionalLicence);
        }

        for (HsaPerson.Speciality speciality : hsaPerson.getSpecialities()) {
            person.getSpecialityCode().add(speciality.getSpecialityCode());
            person.getSpecialityName().add(speciality.getSpecialityName());
        }

        if (hsaPerson.getPaTitle() != null) {
            for (HsaPerson.PaTitle paTitle : hsaPerson.getPaTitle()) {
                PaTitleType paTitleType = new PaTitleType();
                paTitleType.setPaTitleCode(paTitle.getTitleCode());
                paTitleType.setPaTitleName(paTitle.getTitleName());
                person.getPaTitle().add(paTitleType);
            }
        }

        response.getPersonInformation().add(person);
        return response;
    }

}
