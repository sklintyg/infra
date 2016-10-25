package se.inera.intyg.common.cache.core;

import javax.cache.expiry.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Created by eriklupander on 2016-10-25.
 */
public interface ConfigurableCache {

    default Duration buildDuration(String expirySeconds, String cacheName) {
        if (expirySeconds == null || expirySeconds.isEmpty()) {
            return Duration.ONE_HOUR;
        }
        try {
            Integer expiry = Integer.parseInt(expirySeconds);
            return new Duration(TimeUnit.SECONDS, expiry);
        } catch(NumberFormatException nfe) {
            String errMsg = "Invalid value for property '" + cacheName + "' with value '" + expirySeconds + "'";
            throw new IllegalArgumentException(errMsg);
        }
    }
}
