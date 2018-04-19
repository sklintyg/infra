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
package se.inera.intyg.infra.integration.pu.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;

import javax.annotation.PostConstruct;

/**
 * While the cacheManager.getCache(...) isn't strictly necessary for creating the cache used by
 * {@link se.inera.intyg.infra.integration.pu.services.PUServiceImpl}, this class provides us with the capability
 * of configuring individual caches based on the current state of the (dynamic) configuration
 * <p>
 * Created by eriklupander on 2016-10-20.
 */
public class PuCacheConfiguration {

    public static final String PERSON_CACHE_NAME = "personCache";
    private static final String PU_CACHE_EXPIRY = "pu.cache.expiry";

    @Value("${" + PU_CACHE_EXPIRY + "}")
    private String personCacheExpirySeconds;

    @Autowired
    private RedisCacheOptionsSetter redisCacheOptionsSetter;

    @PostConstruct
    public void init() {
        redisCacheOptionsSetter.createCache(PERSON_CACHE_NAME, PU_CACHE_EXPIRY);
    }

}
