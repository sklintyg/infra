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

import static se.inera.intyg.infra.integration.intygproxyservice.constants.HsaIntygProxyServiceConstants.CARE_PROVIDER_OF_CARE_UNIT_CACHE_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.intygproxyservice.client.HsaIntygProxyServiceCareProviderOfCareUnitClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetCareProviderOfCareUnitRequestDTO;

@Service
@RequiredArgsConstructor
public class GetCareProviderOfCareUnitService {

    private final HsaIntygProxyServiceCareProviderOfCareUnitClient hsaIntygProxyServiceCareProviderOfCareUnitClient;

    @Cacheable(cacheNames = CARE_PROVIDER_OF_CARE_UNIT_CACHE_NAME, key = "#getCareProviderOfCareUnitRequestDTO.careUnitHsaId", unless = "#result == null")
    public String get(GetCareProviderOfCareUnitRequestDTO getCareProviderOfCareUnitRequestDTO) throws HsaServiceCallException {
        validateRequestParameters(getCareProviderOfCareUnitRequestDTO);
        final var careProviderOfCareUnit = hsaIntygProxyServiceCareProviderOfCareUnitClient.getCareProviderOfCareUnit(
            getCareProviderOfCareUnitRequestDTO);

        return careProviderOfCareUnit.getHealthCareUnit().getHealthCareProviderHsaId();
    }

    private void validateRequestParameters(GetCareProviderOfCareUnitRequestDTO getCareProviderOfCareUnitRequestDTO) {
        if (hsaIdIsNullOrEmpty(getCareProviderOfCareUnitRequestDTO)) {
            throw new IllegalArgumentException("Missing required parameters. Must provide careUnitHsaId.");
        }
    }

    private static boolean hsaIdIsNullOrEmpty(GetCareProviderOfCareUnitRequestDTO getCareProviderOfCareUnitRequestDTO) {
        return getCareProviderOfCareUnitRequestDTO.getCareUnitHsaId() == null || getCareProviderOfCareUnitRequestDTO.getCareUnitHsaId()
            .isEmpty();
    }
}
