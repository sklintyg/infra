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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMember;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetHealthCareUnitMembersRequestDTO;

@Service
@RequiredArgsConstructor
public class GetHealthCareUnitMemberHsaIdService {

    private final GetHealthCareUnitMembersService getHealthCareUnitMembersService;

    public List<String> get(GetHealthCareUnitMembersRequestDTO getHealthCareUnitMembersRequest) {
        final var healthCareUnitMembers = getHealthCareUnitMembersService.get(getHealthCareUnitMembersRequest);
        if (responseIsNullOrEmpty(healthCareUnitMembers)) {
            return Collections.emptyList();
        }
        return healthCareUnitMembers.getHealthCareUnitMember().stream()
            .map(HealthCareUnitMember::getHealthCareUnitMemberHsaId)
            .collect(Collectors.toList());
    }

    private static boolean responseIsNullOrEmpty(HealthCareUnitMembers healthCareUnitMembers) {
        return healthCareUnitMembers == null || healthCareUnitMembers.getHealthCareUnitMember() == null
            || healthCareUnitMembers.getHealthCareUnitMember().isEmpty();
    }
}
