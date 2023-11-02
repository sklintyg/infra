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

package se.inera.intyg.infra.integration.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import se.inera.intyg.infra.integration.intygproxyservice.client.HsaIntygProxyServiceHealthCareUnitClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetHealthCareUnitRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.HealthCareUnitResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceHealthCareUnitClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HsaIntygProxyServiceHealthCareUnitClient hsaIntygProxyServiceHealthCareUnitClient;

    private static final String CARE_UNIT_HSA_ID = "careUnitHsaId";

    @Test
    void shouldReturnGetHealthCareUnitResponseWhenHsaIdIsProvided() throws HsaServiceCallException {
        final var request = GetHealthCareUnitRequestDTO.builder()
            .hsaId(CARE_UNIT_HSA_ID)
            .build();

        final var expectedResponse = HealthCareUnitResponseDTO.builder().build();

        when(restTemplate.postForObject(
            anyString(),
            eq(request),
            eq(HealthCareUnitResponseDTO.class))
        ).thenReturn(expectedResponse);

        final var actualResponse = hsaIntygProxyServiceHealthCareUnitClient.getHealthCareUnit(request);
        assertEquals(expectedResponse, actualResponse);
    }
}