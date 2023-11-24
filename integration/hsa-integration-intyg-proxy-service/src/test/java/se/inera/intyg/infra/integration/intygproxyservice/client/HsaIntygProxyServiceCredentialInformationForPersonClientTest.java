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

package se.inera.intyg.infra.integration.intygproxyservice.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetCredentialInformationRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetCredentialInformationResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceCredentialInformationForPersonClientTest {

    private static final List<CredentialInformation> CREDENTIAL_INFORMATIONS = List.of(new CredentialInformation());
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private HsaIntygProxyServiceCredentialInformationForPersonClient credentialInformationForPersonClient;

    @Test
    void shouldReturnGetCredentialInformationResponse() {
        final var request = GetCredentialInformationRequestDTO.builder()
            .personHsaId("personHsaId")
            .build();

        final var expectedResponse = GetCredentialInformationResponseDTO.builder()
            .credentialInformations(CREDENTIAL_INFORMATIONS)
            .build();

        when(restTemplate.postForObject(anyString(), eq(request), eq(GetCredentialInformationResponseDTO.class))).thenReturn(
            expectedResponse);

        final var result = credentialInformationForPersonClient.get(request);

        assertEquals(expectedResponse, result);
    }
}
