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
package se.inera.intyg.infra.integration.hsa.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;

import javax.annotation.PostConstruct;

/**
 * While the cacheManager.getCache(...) isn't strictly necessary for creating the cache used by
 * {@link se.inera.intyg.infra.integration.hsa.client.OrganizationUnitServiceBean}, this class provides us with the capability
 * of configuring individual caches based on the current state of the (dynamic) configuration
 * <p>
 * Created by eriklupander on 2016-10-20.
 */
public class HsaCacheConfiguration {

    public static final String HSA_UNIT_CACHE_NAME = "hsaUnitCache";
    public static final String HSA_HEALTH_CARE_UNIT_CACHE_NAME = "hsaHealthCareUnitCache";
    public static final String HSA_HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME = "hsaHealthCareUnitMembersCache";

    private static final String HSA_UNIT_CACHE_EXPIRY = "hsa.unit.cache.expiry";
    private static final String HSA_HEALTHCAREUNIT_CACHE_EXPIRY = "hsa.healthcareunit.cache.expiry";
    private static final String HSA_HEALHCAREUNITMEMBERS_CACHE_EXPIRY = "hsa.healhcareunitmembers.cache.expiry";

    @Value("${" + HSA_UNIT_CACHE_EXPIRY + "}")
    private String hsaUnitCacheExpirySeconds;

    @Value("${" + HSA_HEALTHCAREUNIT_CACHE_EXPIRY + "}")
    private String hsaHealthCareUnitCacheExpirySeconds;

    @Value("${" + HSA_HEALHCAREUNITMEMBERS_CACHE_EXPIRY + "}")
    private String hsaHeathCareUnitMembersCacheExpirySeconds;

    @Autowired
    private RedisCacheOptionsSetter redisCacheOptionsSetter;

    @PostConstruct
    public void init() {
        redisCacheOptionsSetter.createCache(HSA_UNIT_CACHE_NAME, hsaUnitCacheExpirySeconds);
        redisCacheOptionsSetter.createCache(HSA_HEALTH_CARE_UNIT_CACHE_NAME, hsaHealthCareUnitCacheExpirySeconds);
        redisCacheOptionsSetter.createCache(HSA_HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME, hsaHeathCareUnitMembersCacheExpirySeconds);
    }
}
