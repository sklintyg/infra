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

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareProviderRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareProviderResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceHealthCareProviderClientTest {

    private static final Unit UNIT = new Unit();

    private static final String HSA_ID = "hsaId";

    private static final String ORG_NO = "ORG_NO";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HsaIntygProxyServiceHealthCareProviderClient hsaIntygProxyServiceHealthCareProviderClient;

    @Test
    void shouldThrowHsaServiceCallException() {
        final var request = GetHealthCareProviderRequestDTO.builder().build();
        when(restTemplate.postForObject(anyString(), eq(request), eq(GetHealthCareProviderRequestDTO.class)))
            .thenThrow(IllegalStateException.class);

        assertThrows(IllegalStateException.class, () -> hsaIntygProxyServiceHealthCareProviderClient.get(request));
    }

    @Test
    void shouldReturnResponse() {
        final var expectedResponse = GetHealthCareProviderResponseDTO.builder()
            .healthCareProviders(Collections.emptyList())
            .build();
        final var request = GetHealthCareProviderRequestDTO.builder()
            .hsaId(HSA_ID)
            .build();
        when(restTemplate.postForObject(anyString(), eq(request), eq(GetHealthCareProviderResponseDTO.class)))
            .thenReturn(expectedResponse);

        final var result = hsaIntygProxyServiceHealthCareProviderClient.get(request);

        assertEquals(expectedResponse, result);
    }
}