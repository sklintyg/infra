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
package se.inera.intyg.infra.integration.hsatk.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v2.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeClientTest {

    private static final String HSA_ID = "hsa-1";
    private static final String PNR = "19121212-1212";

    @Mock
    GetEmployeeIncludingProtectedPersonResponderInterface getEmployeeService;

    @InjectMocks
    private EmployeeClient employeeClient;

    @Test
    public void testOk() throws HsaServiceCallException {
        doReturn(buildResponse()).when(getEmployeeService)
            .getEmployeeIncludingProtectedPerson(any(), any(GetEmployeeIncludingProtectedPersonType.class));

        List<PersonInformationType> response = employeeClient.getEmployee(HSA_ID, null, null);
        assertNotNull(response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBothPnrAndHsaIdSupplied() throws HsaServiceCallException {
        employeeClient.getEmployee(PNR, HSA_ID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPnrOrHsaIdSupplied() throws HsaServiceCallException {
        employeeClient.getEmployee(null, null, null);
    }

    @Test(expected = HsaServiceCallException.class)
    public void testWebServiceExceptionIsThrownForSoapFault() throws HsaServiceCallException {
        SOAPFaultException ex = mock(SOAPFaultException.class);
        when(getEmployeeService.getEmployeeIncludingProtectedPerson(
            or(isNull(), anyString()),
            any(GetEmployeeIncludingProtectedPersonType.class))
        ).thenThrow(ex);

        employeeClient.getEmployee(null, HSA_ID, null);
    }

    @Test(expected = HsaServiceCallException.class)
    public void testHSAReturnsEmptyResult() throws HsaServiceCallException {
        doReturn(new GetEmployeeIncludingProtectedPersonResponseType()).when(getEmployeeService)
            .getEmployeeIncludingProtectedPerson(any(), any(GetEmployeeIncludingProtectedPersonType.class));

        List<PersonInformationType> response = employeeClient.getEmployee(HSA_ID, null, null);
        assertNotNull(response);
    }

    private GetEmployeeIncludingProtectedPersonResponseType buildResponse() {
        GetEmployeeIncludingProtectedPersonResponseType resp = new GetEmployeeIncludingProtectedPersonResponseType();
        resp.getPersonInformation().add(new PersonInformationType());
        return resp;
    }
}
