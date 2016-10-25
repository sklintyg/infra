package se.inera.intyg.common.cache.metrics;

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
import se.inera.intyg.common.cache.stats.model.CacheStatistics;

import java.util.Collection;
import java.util.stream.Collectors;

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
