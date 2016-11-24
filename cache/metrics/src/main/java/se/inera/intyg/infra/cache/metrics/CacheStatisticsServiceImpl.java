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

package se.inera.intyg.infra.cache.metrics;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCluster;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMetrics;
import org.apache.ignite.cluster.ClusterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.cache.metrics.model.CacheStatistics;

/**
 * Created by eriklupander on 2016-10-19.
 */
@Service
public class CacheStatisticsServiceImpl implements CacheStatisticsService {

    @Autowired
    private CacheManager cacheManager;

    @Override
    public CacheStatistics getCacheStatistics() {

        CacheStatistics stats = new CacheStatistics();

        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache.getNativeCache() instanceof IgniteCache) {
                IgniteCache igniteCache = (IgniteCache) cache.getNativeCache();
                CacheMetrics metrics = igniteCache.metrics();

                stats.getCacheMetrics().put(cacheName, metrics);
            }
        }

        for (Ignite i : Ignition.allGrids()) {
            IgniteCluster cluster = i.cluster();
            Collection<ClusterNode> topology = cluster.topology(cluster.topologyVersion());
            for (ClusterNode node : topology) {
                String hostName = node.hostNames().stream().collect(Collectors.joining(", "));
                stats.getClusterMetrics().put(hostName, node.metrics());
            }
        }
        return stats;
    }
}
