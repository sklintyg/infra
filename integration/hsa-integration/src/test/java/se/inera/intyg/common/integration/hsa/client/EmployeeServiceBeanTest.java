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
package se.inera.intyg.common.integration.hsa.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v1.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.v1.PersonInformationType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

/**
 * Created by eriklupander on 2016-03-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceBeanTest {

    private static final String HSA_ID = "hsa-1";
    private static final String PNR = "19121212-1212";

    @Mock
    GetEmployeeIncludingProtectedPersonResponderInterface getEmployeeService;

    @InjectMocks
    private EmployeeServiceBean testee;

    @Test
    public void testOk() throws ExternalServiceCallException {
        when(getEmployeeService.getEmployeeIncludingProtectedPerson(anyString(), any(GetEmployeeIncludingProtectedPersonType.class)))
                .thenReturn(buildResponse(ResultCodeEnum.OK));
        List<PersonInformationType> response = testee.getEmployee(HSA_ID, null, null);
        assertNotNull(response);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBothPnrAndHsaIdSupplied() throws ExternalServiceCallException {
        testee.getEmployee(HSA_ID, PNR, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPnrOrHsaIdSupplied() throws ExternalServiceCallException {
        testee.getEmployee(null, null, null);
    }

    @Test(expected = ExternalServiceCallException.class)
    public void testWebServiceExceptionIsThrownForNoOkResponse() throws ExternalServiceCallException {
        when(getEmployeeService.getEmployeeIncludingProtectedPerson(anyString(), any(GetEmployeeIncludingProtectedPersonType.class)))
                .thenReturn(buildResponse(ResultCodeEnum.ERROR));
       testee.getEmployee(HSA_ID, null, null);
    }

    @Test(expected = ExternalServiceCallException.class)
    public void testWebServiceExceptionIsThrownForSoapFault() throws ExternalServiceCallException {
        SOAPFaultException ex = mock(SOAPFaultException.class);
        when(getEmployeeService.getEmployeeIncludingProtectedPerson(anyString(), any(GetEmployeeIncludingProtectedPersonType.class)))
                .thenThrow(ex);
        testee.getEmployee(HSA_ID, null, null);
    }

    private GetEmployeeIncludingProtectedPersonResponseType buildResponse(ResultCodeEnum resultCode) {
        GetEmployeeIncludingProtectedPersonResponseType resp = new GetEmployeeIncludingProtectedPersonResponseType();
        resp.setResultCode(resultCode);
        return resp;
    }
}
