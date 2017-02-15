/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
