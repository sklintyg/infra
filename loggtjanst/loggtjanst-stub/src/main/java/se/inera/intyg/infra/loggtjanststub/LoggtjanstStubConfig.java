/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.loggtjanststub;

import java.util.List;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.spring.JAXRSServerFactoryBeanDefinitionParser.SpringJAXRSServerFactoryBean;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import se.inera.intyg.infra.loggtjanststub.json.LogStoreObjectMapper;

@Configuration
@EnableWebMvc
public class LoggtjanstStubConfig {

    @Autowired
    @Qualifier(value = Bus.DEFAULT_BUS_ID)
    private Bus cxfBus;

    @Bean
    @Profile({"dev", "wc-all-stubs", "wc-loggtjanst-stub"})
    public LogStore logStore() {
        return new LogStore();
    }

    @Bean
    @Profile({"dev", "wc-all-stubs", "wc-loggtjanst-stub"})
    public StubState stubState() {
        return new StubState();
    }

    @Bean
    @Profile({"dev", "wc-all-stubs", "wc-loggtjanst-stub"})
    public LogStoreObjectMapper logStoreObjectMapper() {
        return new LogStoreObjectMapper();
    }

    @Bean
    @Profile({"dev", "wc-all-stubs", "wc-loggtjanst-stub"})
    public StoreLogStubResponder storeLogStubResponder() {
        return new StoreLogStubResponder();
    }

    @Bean
    @Profile({"dev", "wc-all-stubs", "wc-loggtjanst-stub"})
    public EndpointImpl wsEndpoint() {
        EndpointImpl wsEndpoint = new EndpointImpl(cxfBus, storeLogStubResponder());
        wsEndpoint.publish("/stubs/informationsecurity/auditing/log/StoreLog/v2/rivtabp21");
        return wsEndpoint;
    }

    @Bean
    @Profile({"dev", "testability-api"})
    public LoggtjanstStubRestApi loggtjanstStubRestApi() {
        return new LoggtjanstStubRestApi();
    }

    @Bean
    @Profile({"dev", "testability-api"})
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureMessageConverters(
                List<HttpMessageConverter<?>> messageConverters) {
                messageConverters.add(new StringHttpMessageConverter());
                messageConverters.add(new ResourceHttpMessageConverter());
                messageConverters.add(mappingJackson2HttpMessageConverter());
            }
        };
    }

    @Bean
    @Profile({"dev", "testability-api"})
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(logStoreObjectMapper());
        return mappingJackson2HttpMessageConverter;
    }
}
