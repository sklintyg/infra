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
package se.inera.intyg.infra.rediscache.core;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.cache.RedisCacheManager;

public class RedisCacheOptionsSetter {
    public static final int REDIS_DEFAULT_PORT = 6379;

    private long defaultEntryExpiry;

    @Autowired
    private RedisCacheManager redisCacheManager;

    public RedisCacheOptionsSetter(long defaultEntryExpiry) {
        this.defaultEntryExpiry = defaultEntryExpiry;
    }

    public void createCache(String cacheName, String expiryTimeInSeconds) {
        long expiryValue;
        try {
            expiryValue = Long.parseLong(expiryTimeInSeconds);
        } catch (NumberFormatException e) {
            expiryValue = defaultEntryExpiry;
        }
        redisCacheManager.setExpires(ImmutableMap.of(cacheName, expiryValue));
        // First access of cache triggers building it, see implementation of RedisCacheManager for details.
        redisCacheManager.getCache(cacheName);
   }
}
