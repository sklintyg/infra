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
package se.inera.intyg.infra.cache.metrics.model;

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
