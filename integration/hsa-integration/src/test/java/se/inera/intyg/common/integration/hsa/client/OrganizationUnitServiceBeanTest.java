package se.inera.intyg.common.integration.hsa.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v1.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v1.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.GetHealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunit.v1.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.GetUnitType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2016-03-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationUnitServiceBeanTest {

    private static final String UNIT_HSA_ID = "hsa-1";
    @Mock
    private GetUnitResponderInterface getUnitResponderInterface;

    @Mock
    private GetHealthCareUnitResponderInterface getHealthCareUnitResponderInterface;

    @Mock
    private GetHealthCareUnitMembersResponderInterface getHealthCareUnitMembersResponderInterface;

    @InjectMocks
    private OrganizationUnitServiceBean testee;

    @Test
    public void testGetUnit() {
        when(getUnitResponderInterface.getUnit(anyString(), any(GetUnitType.class)))
                .thenReturn(buildGetUnitResponse());
        GetUnitResponseType response = testee.getUnit(UNIT_HSA_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());
    }

    private GetUnitResponseType buildGetUnitResponse() {
        GetUnitResponseType resp = new GetUnitResponseType();
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }

    @Test
    public void testGetHealthCareUnit() {
        when(getHealthCareUnitResponderInterface.getHealthCareUnit(anyString(), any(GetHealthCareUnitType.class)))
                .thenReturn(buildHealthCareGetUnitResponse());
        GetHealthCareUnitResponseType response = testee.getHealthCareUnit(UNIT_HSA_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());
    }

    private GetHealthCareUnitResponseType buildHealthCareGetUnitResponse() {
        GetHealthCareUnitResponseType resp = new GetHealthCareUnitResponseType();
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }

    @Test
    public void testHealthCareUnitMembers() {
        when(getHealthCareUnitMembersResponderInterface.getHealthCareUnitMembers(anyString(), any(GetHealthCareUnitMembersType.class)))
                .thenReturn(buildHealthCareUnitMembersResponse());
        GetHealthCareUnitMembersResponseType response = testee.getHealthCareUnitMembers(UNIT_HSA_ID);
        assertEquals(ResultCodeEnum.OK, response.getResultCode());
    }

    private GetHealthCareUnitMembersResponseType buildHealthCareUnitMembersResponse() {
        GetHealthCareUnitMembersResponseType resp = new GetHealthCareUnitMembersResponseType();
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }
}
