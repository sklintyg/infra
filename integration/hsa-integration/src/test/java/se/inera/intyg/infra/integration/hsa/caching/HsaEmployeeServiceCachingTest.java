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
package se.inera.intyg.infra.integration.hsa.caching;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.infra.integration.hsa.client.EmployeeService;
import se.inera.intyg.infra.integration.hsa.stub.GetEmployeeResponderStub;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.v1.PersonInformationType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:HsaOrganizationServiceCachingTest/test-caching-context.xml")
public class HsaEmployeeServiceCachingTest {

    private static final String PERSON_HSA_ID = "person-hsa-1";

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void testGetEmployeeUsesCaching() throws Exception {
        GetEmployeeResponderStub stub = mock(GetEmployeeResponderStub.class);
        when(stub.getEmployeeIncludingProtectedPerson(any(), any(GetEmployeeIncludingProtectedPersonType.class)))
            .thenReturn(buildEmployeeResponse());

        ReflectionTestUtils
            .setField(((Advised) employeeService).getTargetSource().getTarget(), "getEmployeeIncludingProtectedPersonResponderInterface",
                stub);

        List<PersonInformationType> employees = employeeService.getEmployee(PERSON_HSA_ID, null, null);
        assertNotNull(employees);
        assertTrue(employees.size() == 1);

        employees = employeeService.getEmployee(PERSON_HSA_ID, null, null);
        assertNotNull(employees);
        assertTrue(employees.size() == 1);

        verify(stub, times(1)).getEmployeeIncludingProtectedPerson(anyString(), any(GetEmployeeIncludingProtectedPersonType.class));
    }

    private GetEmployeeIncludingProtectedPersonResponseType buildEmployeeResponse() {
        GetEmployeeIncludingProtectedPersonResponseType resp = new GetEmployeeIncludingProtectedPersonResponseType();
        PersonInformationType personInformationType = new PersonInformationType();
        personInformationType.setPersonHsaId(PERSON_HSA_ID);
        resp.getPersonInformation().add(personInformationType);
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }

}
