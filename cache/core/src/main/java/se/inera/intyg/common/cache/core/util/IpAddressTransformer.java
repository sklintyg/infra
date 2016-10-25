package se.inera.intyg.common.cache.core.util;

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
