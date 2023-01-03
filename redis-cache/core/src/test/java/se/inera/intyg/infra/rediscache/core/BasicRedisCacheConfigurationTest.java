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
package se.inera.intyg.infra.rediscache.core;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
public class BasicRedisCacheConfigurationTest {

    @Autowired
    private CacheManager cacheManager;

    private Cache testCache;

    @BeforeEach
    public void init() {
        testCache = cacheManager.getCache("testCache");
    }

    @AfterEach
    public void teardown() {
        if (testCache != null) {
            testCache.clear();
        }
    }

    @Test
    public void testCache() {
        IntStream.range(0, 100).forEach(i -> testCache.put("key" + i, "value" + i));
        IntStream.range(0, 100).forEach(i -> assertEquals("value" + i, Objects.requireNonNull(testCache.get("key" + i)).get()));

        Object o = Objects.requireNonNull(testCache.get("key1")).get();
        assertEquals("value1", o);
    }

}
