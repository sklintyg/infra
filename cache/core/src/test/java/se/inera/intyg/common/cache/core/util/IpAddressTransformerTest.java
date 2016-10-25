package se.inera.intyg.common.cache.core.util;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by eriklupander on 2016-10-21.
 */
public class IpAddressTransformerTest {

    private static final String IP1 = "192.168.0.1:47500..47509";
    private static final String IP2 = "192.168.0.2:47500..47509";
    private static final String IP3 = "192.168.0.3:47500..47509";

    private IpAddressTransformer testee = new IpAddressTransformer();

    @Test
    public void testSingle() {
        List<String> res = testee.parseIpAddressString(IP1);
        assertEquals(IP1, res.get(0));
    }

    @Test
    public void testTwo() {
        List<String> res = testee.parseIpAddressString(IP1 + "," + IP2);
        assertEquals(IP1, res.get(0));
        assertEquals(IP2, res.get(1));
    }

    @Test
    public void testThree() {
        List<String> res = testee.parseIpAddressString(IP1 + "," + IP2 + "," + IP3);
        assertEquals(IP1, res.get(0));
        assertEquals(IP2, res.get(1));
        assertEquals(IP3, res.get(2));
    }


}
