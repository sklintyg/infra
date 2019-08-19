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
package se.inera.intyg.infra.rediscache.core;

import static org.junit.Assert.assertEquals;


import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.embedded.RedisServer;

/**
 * Tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class BasicRedisCacheConfigurationTest {
    @Autowired
    private CacheManager cacheManager;

    private Cache testCache;

    @Autowired
    RedisServer redisServer;

    @Before
    public void init() {
        testCache = cacheManager.getCache("testCache");
    }

    @After
    public void teardown() {
        redisServer.stop();
    }

    @Test
    public void testCache() throws InterruptedException {

        IntStream.range(0, 100).forEach(i ->  testCache.put("key" + i, "value" + i));
        IntStream.range(0, 100).forEach(i -> assertEquals("value" + i, testCache.get("key" + i).get()));

        Object o = testCache.get("key1").get();
        assertEquals("value1", o);
    }

    @After
    public void tearDown() {
        if (testCache != null) {
            testCache.clear();
        }
    }
}
