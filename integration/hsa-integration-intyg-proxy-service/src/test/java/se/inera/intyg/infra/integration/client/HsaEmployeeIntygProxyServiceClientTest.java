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
import se.inera.intyg.infra.integration.intygproxyservice.client.HsaEmployeeIntygProxyServiceClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetEmployeeRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetEmployeeResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaEmployeeIntygProxyServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HsaEmployeeIntygProxyServiceClient hsaEmployeeIntygProxyServiceClient;
    private static final String INTYG_PROXY_SERVICE_ENDPOINT_GET_EMPLOYEE_URL = "http://localhost:18020/api/v1/person";

    private static final String PERSONAL_IDENTITY_NUMBER = "personalIdentityNumber";
    private static final String PERSON_HSA_ID = "personHsaId";

    @Test
    void shouldThrowIfMissingPersonalIdentityNumberAndPersonHsaId() {
        final var request = GetEmployeeRequestDTO.builder()
            .personId(null)
            .personHsaId(null)
            .build();
        assertThrows(IllegalArgumentException.class, () -> hsaEmployeeIntygProxyServiceClient.getEmployee(request));
    }

    @Test
    void shouldThrowIfBothPersonalIdentityNumberAndPersonHsaIdIsProvided() {
        final var request = GetEmployeeRequestDTO.builder()
            .personId(PERSONAL_IDENTITY_NUMBER)
            .personHsaId(PERSON_HSA_ID)
            .build();
        assertThrows(IllegalArgumentException.class, () -> hsaEmployeeIntygProxyServiceClient.getEmployee(request));
    }

    @Test
    void shouldReturnGetEmployeeResponseDTOWhenPersonalIdentityNumberIsProvided() throws HsaServiceCallException {
        final var expectedResult = GetEmployeeResponseDTO.builder().build();
        final var request = GetEmployeeRequestDTO.builder()
            .personId(PERSONAL_IDENTITY_NUMBER)
            .build();

        when(restTemplate.postForObject(
            anyString(),
            eq(request),
            eq(GetEmployeeResponseDTO.class))
        ).thenReturn(expectedResult);

        final var response = hsaEmployeeIntygProxyServiceClient.getEmployee(request);
        assertEquals(expectedResult, response);
    }

    @Test
    void shouldReturnGetEmployeeResponseDTOWhenPersonHsaIdIsProvided() throws HsaServiceCallException {
        final var expectedResult = GetEmployeeResponseDTO.builder().build();
        final var request = GetEmployeeRequestDTO.builder()
            .personHsaId(PERSON_HSA_ID)
            .build();

        when(restTemplate.postForObject(
            anyString(),
            eq(request),
            eq(GetEmployeeResponseDTO.class))
        ).thenReturn(expectedResult);

        final var response = hsaEmployeeIntygProxyServiceClient.getEmployee(request);
        assertEquals(expectedResult, response);
    }
}
