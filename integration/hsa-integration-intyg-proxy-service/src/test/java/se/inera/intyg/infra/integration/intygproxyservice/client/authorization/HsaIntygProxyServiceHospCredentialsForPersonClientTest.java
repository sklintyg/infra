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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import jakarta.xml.ws.WebServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.intygproxyservice.dto.authorization.GetCredentialsForPersonRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.authorization.GetCredentialsForPersonResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceHospCredentialsForPersonClientTest {

    private static final GetCredentialsForPersonRequestDTO GET_CREDENTIALS_FOR_PERSON_REQUEST_DTO =
        GetCredentialsForPersonRequestDTO.builder()
            .personId("personId")
            .build();
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HsaIntygProxyServiceHospCredentialsForPersonClient credentialsForPersonClient;

    @Test
    void shouldThrowIllegalStateException() {
        when(restTemplate.postForObject(anyString(), eq(GET_CREDENTIALS_FOR_PERSON_REQUEST_DTO),
            eq(GetCredentialsForPersonResponseDTO.class))).thenThrow(
            WebServiceException.class);

        assertThrows(IllegalStateException.class, () -> credentialsForPersonClient.get(GET_CREDENTIALS_FOR_PERSON_REQUEST_DTO));
    }

    @Test
    void shouldReturnGetCredentialsForPersonResponse() {
        final var expectedResponse = GetCredentialsForPersonResponseDTO.builder()
            .credentials(new HospCredentialsForPerson())
            .build();

        when(restTemplate.postForObject(anyString(), eq(GET_CREDENTIALS_FOR_PERSON_REQUEST_DTO),
            eq(GetCredentialsForPersonResponseDTO.class))).thenReturn(expectedResponse);

        final var result = credentialsForPersonClient.get(GET_CREDENTIALS_FOR_PERSON_REQUEST_DTO);

        assertEquals(expectedResponse, result);
    }
}
