/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.UnitType;
import se.riv.infrastructure.directory.v1.PersonInformationType;

/**
 * While the cacheManager.getCache(...) isn't strictly necessary for creating the cache used by {@link
 * se.inera.intyg.infra.integration.hsa.client.OrganizationUnitService}, this class provides us with the capability of configuring
 * individual caches based on the current state of the (dynamic) configuration
 * <p>
 * Created by eriklupander on 2016-10-20.
 */
@Configuration
public class HsaCacheConfiguration {

    static final String HSA_UNIT_CACHE_PREFIX = "hsaUnitCache:";
    static final String HSA_HEALTH_CARE_UNIT_CACHE_PREFIX = "hsaHealthCareUnitCache:";
    static final String HSA_HEALTH_CARE_UNIT_MEMBERS_CACHE_PREFIX = "hsaHealthCareUnitMembersCache:";
    static final String HSA_EMPLOYEE_CACHE_PREFIX = "hsaEmployeeCache:";

    private static final String HSA_UNIT_CACHE_EXPIRY = "hsa.unit.cache.expiry";
    private static final String HSA_HEALTHCAREUNIT_CACHE_EXPIRY = "hsa.healthcareunit.cache.expiry";
    private static final String HSA_HEALHCAREUNITMEMBERS_CACHE_EXPIRY = "hsa.healhcareunitmembers.cache.expiry";
    private static final String HSA_EMPLOYEE_CACHE_EXPIRY = "hsa.employee.cache.expiry";

    @Value("${" + HSA_UNIT_CACHE_EXPIRY + "}")
    private String hsaUnitCacheExpirySeconds;

    @Value("${" + HSA_HEALTHCAREUNIT_CACHE_EXPIRY + "}")
    private String hsaHealthCareUnitCacheExpirySeconds;

    @Value("${" + HSA_HEALHCAREUNITMEMBERS_CACHE_EXPIRY + "}")
    private String hsaHeathCareUnitMembersCacheExpirySeconds;

    @Value("${" + HSA_EMPLOYEE_CACHE_EXPIRY + ":60}")
    private String hsaEmployeeCacheExpirySeconds;

    @Value("${app.name:noname}")
    private String appName;


    @Autowired
    private RedisCacheOptionsSetter redisCacheOptionsSetter;

    @Bean
    Cache hsaUnitCache() {
        return newHsaCache(HSA_UNIT_CACHE_PREFIX, hsaUnitCacheExpirySeconds);
    }

    @Bean
    Cache hsaCareUnitCache() {
        return newHsaCache(HSA_HEALTH_CARE_UNIT_CACHE_PREFIX, hsaHealthCareUnitCacheExpirySeconds);
    }

    @Bean
    Cache hsaCareUnitMemberCache() {
        return newHsaCache(HSA_HEALTH_CARE_UNIT_MEMBERS_CACHE_PREFIX, hsaHeathCareUnitMembersCacheExpirySeconds);
    }

    @Bean
    Cache hsaEmployeeCache() {
        return newHsaCache(HSA_EMPLOYEE_CACHE_PREFIX, hsaEmployeeCacheExpirySeconds);
    }

    private Cache newHsaCache(String prefix, String expiry) {
        return redisCacheOptionsSetter.createCache(prefix + appName, expiry);
    }

    @Bean
    CacheResolver hsaCacheResolver() {
        return new CacheResolver() {
            @Override
            public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
                if (context.getMethod().getReturnType() == UnitType.class) {
                    return Collections.singleton(hsaUnitCache());
                }
                if (context.getMethod().getReturnType() == HealthCareUnitType.class) {
                    return Collections.singleton(hsaCareUnitCache());
                }
                if (context.getMethod().getReturnType() == HealthCareUnitMembersType.class) {
                    return Collections.singleton(hsaCareUnitMemberCache());
                }

                if (context.getMethod().getReturnType() == List.class) {
                    ParameterizedType parameterizedReturnType = (ParameterizedType) context.getMethod().getGenericReturnType();
                    Type[] actualTypeArguments = parameterizedReturnType.getActualTypeArguments();
                    if (actualTypeArguments.length == 1) {
                        if (actualTypeArguments[0] == PersonInformationType.class) {
                            return Collections.singleton(hsaEmployeeCache());
                        }
                    }
                }

                return Collections.emptyList();
            }
        };
    }
}
