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

package se.inera.intyg.infra.integration.intygproxyservice.client.authorization;

import static org.junit.Assert.assertThrows;
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
import se.inera.intyg.infra.integration.hsatk.model.Result;
import se.inera.intyg.infra.integration.intygproxyservice.dto.authorization.GetHospCertificationPersonRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.authorization.GetHospCertificationPersonResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceHospCertificationPersonClientTest {

    private static final String PERSON_ID = "personId";
    private static final String CERTIFICATION_ID = "certificationId";
    private static final Result RESULT = new Result();
    public static final String OPERATION = "operation";
    public static final String REASON = "reason";

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HsaIntygProxyServiceHospCertificationPersonClient hospCertificationPersonClient;

    @Test
    void shouldReturnGetHospCertificationPersonResponse() {

        final var request = GetHospCertificationPersonRequestDTO.builder()
            .personId(PERSON_ID)
            .certificationId(CERTIFICATION_ID)
            .operation(OPERATION)
            .reason(REASON)
            .build();

        final var expectedResponse = GetHospCertificationPersonResponseDTO.builder()
            .result(RESULT)
            .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(GetHospCertificationPersonResponseDTO.class))).thenReturn(
            expectedResponse);

        final var result = hospCertificationPersonClient.get(request);

        assertEquals(expectedResponse, result);
    }

    @Test
    void shouldThrowIllegalStateException() {
        final var request = GetHospCertificationPersonRequestDTO.builder().build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(GetHospCertificationPersonResponseDTO.class))).thenThrow(
            IllegalStateException.class);

        assertThrows(IllegalStateException.class, () -> hospCertificationPersonClient.get(request));
    }
}
