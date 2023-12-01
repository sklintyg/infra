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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.Commission;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitMembersRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.services.organization.converter.CareUnitConverter;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetCareUnitService {

    private final GetUnitService getUnitService;
    private final GetHealthCareUnitMembersService getHealthCareUnitMembersService;
    private final CareUnitConverter careUnitConverter;

    public Vardenhet get(Commission commission) {
        final var unit = getUnitFromHsa(commission.getHealthCareUnitHsaId());
        final var members = getHealthCareUnitMembersFromHsa(commission.getHealthCareUnitHsaId());
        final var isUnitDefined = unit.getUnitHsaId() != null && !unit.getUnitHsaId().isEmpty();

        if (!isUnitDefined) {
            return null;
        }

        return careUnitConverter.convert(commission, unit, members);
    }

    private Unit getUnitFromHsa(String careUnitHsaId) {
        return getUnitService.get(
            GetUnitRequestDTO.builder()
                .hsaId(careUnitHsaId)
                .build()
        );
    }

    private HealthCareUnitMembers getHealthCareUnitMembersFromHsa(String unitId) {
        return getHealthCareUnitMembersService.get(
            GetHealthCareUnitMembersRequestDTO.builder()
                .hsaId(unitId)
                .build()
        );
    }
}
