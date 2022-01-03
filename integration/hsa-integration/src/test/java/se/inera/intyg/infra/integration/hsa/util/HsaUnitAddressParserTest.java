/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.riv.infrastructure.directory.v1.AddressType;

/**
 * Created by eriklupander on 2017-09-19.
 */
public class HsaUnitAddressParserTest {

    private static final String MOTTAGNING_ID = "mottagning-1";
    private static final String MOTTAGNING_NAME = "Mottagning 1";
    private HsaUnitAddressParser testee = new HsaUnitAddressParser();

    // Data from HSA test IFV1239877878-1046
    @Test
    public void testParseTwoLineMottagning() {
        Mottagning mottagning = new Mottagning(MOTTAGNING_ID, MOTTAGNING_NAME);
        AddressType address = new AddressType();
        address.getAddressLine().add("Lokalgatan 12");
        address.getAddressLine().add("10000 Lillsala");
        testee.updateWithAddress(mottagning, address, null);

        assertEquals("Lokalgatan 12", mottagning.getPostadress());
        assertEquals("10000", mottagning.getPostnummer());
        assertEquals("Lillsala", mottagning.getPostort());
    }

    // Data from HSA test IFV1239877878-104C
    @Test
    public void testParseTwoLineMottagning2() {
        Mottagning mottagning = new Mottagning(MOTTAGNING_ID, MOTTAGNING_NAME);
        AddressType address = new AddressType();
        address.getAddressLine().add("Lokalgatan 12, 2 trappor");
        address.getAddressLine().add("100 00 Lillsala");
        testee.updateWithAddress(mottagning, address, null);

        assertEquals("Lokalgatan 12, 2 trappor", mottagning.getPostadress());
        assertEquals("100 00", mottagning.getPostnummer());
        assertEquals("Lillsala", mottagning.getPostort());
    }

    @Test
    public void testParseTwoLineMottagningWithSeparatePostalCode() {
        Mottagning mottagning = new Mottagning(MOTTAGNING_ID, MOTTAGNING_NAME);
        AddressType address = new AddressType();
        address.getAddressLine().add("Lokalgatan 12");
        address.getAddressLine().add("10000 Lillsala");
        testee.updateWithAddress(mottagning, address, "12345");

        assertEquals("Lokalgatan 12", mottagning.getPostadress());
        assertEquals("12345", mottagning.getPostnummer());
        assertEquals("Lillsala", mottagning.getPostort());
    }

    @Test
    public void testParseTwoLineMottagningWithNoLines() {
        Mottagning mottagning = new Mottagning(MOTTAGNING_ID, MOTTAGNING_NAME);
        AddressType address = new AddressType();

        testee.updateWithAddress(mottagning, address, "12345");

        assertEquals("", mottagning.getPostadress());
        assertEquals("12345", mottagning.getPostnummer());
        assertEquals("", mottagning.getPostort());
    }

    @Test
    public void testParseTwoLineMottagningWithNoLinesAndNoPostnummer() {
        Mottagning mottagning = new Mottagning(MOTTAGNING_ID, MOTTAGNING_NAME);
        AddressType address = new AddressType();

        testee.updateWithAddress(mottagning, address, null);

        assertEquals("", mottagning.getPostadress());
        assertEquals("", mottagning.getPostnummer());
        assertEquals("", mottagning.getPostort());
    }

    @Test
    public void testParseTwoLineMottagningWithNullLines() {
        Mottagning mottagning = new Mottagning(MOTTAGNING_ID, MOTTAGNING_NAME);
        testee.updateWithAddress(mottagning, null, null);

        assertNull(mottagning.getPostadress());
        assertNull(mottagning.getPostnummer());
        assertNull(mottagning.getPostort());
    }


}
