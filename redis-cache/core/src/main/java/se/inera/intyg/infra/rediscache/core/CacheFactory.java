/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.rediscache.core;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

public class CacheFactory extends RedisCacheManager {

    RedisCacheConfiguration defaultCacheConfiguration;

    public CacheFactory(RedisCacheWriter cacheWriter,
        RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
        this.defaultCacheConfiguration = defaultCacheConfiguration;
    }

    RedisCache createCache(String cacheName) {
        return createRedisCache(cacheName, defaultCacheConfiguration);
    }

    RedisCache createCache(String cacheName, RedisCacheConfiguration redisCacheConfiguration) {
        return createRedisCache(cacheName, redisCacheConfiguration);
    }
}
