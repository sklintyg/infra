/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.organization.gethealthcareprovider.v1.rivtabp21.GetHealthCareProviderResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.GetHealthCareProviderResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.GetHealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.HealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v2.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v2.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunit.v2.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GetUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.UnitType;

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

    @Mock
    private GetHealthCareProviderResponderInterface getHealthCareProviderResponderInterface;

    @InjectMocks
    private OrganizationUnitServiceBean testee;

    @Test
    public void testGetUnit() throws HsaServiceCallException {
        when(getUnitResponderInterface.getUnit(
            or(isNull(), anyString()),
            any(GetUnitType.class))
        ).thenReturn(buildGetUnitResponse());

        UnitType response = testee.getUnit(UNIT_HSA_ID);
        assertNotNull(response);
        assertEquals(TEST, response.getUnitHsaId());
    }

    @Test
    public void testGetHealthCareUnit() throws HsaServiceCallException {
        when(getHealthCareUnitResponderInterface.getHealthCareUnit(
            or(isNull(), anyString()),
            any(GetHealthCareUnitType.class))
        ).thenReturn(buildHealthCareGetUnitResponse());

        HealthCareUnitType response = testee.getHealthCareUnit(UNIT_HSA_ID);
        assertNotNull(response);
        assertEquals(TEST, response.getHealthCareUnitHsaId());
    }

    @Test
    public void testHealthCareUnitMembers() throws HsaServiceCallException {
        when(getHealthCareUnitMembersResponderInterface.getHealthCareUnitMembers(
            or(isNull(), anyString()),
            any(GetHealthCareUnitMembersType.class))
        ).thenReturn(buildHealthCareUnitMembersResponse());

        HealthCareUnitMembersType response = testee.getHealthCareUnitMembers(UNIT_HSA_ID);
        assertNotNull(response);
        assertEquals(TEST, response.getHealthCareUnitHsaId());
    }

    @Test
    public void getHealthCareProvider() throws HsaServiceCallException {
        when(getHealthCareProviderResponderInterface.getHealthCareProvider(
            or(isNull(), anyString()),
            any(GetHealthCareProviderType.class))
        ).thenReturn(buildHealthCareProviderResponse());

        List<HealthCareProviderType> response = testee.getHealthCareProvider(UNIT_HSA_ID);
        assertNotNull(response);
        assertEquals(TEST, response.get(0).getHealthCareProviderHsaId());
    }

    private GetHealthCareProviderResponseType buildHealthCareProviderResponse() {
        GetHealthCareProviderResponseType resp = new GetHealthCareProviderResponseType();
        HealthCareProviderType healthCareProviderType = new HealthCareProviderType();
        healthCareProviderType.setHealthCareProviderHsaId(TEST);
        resp.getHealthCareProvider().add(healthCareProviderType);
        return resp;
    }

    private GetHealthCareUnitMembersResponseType buildHealthCareUnitMembersResponse() {
        GetHealthCareUnitMembersResponseType resp = new GetHealthCareUnitMembersResponseType();
        HealthCareUnitMembersType healthCareUnitMembers = new HealthCareUnitMembersType();
        healthCareUnitMembers.setHealthCareUnitHsaId(TEST);
        resp.setHealthCareUnitMembers(healthCareUnitMembers);
        return resp;
    }

    private GetHealthCareUnitResponseType buildHealthCareGetUnitResponse() {
        GetHealthCareUnitResponseType resp = new GetHealthCareUnitResponseType();
        HealthCareUnitType healthCareUnit = new HealthCareUnitType();
        healthCareUnit.setHealthCareUnitHsaId(TEST);
        resp.setHealthCareUnit(healthCareUnit);
        return resp;
    }

    private GetUnitResponseType buildGetUnitResponse() {
        GetUnitResponseType resp = new GetUnitResponseType();
        UnitType unit = new UnitType();
        unit.setUnitHsaId(TEST);
        resp.setUnit(unit);
        return resp;
    }
}
