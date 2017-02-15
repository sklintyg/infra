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
package se.inera.intyg.infra.cache.core;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by eriklupander on 2016-10-18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:basic-cache-test-context.xml")
public class BasicCacheConfigurationTest {

    @Autowired
    private SpringCacheManager cacheManager;

    private Cache testCache;

    @Before
    public void init() {
        testCache = cacheManager.getCache("testCache");
    }

    @Test
    public void testCache() throws InterruptedException {

        IntStream.range(0, 100).forEach(i ->  testCache.put("key" + i, "value" + i));
        IntStream.range(0, 100).forEach(i -> assertEquals("value" + i, testCache.get("key" + i).get()));

        testCache.get("other-key");

        assertEquals(100L, ((IgniteCache)testCache.getNativeCache()).localMetrics().getCachePuts());
        assertEquals(101L, ((IgniteCache)testCache.getNativeCache()).localMetrics().getCacheGets());
        assertEquals(1L, ((IgniteCache)testCache.getNativeCache()).localMetrics().getCacheMisses());
    }

    @After
    public void tearDown() {
        if (testCache != null) {
            testCache.clear();
        }
    }
}
