package se.inera.intyg.common.cache.metrics;

import se.inera.intyg.common.cache.stats.model.CacheStatistics;

/**
 * Created by eriklupander on 2016-10-19.
 */
public interface CacheStatisticsService {

    /**
     * Stats as DTO.
     *
     * @return
     */
    CacheStatistics getCacheStatistics();
}
