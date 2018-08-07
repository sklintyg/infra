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

import static se.inera.intyg.infra.rediscache.core.RedisCacheOptionsSetter.REDIS_DEFAULT_PORT;


import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import redis.embedded.RedisServer;


/**
 * Embedded cache meant for use in 'dev' profile and during testing.
 */
@Configuration
@EnableCaching
public class EmbeddedCacheConfiguration extends BasicCacheConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedCacheConfiguration.class);

    private static final int NUMBER_OF_PORTS_TO_TRY = 10;

    private @Value("${redis.cache.default_entry_expiry_time_in_seconds}") int defaultEntryExpiry;

    private RedisServer redisServer;

    @PreDestroy
    public void shutdownRedis() {
        this.redisServer.stop();
    }

    @Bean
    public RedisServer redisServer() {
        final AtomicInteger port = new AtomicInteger(REDIS_DEFAULT_PORT);

        this.redisServer = Stream.generate(() -> port.getAndIncrement())
                .limit(NUMBER_OF_PORTS_TO_TRY)
                .map(this::create)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to start embedded redis server"));

        return this.redisServer;
    }

    //
    RedisServer create(final int port) {
        final RedisServer redisServer = RedisServer.builder()
                .port(port)
                .setting("maxmemory 512M")
                .build();
        try {
            redisServer.start();
            LOG.info("Embedded redis server listens on port {}", port);
            return redisServer;
        } catch (Exception e) {
            LOG.warn("Unable to start embedded redis server on port {}", port);
        }
        return null;
    }

    @Bean
    @DependsOn("redisServer")
    @Override
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = super.jedisConnectionFactory();
        factory.setPort(redisServer.ports().get(0));
        return factory;
    }
}
