package se.inera.intyg.infra.cache.metrics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.Cache;

import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2016-10-21.
 */
@RunWith(MockitoJUnitRunner.class)
public class CacheStatisticsServiceTest {

    @Mock
    org.springframework.cache.CacheManager cacheManager;

    @InjectMocks
    CacheStatisticsService testee = new CacheStatisticsServiceImpl();

    private Cache testCache = mock(Cache.class);

    @Test
    public void testStatsService() {
        when(cacheManager.getCacheNames()).thenReturn(Arrays.asList("testCache"));

        when(cacheManager.getCache("testCache")).thenReturn(testCache);
        CacheStatistics cacheStatistics = testee.getCacheStatistics();
        assertNotNull(cacheStatistics);
    }
}
