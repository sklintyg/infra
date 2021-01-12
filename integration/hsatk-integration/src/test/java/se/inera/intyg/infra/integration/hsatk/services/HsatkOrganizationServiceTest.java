/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.client.OrganizationClient;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProvider;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.HealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMemberType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.ProfileEnum;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.UnitType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HsatkOrganizationServiceTest {

    private static final String HSA_ID = "HSA_ID";

    @Mock
    OrganizationClient organizationClient;

    @InjectMocks
    HsatkOrganizationServiceImpl organizationService;

    @Test
    public void testGetHealthCareProviderOK() throws HsaServiceCallException {
        when(organizationClient.getHealthCareProvider(anyString(), any())).thenReturn(buildHealthCareProviderResponse());
        List<HealthCareProvider> healthCareProviders = organizationService.getHealthCareProvider(HSA_ID, null);

        Assert.assertNotNull(healthCareProviders);
    }

    @Test
    public void testGetHealthCareProviderHsaServiceException() throws HsaServiceCallException {
        when(organizationClient.getHealthCareProvider(anyString(), any())).thenThrow(new HsaServiceCallException());
        List<HealthCareProvider> healthCareProviders = organizationService.getHealthCareProvider(HSA_ID, null);

        Assert.assertNotNull(healthCareProviders);
        Assert.assertEquals(0, healthCareProviders.size());
    }

    @Test
    public void testGetHealthCareUnitOK() throws HsaServiceCallException {
        when(organizationClient.getHealthCareUnit(anyString())).thenReturn(buildHealthCareUnit());
        HealthCareUnit healthCareUnit = organizationService.getHealthCareUnit(HSA_ID);

        Assert.assertNotNull(healthCareUnit);
    }

    @Test
    public void testGetHealthCareUnitHsaServiceCallException() throws HsaServiceCallException {
        when(organizationClient.getHealthCareUnit(anyString())).thenThrow(new HsaServiceCallException());
        HealthCareUnit healthCareUnit = organizationService.getHealthCareUnit(HSA_ID);

        Assert.assertNotNull(healthCareUnit);
        Assert.assertNull(healthCareUnit.getHealthCareUnitHsaId());
    }

    @Test
    public void testGetHealthCareUnitMembersOK() throws HsaServiceCallException {
        when(organizationClient.getHealthCareUnitMembers(anyString())).thenReturn(buildHealthCareUnitMembersResponse());
        HealthCareUnitMembers healthCareUnitMembers = organizationService.getHealthCareUnitMembers(HSA_ID);

        Assert.assertNotNull(healthCareUnitMembers);
    }

    @Test
    public void testGetHealthCareUnitMembersHsaServiceCallException() throws HsaServiceCallException {
        when(organizationClient.getHealthCareUnitMembers(anyString())).thenThrow(new HsaServiceCallException());
        HealthCareUnitMembers healthCareUnitMembers = organizationService.getHealthCareUnitMembers(HSA_ID);

        Assert.assertNotNull(healthCareUnitMembers);
        Assert.assertNull(healthCareUnitMembers.getHealthCareUnitHsaId());
    }

    @Test
    public void testGetUnitOK() throws HsaServiceCallException {
        when(organizationClient.getUnit(eq(HSA_ID), any())).thenReturn(buildUnitResponse());
        Unit unit = organizationService.getUnit(HSA_ID, ProfileEnum.ALL.value());

        Assert.assertNotNull(unit);
    }

    @Test
    public void testGetUnitHsaServiceCallException() throws HsaServiceCallException {
        when(organizationClient.getUnit(eq(HSA_ID), any())).thenThrow(new HsaServiceCallException());
        Unit unit = organizationService.getUnit(HSA_ID, ProfileEnum.ALL.value());

        Assert.assertNotNull(unit);
        Assert.assertNull(unit.getUnitHsaId());
    }

    private List<HealthCareProviderType> buildHealthCareProviderResponse() {
        List<HealthCareProviderType> response = new ArrayList<>();

        HealthCareProviderType healthCareProviderType = new HealthCareProviderType();


        return response;
    }

    private HealthCareUnitType buildHealthCareUnit() {
        HealthCareUnitType response = new HealthCareUnitType();

        return response;
    }

    private HealthCareUnitMembersType buildHealthCareUnitMembersResponse() {
        HealthCareUnitMembersType response = new HealthCareUnitMembersType();
        response.getHealthCareUnitMember().add(new HealthCareUnitMemberType());
        return response;
    }

    private UnitType buildUnitResponse() {
        UnitType response = new UnitType();


        return response;
    }

}
