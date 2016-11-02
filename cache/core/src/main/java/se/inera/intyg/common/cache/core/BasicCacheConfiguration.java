/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.cache.core;

import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.spring.SpringCacheManager;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.inera.intyg.common.cache.core.util.IpAddressTransformer;

import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Generic bootstrapping of Apache Ignite cache in replicated mode.
 *
 * To enable caching for a project/module, just add common-cache as a dependency and make sure you have .properties
 * file loaded that specified the property:
 *
 * cache.ipaddresses=....
 *
 * Typical value for dev use is 127.0.0.1:47500..47509 which means Ignite will try to form a cluster with any caches
 * present on localhost in the specified port range.
 *
 * Created by eriklupander on 2016-10-11.
 */
@Configuration
@EnableCaching
public class BasicCacheConfiguration {

    private static final Duration DEFAULT_EXPIRY_DURATION = Duration.ONE_MINUTE;

    @Value("${cache.ipaddresses}")
    private String igniteIpAddresses;

    @Value("${cache.default.expiration.seconds}")
    private String cacheExpirySeconds = null;

    @Bean
    public SpringCacheManager cacheManager() {
        SpringCacheManager cacheManager = new SpringCacheManager();
        cacheManager.setConfiguration(igniteConfiguration());
        cacheManager.setDynamicCacheConfiguration(dynamicCacheConfiguration());
        return cacheManager;
    }

    @Bean
    public CacheConfiguration<Object, Object> dynamicCacheConfiguration() {
        Duration duration = resolveDuration();

        CacheConfiguration<Object, Object> cacheConfiguration = new CacheConfiguration<>();
        cacheConfiguration.setCacheMode(CacheMode.REPLICATED);
        cacheConfiguration.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(duration));
        cacheConfiguration.setStatisticsEnabled(true);
        return cacheConfiguration;
    }

    @Bean
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        igniteConfiguration.setDiscoverySpi(discoverySpi());
        return igniteConfiguration;
    }

    private DiscoverySpi discoverySpi() {
        TcpDiscoverySpi spi = new TcpDiscoverySpi();
        spi.setIpFinder(tcpDiscoveryMulticastIpFinder());
        return spi;
    }

    private TcpDiscoveryIpFinder tcpDiscoveryMulticastIpFinder() {
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(buildIpAddressList(igniteIpAddresses));
        return ipFinder;
    }

    /**
     * Note that ignite parses the IP-adresses internally if one or more of the specified
     * addresses are syntactically invalid Ignite will throw an exception.
     */
    private List<String> buildIpAddressList(String igniteIpAddresses) {
        return new IpAddressTransformer().parseIpAddressString(igniteIpAddresses);
    }

    private Duration resolveDuration() {
        Duration duration;
        if (cacheExpirySeconds != null && cacheExpirySeconds.trim().length() > 0) {
            duration = new Duration(TimeUnit.SECONDS, Integer.parseInt(cacheExpirySeconds));
        } else {
            duration = DEFAULT_EXPIRY_DURATION;
        }
        return duration;
    }

    // Setters for unit test context setup.
    public void setIgniteIpAddresses(String igniteIpAddresses) {
        this.igniteIpAddresses = igniteIpAddresses;
    }

    public void setCacheExpirySeconds(String cacheExpirySeconds) {
        this.cacheExpirySeconds = cacheExpirySeconds;
    }
}
