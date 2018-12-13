/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.postnummer.service;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.intyg.infra.integration.postnummer.model.Omrade;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PostnummerServiceTest.TestConfiguration.class)
public class PostnummerServiceTest {

    @Configuration
    @ComponentScan("se.inera.intyg.infra.integration.postnummer")
    @PropertySource(
            value={"classpath:/test.properties"},
            ignoreResourceNotFound = false)
    public static class TestConfiguration {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertiesResolver() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }

    @Autowired
    PostnummerService postnummerService;

    @Test
    public void testGetPostnummer() {

        List<Omrade> omrade13061 = Arrays.asList(new Omrade("13061", "HÅRSFJÄRDEN", "HANINGE", "STOCKHOLM"));
        List<Omrade> omrade13100 = Arrays.asList(new Omrade("13100", "NACKA", "NACKA", "STOCKHOLM"));
        List<Omrade> omrade13155 = Arrays.asList(new Omrade("13155", "NACKA", "STOCKHOLM", "STOCKHOLM"),
                new Omrade("13155", "NACKA", "NACKA", "STOCKHOLM"));

        assertNull(postnummerService.getOmradeByPostnummer(null));
        assertNull(postnummerService.getOmradeByPostnummer(""));
        assertNull(postnummerService.getOmradeByPostnummer("xxyy"));
        assertThat(postnummerService.getOmradeByPostnummer("13061"), is(omrade13061));
        assertThat(postnummerService.getOmradeByPostnummer("13100"), is(omrade13100));
        assertThat(postnummerService.getOmradeByPostnummer("13155"), is(omrade13155));
        assertThat(postnummerService.getOmradeByPostnummer("13155"), not(omrade13061));
    }

    @Test
    public void testGetKommunList() {
        List<String> verify = Arrays.asList("HANINGE", "NACKA", "STOCKHOLM", "VÄSTERVIK", "LINKÖPING");

        assertThat(postnummerService.getKommunList(), is(verify));
    }
}
