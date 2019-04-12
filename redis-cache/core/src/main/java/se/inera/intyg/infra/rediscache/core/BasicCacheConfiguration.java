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

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import se.inera.intyg.infra.rediscache.core.util.ConnectionStringUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Stream;

/**
 * Initialization and activation of Redis cache for a single Redis host.
 */
@Configuration
@EnableCaching
public class BasicCacheConfiguration {

    @Value("${redis.host}")
    String redisHost;
    @Value("${redis.port}")
    String redisPort;
    @Value("${redis.password}")
    String redisPassword;
    @Value("${redis.cache.default_entry_expiry_time_in_seconds}")
    int defaultEntryExpiry;
    @Value("${redis.sentinel.master.name}")
    String redisSentinelMasterName;

    @Resource
    private Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Due to complexity reasons, we do a programmatic profile check here to pick the appropriate JedisConnectionFactory.
     */
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (Stream.of(activeProfiles).noneMatch("redis-sentinel"::equalsIgnoreCase)) {
            return plainConnectionFactory();
        } else {
            return sentinelConnectionFactory();
        }
    }

    @Bean(name = "rediscache")
    RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
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

    private JedisConnectionFactory plainConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(Integer.parseInt(redisPort));
        if (StringUtils.hasLength(redisPassword)) {
            factory.setPassword(redisPassword);
        }
        //factory.setUsePool(true);
        return factory;
    }

    private JedisConnectionFactory sentinelConnectionFactory() {
        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                .master(redisSentinelMasterName);

        if (Strings.isNullOrEmpty(redisHost) || Strings.isNullOrEmpty(redisPort)) {
            throw new IllegalStateException("Cannot bootstrap RedisSentinelConfiguration, redis.host or redis.port is null or empty");
        }
        List<String> hosts = ConnectionStringUtil.parsePropertyString(redisHost);
        List<String> ports = ConnectionStringUtil.parsePropertyString(redisPort);

        if (hosts.size() == 0 || ports.size() == 0 || hosts.size() != ports.size()) {
            throw new IllegalStateException(
                    "Cannot bootstrap RedisSentinelConfiguration, number of redis.host and/or redis.port was zero or not equal.");
        }

        for (int a = 0; a < hosts.size(); a++) {
            sentinelConfig = sentinelConfig.sentinel(hosts.get(a), Integer.parseInt(ports.get(a)));
        }

        return new JedisConnectionFactory(sentinelConfig);
    }
}
