package se.inera.intyg.infra.pu.integration.intygproxyservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PURestClientConfig {

    public static final String LOG_TRACE_ID_HEADER = "x-trace-id";
    public static final String LOG_SESSION_ID_HEADER = "x-session-id";

    public static final String SESSION_ID_KEY = "session.id";
    public static final String TRACE_ID_KEY = "trace.id";

    @Value("${integration.intygproxyservice.baseurl}")
    private String intygProxyServiceBaseUrl;

    @Bean(name = "puIntygProxyServiceRestClient")
    public RestClient ipsRestClient() {
        return RestClient.create(intygProxyServiceBaseUrl);
    }
}