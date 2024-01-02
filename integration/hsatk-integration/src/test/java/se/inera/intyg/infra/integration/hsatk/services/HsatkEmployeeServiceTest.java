/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.client.EmployeeClient;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.riv.infrastructure.directory.employee.v2.HealthCareProfessionalLicenceSpecialityType;
import se.riv.infrastructure.directory.employee.v2.PaTitleType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;
import se.riv.infrastructure.directory.employee.v2.ProfileEnum;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HsatkEmployeeServiceTest {

    private static final String PIN = "PIN";
    private static final String HSA_ID = "HSA_ID";
    private static final String PTC = "PTC";
    private static final String PTN = "PTN";
    private static final String SC = "SC";
    private static final String SN = "SN";

    @Mock
    EmployeeClient employeeClient;

    @InjectMocks
    HsatkEmployeeServiceImpl hsatkEmployeeService;

    @Test
    public void testGetEmployeeOK() throws HsaServiceCallException {
        when(employeeClient.getEmployee(anyString(), any(), any())).thenReturn(buildEmployeeResponse());

        List<PersonInformation> informationList = hsatkEmployeeService.getEmployee(PIN, null);

        Assert.assertNotNull(informationList);
        Assert.assertEquals(PTN, informationList.stream().findFirst().get().getPaTitle().stream().findFirst().get().getPaTitleName());
    }

    @Test
    public void testGetEmployeeProfileOK() throws HsaServiceCallException {
        when(employeeClient.getEmployee(any(), eq(HSA_ID), any())).thenReturn(buildEmployeeResponse());

        List<PersonInformation> informationList = hsatkEmployeeService.getEmployee(null, HSA_ID, ProfileEnum.EXTENDED_1.value());

        Assert.assertNotNull(informationList);
        Assert.assertEquals(PTN, informationList.stream().findFirst().get().getPaTitle().stream().findFirst().get().getPaTitleName());
    }

    @Test(expected = WebServiceException.class)
    public void testGetEmployeeIllegalArgsException() throws HsaServiceCallException {
        when(employeeClient.getEmployee(eq(PIN), eq(HSA_ID), any())).thenThrow(new IllegalArgumentException());

        List<PersonInformation> informationList = hsatkEmployeeService.getEmployee(PIN, HSA_ID, ProfileEnum.EXTENDED_1.value());
    }

    @Test
    public void testGetEmployeeWebServiceException() throws HsaServiceCallException {
        when(employeeClient.getEmployee(any(), eq(HSA_ID), any())).thenThrow(new HsaServiceCallException());

        List<PersonInformation> informationList = hsatkEmployeeService.getEmployee(null, HSA_ID, ProfileEnum.EXTENDED_1.value());

        Assert.assertNotNull(informationList);
        Assert.assertEquals(0, informationList.size());
    }

    private List<PersonInformationType> buildEmployeeResponse() {
        List<PersonInformationType> response = new ArrayList<>();
        PersonInformationType personInformationType = new PersonInformationType();
        personInformationType.setPersonHsaId(HSA_ID);
        personInformationType.getSpecialityCode();
        personInformationType.getFacsimileTelephoneNumber();

        HealthCareProfessionalLicenceSpecialityType specialityType = new HealthCareProfessionalLicenceSpecialityType();
        specialityType.setSpecialityCode(SC);
        specialityType.setSpecialityName(SN);
        personInformationType.getHealthCareProfessionalLicenceSpeciality().add(specialityType);

        PaTitleType titleType = new PaTitleType();
        titleType.setPaTitleCode(PTC);
        titleType.setPaTitleName(PTN);
        personInformationType.getPaTitle().add(titleType);

        response.add(personInformationType);
        return response;
    }
}
