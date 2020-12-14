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
package se.inera.intyg.infra.integration.hsatk.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.client.OrganizationClient;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProvider;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.util.HsaTypeConverter;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GetUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.ProfileEnum;

@Service
public class HsatkOrganizationServiceImpl implements HsatkOrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(HsatkAuthorizationManagementServiceImpl.class);

    HsaTypeConverter hsaTypeConverter = new HsaTypeConverter();

    @Autowired
    OrganizationClient organizationClient;

    @Override
    public List<HealthCareProvider> getHealthCareProvider(String healthCareProviderHsaId, String healthCareProviderOrgNo) {
        List<HealthCareProvider> healthCareProviderList = new ArrayList<>();

        try {
            healthCareProviderList = organizationClient.getHealthCareProvider(healthCareProviderHsaId, healthCareProviderOrgNo)
                    .stream().map(hsaTypeConverter::toHealthCareProvider).collect(Collectors.toList());
        } catch (HsaServiceCallException e) {
            LOG.error("", e);
        } catch (Exception e) {
            LOG.error("Unexpected error occured: ", e);
        }
        return healthCareProviderList;
    }

    @Override
    public HealthCareUnit getHealthCareUnit(String healthCareUnitMemberHsaId) {
        HealthCareUnit healthCareUnit = new HealthCareUnit();

        try {
            healthCareUnit = hsaTypeConverter.toHealthCareUnit(organizationClient.getHealthCareUnit(healthCareUnitMemberHsaId));
        } catch (HsaServiceCallException e) {
            LOG.error("", e);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return healthCareUnit;
    }

    @Override
    public HealthCareUnitMembers getHealthCareUnitMembers(String healtCareUnitHsaId) {
        HealthCareUnitMembers healthCareUnitMembers = new HealthCareUnitMembers();
        try {
            healthCareUnitMembers = hsaTypeConverter.toHealthCareUnitMembers(
                    organizationClient.getHealthCareUnitMembers(healtCareUnitHsaId));
        } catch (HsaServiceCallException e) {
            LOG.error("", e);
        } catch (Exception e) {
            LOG.error("Unexpected error occurred while getting HealthCareUnitMembers : {}", e.getLocalizedMessage());
        }
        return healthCareUnitMembers;
    }

    @Override
    public Unit getUnit(String unitHsaId, String profile) {
        ProfileEnum profileEnum = ProfileEnum.BASIC;
        GetUnitType getUnitType = new GetUnitType();
        getUnitType.setUnitHsaId(unitHsaId);

        Unit unit = new Unit();

        if (StringUtils.isNotEmpty(profile)) {
            profileEnum = ProfileEnum.fromValue(profile);
        }
        try {
            unit = hsaTypeConverter.toUnit(organizationClient.getUnit(unitHsaId, profileEnum));

        } catch (HsaServiceCallException e) {
            LOG.error("Failed to get Unit from HSA: {}", e.getLocalizedMessage());

        } catch (Exception e) {
            LOG.error("Unexpected error occurred during getUnit method: {}", e.getLocalizedMessage());
        }
        return unit;
    }

}
