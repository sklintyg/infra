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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMember;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetHealthCareUnitMembersRequestDTO;

@ExtendWith(MockitoExtension.class)
class GetHealthCareUnitMemberHsaIdServiceTest {

    @Mock
    private GetHealthCareUnitMembersService getHealthCareUnitMembersService;
    @InjectMocks
    private GetHealthCareUnitMemberHsaIdService getHealthCareUnitMemberHsaIdService;

    private static final String HSA_ID = "hsaId";
    private static final String HEALTH_CARE_UNIT_MEMBER_HSA_ID_1 = "healthCareUnitMemberHsaId1";
    private static final String HEALTH_CARE_UNIT_MEMBER_HSA_ID_2 = "healthCareUnitMemberHsaId3";

    @Test
    void shouldReturnAEmptyListIfResponseIsNull() {
        final var request = GetHealthCareUnitMembersRequestDTO.builder().hsaId(HSA_ID).build();
        when(getHealthCareUnitMembersService.getHealthCareUnitMembers(request)).thenReturn(null);
        final var result = getHealthCareUnitMemberHsaIdService.getHealthCareUnitMembersHsaIds(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAEmptyListIfListOfHealthCareUnitMemberIsNull() {
        final var request = GetHealthCareUnitMembersRequestDTO.builder().hsaId(HSA_ID).build();
        final var healthCareUnitMembers = new HealthCareUnitMembers();
        when(getHealthCareUnitMembersService.getHealthCareUnitMembers(request)).thenReturn(healthCareUnitMembers);
        final var result = getHealthCareUnitMemberHsaIdService.getHealthCareUnitMembersHsaIds(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAEmptyListIfListOfHealthCareUnitMemberIsEmpty() {
        final var request = GetHealthCareUnitMembersRequestDTO.builder().hsaId(HSA_ID).build();
        final var healthCareUnitMembers = new HealthCareUnitMembers();
        healthCareUnitMembers.setHealthCareUnitMember(Collections.emptyList());
        when(getHealthCareUnitMembersService.getHealthCareUnitMembers(request)).thenReturn(healthCareUnitMembers);
        final var result = getHealthCareUnitMemberHsaIdService.getHealthCareUnitMembersHsaIds(request);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnListOfHsaIdsExtractedFromHealthCareUnitMembers() {
        final var request = GetHealthCareUnitMembersRequestDTO.builder().hsaId(HSA_ID).build();
        final var healthCareUnitMembers = new HealthCareUnitMembers();
        final var healthCareUnitMember1 = getHealthCareUnitMember(HEALTH_CARE_UNIT_MEMBER_HSA_ID_1);
        final var healthCareUnitMember2 = getHealthCareUnitMember(HEALTH_CARE_UNIT_MEMBER_HSA_ID_2);
        healthCareUnitMembers.setHealthCareUnitMember(List.of(healthCareUnitMember1, healthCareUnitMember2));
        final var expectedResult = List.of(HEALTH_CARE_UNIT_MEMBER_HSA_ID_1, HEALTH_CARE_UNIT_MEMBER_HSA_ID_2);
        when(getHealthCareUnitMembersService.getHealthCareUnitMembers(request)).thenReturn(healthCareUnitMembers);
        final var result = getHealthCareUnitMemberHsaIdService.getHealthCareUnitMembersHsaIds(request);
        assertEquals(expectedResult, result);
    }

    private static HealthCareUnitMember getHealthCareUnitMember(String hsaId) {
        final var healthCareUnitMember = new HealthCareUnitMember();
        healthCareUnitMember.setHealthCareUnitMemberHsaId(hsaId);
        return healthCareUnitMember;
    }
}