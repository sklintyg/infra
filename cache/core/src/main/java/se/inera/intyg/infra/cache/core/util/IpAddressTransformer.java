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
package se.inera.intyg.infra.cache.core.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by eriklupander on 2016-10-21.
 */
public class IpAddressTransformer {

    private static final String DEFAULT_IGNITE_IP_LOCALHOST = "127.0.0.1:47500..47509";
    private static final String SEPARATOR_CHAR = ",";

    public List<String> parseIpAddressString(String igniteIpAddresses) {

        if (igniteIpAddresses == null || igniteIpAddresses.isEmpty()) {
            return Collections.singletonList(DEFAULT_IGNITE_IP_LOCALHOST);
        }

        if (igniteIpAddresses.contains(SEPARATOR_CHAR)) {
            return Arrays.asList(igniteIpAddresses.split(SEPARATOR_CHAR));
        } else {
            return Collections.singletonList(igniteIpAddresses);
        }
    }
}
