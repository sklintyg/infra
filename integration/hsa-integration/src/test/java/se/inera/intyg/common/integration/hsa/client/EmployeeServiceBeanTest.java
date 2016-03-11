package se.inera.intyg.common.integration.hsa.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v1.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void testOk() {
        when(getEmployeeService.getEmployeeIncludingProtectedPerson(anyString(), any(GetEmployeeIncludingProtectedPersonType.class)))
                .thenReturn(buildResponse(ResultCodeEnum.OK));
        GetEmployeeIncludingProtectedPersonResponseType response = testee.getEmployee(HSA_ID, null, null);
        assertNotNull(response);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBothPnrAndHsaIdSupplied() {
        testee.getEmployee(HSA_ID, PNR, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPnrOrHsaIdSupplied() {
        testee.getEmployee(null, null, null);
    }

    @Test(expected = WebServiceException.class)
    public void testWebServiceExceptionIsThrownForNoOkResponse() {
        when(getEmployeeService.getEmployeeIncludingProtectedPerson(anyString(), any(GetEmployeeIncludingProtectedPersonType.class)))
                .thenReturn(buildResponse(ResultCodeEnum.ERROR));
       testee.getEmployee(HSA_ID, null, null);
    }

    @Test(expected = WebServiceException.class)
    public void testWebServiceExceptionIsThrownForSoapFault() {
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
