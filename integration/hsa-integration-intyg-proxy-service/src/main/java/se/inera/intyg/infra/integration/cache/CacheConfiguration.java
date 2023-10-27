/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter;

@Configuration
@RequiredArgsConstructor
public class CacheConfiguration {

    private final RedisCacheOptionsSetter redisCacheOptionsSetter;
    static final String HSA_EMPLOYEE_CACHE_PREFIX = "hsaEmployeeCache:";
    @Value("${hsa.get.employee.cache.expiry}")
    private String hsaEmployeeCacheExpirySeconds;

    @Bean(name = "hsaCacheResolver")
    public Cache hsaCacheResolver() {
        return redisCacheOptionsSetter.createCache(HSA_EMPLOYEE_CACHE_PREFIX, hsaEmployeeCacheExpirySeconds);
    }
}
