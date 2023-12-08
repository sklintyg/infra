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

package se.inera.intyg.infra.integration.intygproxyservice.services.authorization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import javax.xml.ws.WebServiceException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.intygproxyservice.dto.authorization.GetCredentialInformationRequestDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntegrationAuthorizationManagementServiceTest {

    private static final String PERSON_HSA_ID = "personHsaId";
    private static final String PERSON_ID = "personId";
    @Mock
    private GetCredentialInformationForPersonService credentialInformationForPersonService;

    @Mock
    private GetHospCredentialsForPersonService getHospCredentialsForPersonService;

    @InjectMocks
    private HsaIntegrationAuthorizationManagementService hsaIntegrationAuthorizationManagementService;

    @Nested
    class GetCredentialInformationForPerson {

        @Test
        void shouldReturnListOfCredentialInformation() {
            final var expectedResponse = List.of(new CredentialInformation());

            final var request = GetCredentialInformationRequestDTO.builder()
                .personHsaId(PERSON_HSA_ID)
                .build();
            when(credentialInformationForPersonService.get(request)).thenReturn(expectedResponse);

            final var result = hsaIntegrationAuthorizationManagementService.getCredentialInformationForPerson(null,
                PERSON_HSA_ID, null);

            assertEquals(expectedResponse, result);
        }
    }

    @Nested
    class GetHospCredentialsForPersonResponseType {

        @Test
        void shouldThrowIfCredentialsForPersonIsNull() {
            when(getHospCredentialsForPersonService.get(PERSON_ID)).thenReturn(null);
            assertThrows(WebServiceException.class,
                () -> hsaIntegrationAuthorizationManagementService.getHospCredentialsForPersonResponseType(PERSON_ID));
        }

        @Test
        void shouldReturnCredentialsForPerson() {
            final var expectedResult = new HospCredentialsForPerson();

            when(getHospCredentialsForPersonService.get(PERSON_ID)).thenReturn(expectedResult);

            final var result = hsaIntegrationAuthorizationManagementService.getHospCredentialsForPersonResponseType(
                PERSON_ID);

            assertEquals(expectedResult, result);
        }
    }
}
