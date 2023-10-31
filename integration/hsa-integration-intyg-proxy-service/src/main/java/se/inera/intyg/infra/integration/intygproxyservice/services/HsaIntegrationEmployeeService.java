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
import static se.inera.intyg.infra.integration.intygproxyservice.constants.HsaIntygProxyServiceConstants.EMPLOYEE_CACHE_NAME;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.services.HsatkEmployeeService;
import se.inera.intyg.infra.integration.intygproxyservice.client.HsaIntygProxyServiceEmployeeClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetEmployeeRequestDTO;

@Slf4j
@Service
@Profile(HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE)
@RequiredArgsConstructor
public class HsaIntegrationEmployeeService implements HsatkEmployeeService {

    private final HsaIntygProxyServiceEmployeeClient hsaIntygProxyServiceEmployeeClient;

    @Override
    public List<PersonInformation> getEmployee(String personalIdentityNumber, String personHsaId) {
        return getEmployee(personalIdentityNumber, personHsaId, null);
    }

    @Override
    @Cacheable(cacheNames = EMPLOYEE_CACHE_NAME, key = "#personId + #personHsaId", unless = "#result == null")
    public List<PersonInformation> getEmployee(String personId, String personHsaId, String profile) {
        try {
            final var getEmployeeResponse = hsaIntygProxyServiceEmployeeClient.getEmployee(
                GetEmployeeRequestDTO.builder()
                    .personId(personId)
                    .hsaId(personHsaId)
                    .build()
            );
            return getEmployeeResponse.getEmployee().getPersonInformation();
        } catch (HsaServiceCallException exception) {
            log.warn("HsaServiceCallException thrown: {}", exception);
            return new ArrayList<>();
        }
    }
}
