/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.intygproxyservice.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.intygproxyservice.client.organization.HsaIntygProxyServiceHealthCareUnitMembersClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitMembersRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitMembersResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceHealthCareUnitMembersClientTest {

    private static final String HSA_ID = "hsaId";
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private HsaIntygProxyServiceHealthCareUnitMembersClient healthCareUnitMembersClient;

    @Test
    void shouldThrowIllegalStateException() {
        final var request = GetHealthCareUnitMembersRequestDTO.builder()
            .hsaId(HSA_ID)
            .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(GetHealthCareUnitMembersResponseDTO.class))).thenThrow(
            IllegalArgumentException.class
        );

        assertThrows(IllegalStateException.class, () -> healthCareUnitMembersClient.getHealthCareUnitMembers(request));
    }

    @Test
    void shouldReturnGetHealthCareUnitMembers() {
        final var request = GetHealthCareUnitMembersRequestDTO.builder()
            .hsaId(HSA_ID)
            .build();
        final var expectedResponse = GetHealthCareUnitMembersResponseDTO.builder()
            .healthCareUnitMembers(new HealthCareUnitMembers())
            .build();
        when(restTemplate.postForObject(anyString(), eq(request), eq(GetHealthCareUnitMembersResponseDTO.class))).thenReturn(
            expectedResponse
        );
        final var result = healthCareUnitMembersClient.getHealthCareUnitMembers(request);
        assertEquals(expectedResponse, result);
    }
}
