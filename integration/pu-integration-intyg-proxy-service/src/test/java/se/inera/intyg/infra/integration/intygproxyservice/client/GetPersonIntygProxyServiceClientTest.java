package se.inera.intyg.infra.integration.intygproxyservice.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.TRACE_ID_KEY;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;
import se.inera.intyg.infra.integration.api.model.Person;
import se.inera.intyg.infra.integration.api.model.PersonSvar.Status;
import se.inera.intyg.infra.integration.intygproxyservice.dto.PersonRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.PersonResponseDTO;

class GetPersonIntygProxyServiceClientTest {

    private static final String ENDPOINT = "endpoint";
    private static final String TRACE_ID = "traceId";
    private static final String SESSION_ID = "sessionId";
    private final RestClient restClient = mock(RestClient.class);
    private final RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
    private final RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
    private final RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);
    private GetPersonIntygProxyServiceClient getPersonIntygProxyServiceClient;

    @BeforeEach
    void setUp() {
        getPersonIntygProxyServiceClient = new GetPersonIntygProxyServiceClient(restClient);
        ReflectionTestUtils.setField(getPersonIntygProxyServiceClient, "personEndpoint", ENDPOINT);
        MDC.put(TRACE_ID_KEY, TRACE_ID);
        MDC.put(SESSION_ID_KEY, SESSION_ID);
    }

    @Test
    void shallReturnPersonResponse() {
        final var request = PersonRequestDTO.builder()
            .personId("personId")
            .queryCache(true)
            .build();

        final var expectedResponse = PersonResponseDTO.builder()
            .status(Status.FOUND)
            .person(mock(Person.class))
            .build();

        doReturn(requestBodyUriSpec).when(restClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(ENDPOINT);
        doReturn(requestBodySpec).when(requestBodySpec).body(request);
        doReturn(requestBodySpec).when(requestBodySpec).header(LOG_TRACE_ID_HEADER, TRACE_ID);
        doReturn(requestBodySpec).when(requestBodySpec).header(LOG_SESSION_ID_HEADER, SESSION_ID);
        doReturn(requestBodySpec).when(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(expectedResponse).when(responseSpec).body(PersonResponseDTO.class);

        final var response = getPersonIntygProxyServiceClient.get(request);

        assertEquals(expectedResponse, response);
    }

    @Test
    void shallSetHeadersCorrectly() {
        final var request = PersonRequestDTO.builder()
            .personId("personId")
            .queryCache(true)
            .build();

        doReturn(requestBodyUriSpec).when(restClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(ENDPOINT);
        doReturn(requestBodySpec).when(requestBodySpec).body(request);
        doReturn(requestBodySpec).when(requestBodySpec).header(LOG_TRACE_ID_HEADER, TRACE_ID);
        doReturn(requestBodySpec).when(requestBodySpec).header(LOG_SESSION_ID_HEADER, SESSION_ID);
        doReturn(requestBodySpec).when(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(mock(PersonResponseDTO.class)).when(responseSpec).body(PersonResponseDTO.class);

        getPersonIntygProxyServiceClient.get(request);

        verify(requestBodySpec).header(LOG_TRACE_ID_HEADER, TRACE_ID);
        verify(requestBodySpec).header(LOG_SESSION_ID_HEADER, SESSION_ID);
    }

    @Test
    void shallSetContentTypeAsApplicationJson() {
        final var request = PersonRequestDTO.builder()
            .personId("personId")
            .queryCache(true)
            .build();

        doReturn(requestBodyUriSpec).when(restClient).post();
        doReturn(requestBodySpec).when(requestBodyUriSpec).uri(ENDPOINT);
        doReturn(requestBodySpec).when(requestBodySpec).body(request);
        doReturn(requestBodySpec).when(requestBodySpec).header(LOG_TRACE_ID_HEADER, TRACE_ID);
        doReturn(requestBodySpec).when(requestBodySpec).header(LOG_SESSION_ID_HEADER, SESSION_ID);
        doReturn(requestBodySpec).when(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(mock(PersonResponseDTO.class)).when(responseSpec).body(PersonResponseDTO.class);

        getPersonIntygProxyServiceClient.get(request);

        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
    }
}