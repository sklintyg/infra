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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.infra.integration.hsa.stub.GetHealthCareUnitMembersResponderStub;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

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
        when(stub.getHealthCareUnitMembers(any(), any(GetHealthCareUnitMembersType.class)))
            .thenReturn(buildHealthCareUnitMembersResponse());

        ReflectionTestUtils
            .setField(((Advised) organizationUnitService).getTargetSource().getTarget(), "getHealthCareUnitMembersResponderInterface",
                stub);

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
