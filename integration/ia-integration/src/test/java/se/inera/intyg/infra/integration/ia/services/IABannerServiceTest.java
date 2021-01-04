/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.ia.services;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.driftbannerdto.Application;
import se.inera.intyg.infra.driftbannerdto.Banner;
import se.inera.intyg.infra.driftbannerdto.BannerPriority;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration("classpath:test-context.xml")
@ActiveProfiles({"test"})
public class IABannerServiceTest {

    private static ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private IABannerServiceImpl service;

    @Autowired
    private Cache iaCache;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeClass
    public static void init() {
        mapper.registerModule(new JavaTimeModule()
            .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Banner.FORMAT)))
            .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Banner.FORMAT))));
    }

    @Before
    public void setup() {
        iaCache.clear();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testGetBanner() {
        List<Banner> banners = service.getCurrentBanners();

        assertTrue(banners.isEmpty());
    }

    @Test
    public void testLoadBannerEmpty() throws URISyntaxException, JsonProcessingException {
        List<Banner> banners = new ArrayList<>();

        mockServer.expect(ExpectedCount.once(),
            requestTo(new URI("http://localhost:8170/actuator/banner/WEBCERT")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(banners))
            );

        List<Banner> loadedBanners = service.loadBanners(Application.WEBCERT);
        mockServer.verify();

        assertEquals(banners.size(), loadedBanners.size());
    }

    @Test
    public void testLoadBanners() throws URISyntaxException, JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners = new ArrayList<>();

        Banner banner = new Banner(UUID.randomUUID(), now, Application.WEBCERT, "test msg", now.minusDays(10), now.plusDays(10),
            BannerPriority.HOG);
        banners.add(banner);

        mockServer.expect(ExpectedCount.once(),
            requestTo(new URI("http://localhost:8170/actuator/banner/WEBCERT")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withStatus(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(mapper.writeValueAsString(banners))
            );

        List<Banner> loadedBanners = service.loadBanners(Application.WEBCERT);
        mockServer.verify();

        assertEquals(banners.size(), loadedBanners.size());

        List<Banner> bannersFromStore = service.getCurrentBanners();

        assertEquals(1, bannersFromStore.size());
    }
}
