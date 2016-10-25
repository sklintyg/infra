package se.inera.intyg.common.integration.hsa.cache;

import org.apache.ignite.cache.spring.SpringCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.inera.intyg.common.cache.core.ConfigurableCache;

import javax.annotation.PostConstruct;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
// import org.springframework.context.annotation.Profile;

/**
 * While the cacheManager.getCache(...) isn't strictly necessary for creating the cache used by
 * {@link se.inera.intyg.common.integration.hsa.client.OrganizationUnitServiceBean}, this class
 * provides us with the capability of configuring individual caches based on the current state of the
 * {@link org.apache.ignite.cache.spring.SpringCacheManager#dynamicCacheCfg}
 *
 * Created by eriklupander on 2016-10-20.
 */
public class HsaCacheConfiguration implements ConfigurableCache {

    private static final Logger LOG = LoggerFactory.getLogger(HsaCacheConfiguration.class);

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

    private void initCache(String cacheName, Duration duration) {
        cacheManager.getDynamicCacheConfiguration().setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
        cacheManager.getCache(cacheName);
    }
}
