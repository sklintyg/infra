package se.inera.intyg.common.integration.hsa.caching;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.common.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.common.integration.hsa.stub.GetHealthCareUnitMembersResponderStub;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v1.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.aop.framework.Advised;

/**
 * Created by eriklupander on 2016-10-19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:HsaOrganizationServiceCachingTest/test-caching-context.xml")
public class HsaOrganizationServiceCachingTest {

    private static final String UNIT_HSA_ID = "hsa-1";

    @Autowired
    private OrganizationUnitService organizationUnitService;

    @Test
    public void testHealthCareUnitMembersUsesCaching() throws Exception {
        GetHealthCareUnitMembersResponderStub stub = mock(GetHealthCareUnitMembersResponderStub.class);
        when(stub.getHealthCareUnitMembers(any(), any(GetHealthCareUnitMembersType.class))).thenReturn(buildHealthCareUnitMembersResponse());

        ReflectionTestUtils.setField(((Advised) organizationUnitService).getTargetSource().getTarget(), "getHealthCareUnitMembersResponderInterface", stub);

        HealthCareUnitMembersType healthCareUnitMembers = organizationUnitService.getHealthCareUnitMembers(UNIT_HSA_ID);
        assertNotNull(healthCareUnitMembers);

        healthCareUnitMembers = organizationUnitService.getHealthCareUnitMembers(UNIT_HSA_ID);
        assertNotNull(healthCareUnitMembers);

        verify(stub, times(1)).getHealthCareUnitMembers(anyString(), any(GetHealthCareUnitMembersType.class));
    }

    private GetHealthCareUnitMembersResponseType buildHealthCareUnitMembersResponse() {
        GetHealthCareUnitMembersResponseType resp = new GetHealthCareUnitMembersResponseType();
        HealthCareUnitMembersType healthCareUnitMembers = new HealthCareUnitMembersType();
        healthCareUnitMembers.setHealthCareUnitHsaId(UNIT_HSA_ID);
        resp.setHealthCareUnitMembers(healthCareUnitMembers);
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }

}
