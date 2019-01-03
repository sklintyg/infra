/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.pu.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;

/**
 * While the cacheManager.getCache(...) isn't strictly necessary for creating the cache used by
 * {@link se.inera.intyg.infra.integration.pu.services.PUService}, this class provides us with the capability
 * of configuring individual caches based on the current state of the (dynamic) configuration
 * <p>
 * Created by eriklupander on 2016-10-20.
 */
@Configuration
public class PuCacheConfiguration {

    @Value("${app.name:noname}")
    private String appName;

    @Value("${pu.cache.expiry}")
    private String personCacheExpirySeconds;

    @Autowired
    private RedisCacheOptionsSetter redisCacheOptionsSetter;

    @Bean
    public Cache puCache() {
        return redisCacheOptionsSetter.createCache("personCache:" + appName, personCacheExpirySeconds);
    }
}
