package se.inera.intyg.infra.integration.intygproxyservice.client;

import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.LOG_SESSION_ID_HEADER;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.LOG_TRACE_ID_HEADER;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.SESSION_ID_KEY;
import static se.inera.intyg.infra.integration.intygproxyservice.configuration.RestClientConfig.TRACE_ID_KEY;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.infra.integration.intygproxyservice.dto.PersonsRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.PersonsResponseDTO;

@Service
public class GetPersonsIntygProxyServiceClient {

    private final RestClient ipsRestClient;
    @Value("${integration.intygproxyservice.persons.endpoint}")
    private String personsEndpoint;

    public GetPersonsIntygProxyServiceClient(@Qualifier("puIntygProxyServiceRestClient") RestClient ipsRestClient) {
        this.ipsRestClient = ipsRestClient;
    }

    public PersonsResponseDTO get(PersonsRequestDTO request) {
        return ipsRestClient
            .post()
            .uri(personsEndpoint)
            .body(request)
            .header(LOG_TRACE_ID_HEADER, MDC.get(TRACE_ID_KEY))
            .header(LOG_SESSION_ID_HEADER, MDC.get(SESSION_ID_KEY))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(PersonsResponseDTO.class);
    }
}