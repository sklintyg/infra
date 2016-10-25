package se.inera.intyg.common.cache.stats.model;

import org.apache.ignite.cache.CacheMetrics;
import org.apache.ignite.cluster.ClusterMetrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eriklupander on 2016-10-20.
 */
public class CacheStatistics {

    private Map<String, CacheMetrics> cacheMetrics = new HashMap<>();
    private Map<String, ClusterMetrics> clusterMetrics = new HashMap<>();

    public Map<String, CacheMetrics> getCacheMetrics() {
        return cacheMetrics;
    }

    public void setCacheMetrics(Map<String, CacheMetrics> cacheMetrics) {
        this.cacheMetrics = cacheMetrics;
    }

    public Map<String, ClusterMetrics> getClusterMetrics() {
        return clusterMetrics;
    }

    public void setClusterMetrics(Map<String, ClusterMetrics> clusterMetrics) {
        this.clusterMetrics = clusterMetrics;
    }
}
