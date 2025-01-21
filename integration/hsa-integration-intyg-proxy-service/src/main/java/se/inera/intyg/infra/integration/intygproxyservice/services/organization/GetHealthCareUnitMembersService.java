/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.intygproxyservice.services.organization;

import static se.inera.intyg.infra.integration.intygproxyservice.constants.HsaIntygProxyServiceConstants.HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.intygproxyservice.client.organization.HsaIntygProxyServiceHealthCareUnitMembersClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitMembersRequestDTO;

@Service
@RequiredArgsConstructor
public class GetHealthCareUnitMembersService {

    private final HsaIntygProxyServiceHealthCareUnitMembersClient healthCareUnitMembersClient;

    @Cacheable(cacheNames = HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME, key = "#getHealthCareUnitMembersRequest.hsaId",
        unless = "#result == null")
    public HealthCareUnitMembers get(GetHealthCareUnitMembersRequestDTO getHealthCareUnitMembersRequest) {
        validateRequest(getHealthCareUnitMembersRequest.getHsaId());
        final var healthCareUnitMembersResponse = healthCareUnitMembersClient.get(getHealthCareUnitMembersRequest);
        return healthCareUnitMembersResponse.getHealthCareUnitMembers();
    }

    private void validateRequest(String hsaId) {
        if (hsaId == null || hsaId.isEmpty()) {
            throw new IllegalArgumentException("HsaId for unit was not provided '" + hsaId + "'");
        }
    }
}
