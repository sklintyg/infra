package se.inera.intyg.infra.pu.integration.intygproxyservice.client;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import se.inera.intyg.infra.pu.integration.intygproxyservice.configuration.PURestClientConfig;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonRequestDTO;
import se.inera.intyg.infra.pu.integration.intygproxyservice.dto.PersonResponseDTO;

@Service
public class GetPersonIntygProxyServiceClient {

    @Autowired
    @Qualifier("puIntygProxyServiceRestClient")
    private RestClient ipsRestClient;

    @Value("${integration.intygproxyservice.person.endpoint}")
    private String personEndpoint;

    public PersonResponseDTO get(PersonRequestDTO request) {
        return ipsRestClient
            .post()
            .uri(personEndpoint)
            .body(request)
            .header(PURestClientConfig.LOG_TRACE_ID_HEADER, MDC.get(PURestClientConfig.TRACE_ID_KEY))
            .header(PURestClientConfig.LOG_SESSION_ID_HEADER, MDC.get(PURestClientConfig.SESSION_ID_KEY))
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(PersonResponseDTO.class);
    }
}