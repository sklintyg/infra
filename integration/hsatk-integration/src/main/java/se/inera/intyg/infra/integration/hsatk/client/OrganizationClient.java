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
package se.inera.intyg.infra.integration.hsatk.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
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
import se.riv.infrastructure.directory.organization.getunitresponder.v2.ProfileEnum;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.UnitType;

@Service
public class OrganizationClient {

    @Autowired
    private GetHealthCareProviderResponderInterface getHealthCareProviderResponderInterface;

    @Autowired
    private GetHealthCareUnitResponderInterface getHealthCareUnitResponderInterface;

    @Autowired
    private GetHealthCareUnitMembersResponderInterface getHealthCareUnitMembersResponderInterface;

    @Autowired
    private GetUnitResponderInterface getUnitResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    private static boolean includeFeignedObject = false;

    @Cacheable(cacheResolver = "hsaCacheResolver", key = "#healthCareProviderHsaId + #healthCareProviderHsaId", unless = "#result == null")
    public List<HealthCareProviderType> getHealthCareProvider(
        String healthCareProviderHsaId, String healthCareProviderOrgNo)
        throws HsaServiceCallException {

        GetHealthCareProviderType parameters = new GetHealthCareProviderType();

        parameters.setHealthCareProviderHsaId(healthCareProviderHsaId);
        parameters.setHealthCareProviderOrgNo(healthCareProviderOrgNo);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetHealthCareProviderResponseType response =
            getHealthCareProviderResponderInterface.getHealthCareProvider(logicalAddress, parameters);

        if (response == null || response.getHealthCareProvider().isEmpty()) {
            System.out.println("Response is null or empty");
            throw new HsaServiceCallException("Could not GetHealthCareProvider for hsaId " + healthCareProviderHsaId);
        }

        return response.getHealthCareProvider();
    }

    @Cacheable(cacheResolver = "hsaCacheResolver", key = "#healthCareUnitHsaId", unless = "#result == null")
    public HealthCareUnitType getHealthCareUnit(String healthCareUnitHsaId)
        throws HsaServiceCallException {

        GetHealthCareUnitType parameters = new GetHealthCareUnitType();

        parameters.setHealthCareUnitMemberHsaId(healthCareUnitHsaId);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetHealthCareUnitResponseType response = getHealthCareUnitResponderInterface.getHealthCareUnit(logicalAddress, parameters);

        if (response.getHealthCareUnit() == null) {
            System.out.println("Response is null");
            throw new HsaServiceCallException("Could not GetHealthCareUnit for healthCareUnitHsaId " + healthCareUnitHsaId);
        }

        return response.getHealthCareUnit();
    }

    @Cacheable(cacheResolver = "hsaCacheResolver", key = "#healthCareUnitMemberHsaId", unless = "#result == null")
    public HealthCareUnitMembersType getHealthCareUnitMembers(String healthCareUnitMemberHsaId)
        throws HsaServiceCallException {
        GetHealthCareUnitMembersType parameters = new GetHealthCareUnitMembersType();

        parameters.setHealthCareUnitHsaId(healthCareUnitMemberHsaId);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetHealthCareUnitMembersResponseType response = getHealthCareUnitMembersResponderInterface
            .getHealthCareUnitMembers(logicalAddress, parameters);

        if (response == null || response.getHealthCareUnitMembers() == null) {
            System.out.println("Response is null");
            throw new HsaServiceCallException(
                "Could not GetHealthCareUnitMembers for healthCareUnitMemberHsaId " + healthCareUnitMemberHsaId);
        }

        return response.getHealthCareUnitMembers();
    }

    @Cacheable(cacheResolver = "hsaCacheResolver",
        key = "#unitHsaId + (#profile != null ? #profile.name() : '')",
        unless = "#result == null")
    public UnitType getUnit(String unitHsaId, ProfileEnum profile)
        throws HsaServiceCallException {

        GetUnitType parameters = new GetUnitType();

        parameters.setUnitHsaId(unitHsaId);
        parameters.setProfile(profile);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetUnitResponseType response = getUnitResponderInterface.getUnit(logicalAddress, parameters);

        if (response == null || response.getUnit() == null) {
            System.out.println("Response is null");
            throw new HsaServiceCallException("Could not GetUnit for unitHsaId " + unitHsaId);
        }

        return response.getUnit();
    }
}
