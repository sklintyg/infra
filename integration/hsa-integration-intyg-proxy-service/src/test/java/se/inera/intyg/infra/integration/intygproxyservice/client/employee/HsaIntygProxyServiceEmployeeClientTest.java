/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.intygproxyservice.client.employee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.HsaRestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.HsaRestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.HsaRestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.HsaRestClientConfig.TRACE_ID_KEY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;
import se.inera.intyg.infra.integration.intygproxyservice.dto.employee.GetEmployeeRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.employee.GetEmployeeResponseDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntygProxyServiceEmployeeClientTest {

    @Mock
    private RestClient restClient;

    @InjectMocks
    private HsaIntygProxyServiceEmployeeClient hsaIntygProxyServiceEmployeeClient;

    private static final String PERSONAL_IDENTITY_NUMBER = "personalIdentityNumber";

    private RequestBodyUriSpec requestBodyUriSpec;
    private ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        final var uri = "/api/from/configuration";
        ReflectionTestUtils.setField(hsaIntygProxyServiceEmployeeClient, "employeeEndpoint", uri);

        requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);

        MDC.put(TRACE_ID_KEY, "traceId");
        MDC.put(SESSION_ID_KEY, "sessionId");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(GetEmployeeRequestDTO.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(LOG_TRACE_ID_HEADER, "traceId")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(LOG_SESSION_ID_HEADER, "sessionId")).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void shallReturnGetCitizenCertificatesResponse() {
        final var request = GetEmployeeRequestDTO.builder()
            .personId(PERSONAL_IDENTITY_NUMBER)
            .build();

        final var expectedResponse = GetEmployeeResponseDTO.builder().build();

        doReturn(expectedResponse).when(responseSpec).body(GetEmployeeResponseDTO.class);

        final var actualResponse = hsaIntygProxyServiceEmployeeClient.getEmployee(request);

        assertEquals(expectedResponse, actualResponse);
    }
}
