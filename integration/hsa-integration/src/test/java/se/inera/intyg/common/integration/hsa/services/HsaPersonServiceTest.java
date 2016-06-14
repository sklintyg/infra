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

package se.inera.intyg.common.integration.hsa.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.common.integration.hsa.client.AuthorizationManagementService;
import se.inera.intyg.common.integration.hsa.client.EmployeeService;
import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.v1.PersonInformationType;

@RunWith(MockitoJUnitRunner.class)
public class HsaPersonServiceTest {

    private static final String VALID_HSA_ID = "SE11837399";
    private static final String INVALID_HSA_ID = "SE88888888";

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AuthorizationManagementService authorizationManagementService;

    @InjectMocks
    private HsaPersonServiceImpl hsaPersonService;

    @Before
    public void setupExpectations() throws ExternalServiceCallException {

        when(employeeService.getEmployee(VALID_HSA_ID, null, null)).thenReturn(buildResponse());

        ArrayList<PersonInformationType> emptyResponse = new ArrayList<PersonInformationType>();
        when(employeeService.getEmployee(INVALID_HSA_ID, null, null)).thenReturn(emptyResponse);
    }

    @Test
    public void testGetHsaPersonInfoWithValidPerson() {

        List<PersonInformationType> res = hsaPersonService.getHsaPersonInfo(VALID_HSA_ID);

        assertNotNull(res);
        assertFalse(res.isEmpty());
    }

    @Test
    public void testGetHsaPersonInfoWithInvalidPerson() {

        List<PersonInformationType> res = hsaPersonService.getHsaPersonInfo(INVALID_HSA_ID);

        assertNotNull(res);
        assertTrue(res.isEmpty());
    }

    private List<PersonInformationType> buildResponse() {

        PersonInformationType userType = buildUserType(VALID_HSA_ID, "Henry", "Jekyl");

        return Arrays.asList(userType);
    }

    private PersonInformationType buildUserType(String hsaId, String fName, String lName) {

        PersonInformationType type = new PersonInformationType();
        type.setPersonHsaId(hsaId);
        type.setGivenName(fName);
        type.setMiddleAndSurName(lName);
        type.setMail(fName.concat(".").concat(lName).concat("@mailinator.com"));

        type.getSpecialityCode().addAll(Arrays.asList("100", "200", "300"));
        type.getSpecialityName().addAll(Arrays.asList("Kirurgi", "Psykiatri", "Ortopedi"));

        return type;
    }

}
