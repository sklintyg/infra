/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.Ignition;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import se.inera.intyg.infra.cache.core.ConfigurableCache;
import se.inera.intyg.infra.integration.hsa.client.OrganizationUnitServiceBean;

/**
 * While the cacheManager.getCache(...) isn't strictly necessary for creating the cache used by
 * {@link OrganizationUnitServiceBean}, this class
 * provides us with the capability of configuring individual caches based on the current state of the
 * {@link org.apache.ignite.cache.spring.SpringCacheManager#dynamicCacheCfg}
 *
 * Created by eriklupander on 2016-10-20.
 */
public class HsaCacheConfiguration implements ConfigurableCache {

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
    private SpringCacheManager cacheManager;

    @PostConstruct
    public void init() {
        Duration hsaUnitDuration = buildDuration(hsaUnitCacheExpirySeconds, HSA_UNIT_CACHE_EXPIRY);
        Duration hsaHealthCareUnitDuration = buildDuration(hsaHealthCareUnitCacheExpirySeconds, HSA_HEALTHCAREUNIT_CACHE_EXPIRY);
        Duration hsaHealthCareUnitMembersDuration = buildDuration(hsaHeathCareUnitMembersCacheExpirySeconds, HSA_HEALHCAREUNITMEMBERS_CACHE_EXPIRY);

        initCache(HSA_UNIT_CACHE_NAME, hsaUnitDuration);
        initCache(HSA_HEALTH_CARE_UNIT_CACHE_NAME, hsaHealthCareUnitDuration);
        initCache(HSA_HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME, hsaHealthCareUnitMembersDuration);
    }

    @PreDestroy
    public void tearDown() {
        Ignition.stopAll(false);
    }
    private void initCache(String cacheName, Duration duration) {
        cacheManager.getDynamicCacheConfiguration().setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
        cacheManager.getCache(cacheName);
    }
}
