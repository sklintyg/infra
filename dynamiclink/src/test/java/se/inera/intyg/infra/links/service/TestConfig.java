package se.inera.intyg.infra.links.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by eriklupander on 2017-05-03.
 */
@Configuration
@ComponentScan("se.inera.intyg.infra.dynamiclink")
@PropertySource(
        value={"classpath:/test.properties"},
        ignoreResourceNotFound = false)
public class TestConfig {
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
