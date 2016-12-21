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

package se.inera.intyg.infra.cache.core;

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
        } catch (NumberFormatException nfe) {
            String errMsg = "Invalid value for property '" + cacheName + "' with value '" + expirySeconds + "'";
            throw new IllegalArgumentException(errMsg);
        }
    }
}
