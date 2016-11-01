package se.inera.intyg.common.integration.hsa.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v1.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v1.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.GetHealthCareUnitType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunit.v1.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.GetUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.UnitType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2016-03-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrganizationUnitServiceBeanTest {

    private static final String TEST = "TEST";
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
    public void testGetUnit() throws ExternalServiceCallException {
        when(getUnitResponderInterface.getUnit(anyString(), any(GetUnitType.class)))
                .thenReturn(buildGetUnitResponse());
        UnitType response = testee.getUnit(UNIT_HSA_ID);
        assertNotNull(response);
        assertEquals(TEST, response.getUnitHsaId());
    }

    private GetUnitResponseType buildGetUnitResponse() {
        GetUnitResponseType resp = new GetUnitResponseType();
        UnitType unit = new UnitType();
        unit.setUnitHsaId(TEST);
        resp.setUnit(unit);
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }

    @Test
    public void testGetHealthCareUnit() throws ExternalServiceCallException {
        when(getHealthCareUnitResponderInterface.getHealthCareUnit(anyString(), any(GetHealthCareUnitType.class)))
                .thenReturn(buildHealthCareGetUnitResponse());
        HealthCareUnitType response = testee.getHealthCareUnit(UNIT_HSA_ID);
        assertNotNull(response);
        assertEquals(TEST, response.getHealthCareUnitHsaId());
    }

    private GetHealthCareUnitResponseType buildHealthCareGetUnitResponse() {
        GetHealthCareUnitResponseType resp = new GetHealthCareUnitResponseType();
        HealthCareUnitType healthCareUnit = new HealthCareUnitType();
        healthCareUnit.setHealthCareUnitHsaId(TEST);
        resp.setHealthCareUnit(healthCareUnit);
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }

    @Test
    public void testHealthCareUnitMembers() throws ExternalServiceCallException {
        when(getHealthCareUnitMembersResponderInterface.getHealthCareUnitMembers(anyString(), any(GetHealthCareUnitMembersType.class)))
                .thenReturn(buildHealthCareUnitMembersResponse());
        HealthCareUnitMembersType response = testee.getHealthCareUnitMembers(UNIT_HSA_ID);
        assertNotNull(response);
        assertEquals(TEST, response.getHealthCareUnitHsaId());
    }

    private GetHealthCareUnitMembersResponseType buildHealthCareUnitMembersResponse() {
        GetHealthCareUnitMembersResponseType resp = new GetHealthCareUnitMembersResponseType();
        HealthCareUnitMembersType healthCareUnitMembers = new HealthCareUnitMembersType();
        healthCareUnitMembers.setHealthCareUnitHsaId(TEST);
        resp.setHealthCareUnitMembers(healthCareUnitMembers);
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }
}
