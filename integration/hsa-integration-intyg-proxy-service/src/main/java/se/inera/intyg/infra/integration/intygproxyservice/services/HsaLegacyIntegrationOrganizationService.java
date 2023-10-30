/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.intygproxyservice.services;

import static se.inera.intyg.infra.integration.hsatk.constants.HsaIntegrationApiConstants.HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE;
import static se.inera.intyg.infra.integration.intygproxyservice.constants.HsaIntygProxyServiceConstans.HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME;

import java.util.List;
import javax.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserAuthorizationInfo;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.infra.integration.intygproxyservice.client.HsaIntygProxyServiceHealthCareUnitMembersClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetHealthCareUnitMembersRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE)
public class HsaLegacyIntegrationOrganizationService implements HsaOrganizationsService {

    private final HsaIntygProxyServiceHealthCareUnitMembersClient organizationClient;

    @Override
    public UserAuthorizationInfo getAuthorizedEnheterForHosPerson(String hosPersonHsaId) {
        return null;
    }

    @Override
    public String getVardgivareOfVardenhet(String vardenhetHsaId) {
        return null;
    }

    @Override
    public Vardenhet getVardenhet(String vardenhetHsaId) {
        return null;
    }

    @Override
    public Vardgivare getVardgivareInfo(String vardgivareHsaId) {
        return null;
    }

    @Override
    @Cacheable(cacheNames = HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME, key = "#vardEnhetHsaId", unless = "#result == null")
    public List<String> getHsaIdForAktivaUnderenheter(String vardEnhetHsaId) {
        try {
            final var healthCareUnitMemberHsaId = organizationClient.getHealthCareUnitMemberHsaIds(
                GetHealthCareUnitMembersRequestDTO.builder()
                    .hsaId(vardEnhetHsaId)
                    .build()
            );
            return healthCareUnitMemberHsaId.getHsaIds();
        } catch (HsaServiceCallException e) {
            log.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
    }

    @Override
    public String getParentUnit(String hsaId) throws HsaServiceCallException {
        return null;
    }
}
