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

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.embedded.RedisServer;

import javax.annotation.PreDestroy;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter.REDIS_DEFAULT_PORT;

/**
 * Embedded cache meant for use in 'dev' profile and during testing.
 */
@Configuration
@EnableCaching
public class EmbeddedCacheConfiguration {

    private static final String REDIS_HOST = "localhost";
    private static final int NUMBER_OF_PORTS_TO_TRY = 10;
    private int redisPort;
    private @Value("${redis.cache.default_entry_expiry_time_in_seconds}")
    int defaultEntryExpiry;

    private RedisServer redisServer;

    @PreDestroy
    public void shutdownRedis() {
        redisServer.stop();
    }

    @Bean
    public RedisServer redisServer() {
        redisServer = null;
        //Checkstyle:OFF MagicNumber
        List<Integer> portsToTry = IntStream.range(0, NUMBER_OF_PORTS_TO_TRY)
                .map(portOffset -> REDIS_DEFAULT_PORT + portOffset)
                .boxed().collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        //Checkstyle:ON MagicNumber

        boolean redisStartedSuccessfully = false;
        Iterator<Integer> it = portsToTry.iterator();
        Exception lastFailure = null;
        while (!redisStartedSuccessfully && it.hasNext()) {
            int port = it.next();
            try {
                this.redisServer = RedisServer.builder()
                        .port(port)
                        .setting("maxmemory 512M")
                        .build();
                this.redisServer.start();
                System.out.println("Started Redis server at " + port);
                redisStartedSuccessfully = true;
            } catch (Exception e) {
                lastFailure = e;
                System.out.println("Failed to start embedded redis at port " + port);
            }
        }

        if (!redisStartedSuccessfully) {
            throw new RuntimeException(lastFailure);
        } else {
            this.redisPort = redisServer.ports().get(0);
            return redisServer;
        }
    }

    @Bean
    @DependsOn("redisServer")
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(REDIS_HOST);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }

    @Bean
    RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate());
        redisCacheManager.setUsePrefix(true);
        redisCacheManager.setDefaultExpiration(defaultEntryExpiry);

        return redisCacheManager;
    }

    @Bean
    @DependsOn("cacheManager")
    RedisCacheOptionsSetter redisCacheOptionsSetter() {
        return new RedisCacheOptionsSetter(defaultEntryExpiry);
    }
}
