package se.inera.intyg.infra.pu.integration.intygproxyservice.client;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.infra.pu.integration.intygproxyservice.configuration.PURestClientConfig;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonsRequestDTO;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonsResponseDTO;

@Service
public class GetPersonsIntygProxyServiceClient {

    @Autowired
    @Qualifier("puIntygProxyServiceRestClient")
    private RestClient ipsRestClient;
    @Value("${integration.intygproxyservice.persons.endpoint}")
    private String personsEndpoint;

    public PersonsResponseDTO get(PersonsRequestDTO request) {
        return ipsRestClient
            .post()
            .uri(personsEndpoint)
            .body(request)
            .header(PURestClientConfig.LOG_TRACE_ID_HEADER, MDC.get(PURestClientConfig.TRACE_ID_KEY))
            .header(PURestClientConfig.LOG_SESSION_ID_HEADER, MDC.get(PURestClientConfig.SESSION_ID_KEY))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(PersonsResponseDTO.class);
    }
}