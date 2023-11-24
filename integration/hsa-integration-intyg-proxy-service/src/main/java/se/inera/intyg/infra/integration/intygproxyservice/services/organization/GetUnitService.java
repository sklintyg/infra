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

package se.inera.intyg.infra.integration.intygproxyservice.services.organization;

import static se.inera.intyg.infra.integration.intygproxyservice.constants.HsaIntygProxyServiceConstants.UNIT_MEMBERS_CACHE_NAME;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.intygproxyservice.client.organization.HsaIntygProxyServiceUnitClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitResponseDTO;

@Service
@RequiredArgsConstructor
public class GetUnitService {

    private final HsaIntygProxyServiceUnitClient hsaIntygProxyServiceUnitClient;

    @Cacheable(cacheNames = UNIT_MEMBERS_CACHE_NAME, key = "#getUnitRequestDTO.hsaId",
        unless = "#result == null")
    public Unit get(GetUnitRequestDTO getUnitRequestDTO) throws HsaServiceCallException {
        validateRequest(getUnitRequestDTO);
        final var getUnitResponseDTO = hsaIntygProxyServiceUnitClient.getUnit(getUnitRequestDTO);
        validateResponse(getUnitResponseDTO, getUnitRequestDTO.getHsaId());
        return getUnitResponseDTO.getUnit();
    }

    private void validateRequest(GetUnitRequestDTO getUnitRequestDTO) {
        if (getUnitRequestDTO.getHsaId() == null || getUnitRequestDTO.getHsaId().isEmpty()) {
            throw new IllegalArgumentException("hsaId is a required field");
        }
    }

    private void validateResponse(GetUnitResponseDTO getUnitResponseDTO, String hsaId) throws HsaServiceCallException {
        if (getUnitResponseDTO == null || getUnitResponseDTO.getUnit() == null) {
            throw new HsaServiceCallException(String.format("Could not get unit with hsaId: '%s'", hsaId));
        }
    }
}
