package se.inera.intyg.common.integration.hsa.cache;

import javax.annotation.PostConstruct;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import org.apache.ignite.cache.spring.SpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
// import org.springframework.context.annotation.Profile;

/**
 * While the cacheManager.getCache(...) isn't strictly necessary for creating the cache used by
 * {@link se.inera.intyg.webcert.integration.pu.services.PUServiceImpl}, this class provides us with the capability
 * of configuring individual caches based on the current state of the
 * {@link org.apache.ignite.cache.spring.SpringCacheManager#dynamicCacheCfg}
 *
 * Created by eriklupander on 2016-10-20.
 */
public class HsaCacheConfiguration {

    public static final String HSA_UNIT_CACHE_NAME = "hsaUnitCache";
    public static final String HSA_HEALTH_CARE_UNIT_CACHE_NAME = "hsaHealthCareUnitCache";
    public static final String HSA_HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME = "hsaHealthCareUnitMembersCache";

    private Duration defaultPersonCacheExpiry = Duration.ONE_MINUTE;

    @Autowired
    private SpringCacheManager cacheManager;

    @PostConstruct
    public void init() {
        cacheManager.getDynamicCacheConfiguration().setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(defaultPersonCacheExpiry));
        cacheManager.getCache(HSA_UNIT_CACHE_NAME);
        cacheManager.getCache(HSA_HEALTH_CARE_UNIT_CACHE_NAME);
        cacheManager.getCache(HSA_HEALTH_CARE_UNIT_MEMBERS_CACHE_NAME);
    }

}
