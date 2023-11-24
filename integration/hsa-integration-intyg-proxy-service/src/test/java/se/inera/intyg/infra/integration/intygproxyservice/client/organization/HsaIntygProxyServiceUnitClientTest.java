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

package se.inera.intyg.infra.integration.intygproxyservice.client.organization;

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
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceUnitClientTest {

    private static final Unit UNIT = new Unit();
    private static final String HSA_ID = "hsaId";
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HsaIntygProxyServiceUnitClient hsaIntygProxyServiceUnitClient;

    @Test
    void shouldThrowHsaServiceCallException() {
        final var request = GetUnitRequestDTO.builder().build();
        when(restTemplate.postForObject(anyString(), eq(request), eq(GetUnitResponseDTO.class))).thenThrow(IllegalStateException.class);
        assertThrows(HsaServiceCallException.class, () -> hsaIntygProxyServiceUnitClient.getUnit(request));
    }

    @Test
    void shouldReturnGetUnitResponseDTO() throws HsaServiceCallException {
        final var expectedResponse = GetUnitResponseDTO.builder()
            .unit(UNIT)
            .build();

        final var request = GetUnitRequestDTO.builder()
            .hsaId(HSA_ID)
            .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(GetUnitResponseDTO.class))).thenReturn(expectedResponse);

        final var result = hsaIntygProxyServiceUnitClient.getUnit(request);

        assertEquals(expectedResponse, result);
    }
}
