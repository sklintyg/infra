/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.sjukfall.engine;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.testdata.AktivtIntygGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


/**
 * Created by Magnus Ekstrand on 2016-02-16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AktivtIntygTest {

    private static final String LOCATION_INTYGSDATA = "classpath:AktivtIntygTest/intygsdata.csv";

    private static List<IntygData> intygDataList;

    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    @BeforeClass
    public static void initTestData() throws IOException {
        AktivtIntygGenerator generator = new AktivtIntygGenerator(LOCATION_INTYGSDATA);
        intygDataList = generator.generate().get();

        assertTrue("Expected 6 but was " + intygDataList.size(), intygDataList.size() == 6);
    }

    @Before
    public void setup() {
    }

    @Test
    public void testIntyg1() {
        IntygData intygData = getIntygsData("intyg-1");
        AktivtIntyg testee = new AktivtIntyg.AktivtIntygBuilder(intygData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-10", false);
    }

    @Test
    public void testIntyg2() {
        IntygData intygData = getIntygsData("intyg-2");
        AktivtIntyg testee = new AktivtIntyg.AktivtIntygBuilder(intygData, activeDate).build();

        assertIntygsData(testee, "2016-02-12", "2016-02-20", true);
    }

    @Test
    public void testIntyg3() {
        IntygData intygData = getIntygsData("intyg-3");
        AktivtIntyg testee = new AktivtIntyg.AktivtIntygBuilder(intygData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-20", true);
    }

    @Test
    public void testIntyg4() {
        IntygData intygData = getIntygsData("intyg-4");
        AktivtIntyg testee = new AktivtIntyg.AktivtIntygBuilder(intygData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-25", false);
    }

    @Test
    public void testIntyg5() {
        IntygData intygData = getIntygsData("intyg-5");
        AktivtIntyg testee = new AktivtIntyg.AktivtIntygBuilder(intygData, activeDate).build();

        assertIntygsData(testee, "2016-02-01", "2016-02-28", true);
    }

    @Test
    public void testIntyg6() {
        IntygData intygData = getIntygsData("intyg-6");
        AktivtIntyg testee1 = new AktivtIntyg.AktivtIntygBuilder(intygData, activeDate).build();
        AktivtIntyg testee2 = new AktivtIntyg.AktivtIntygBuilder(intygData, LocalDate.parse("2016-02-22")).build();
        AktivtIntyg testee3 = new AktivtIntyg.AktivtIntygBuilder(intygData, LocalDate.parse("2016-02-23")).build();

        assertIntygsData(testee1, "2016-02-11", "2016-02-28", false);
        assertIntygsData(testee2, "2016-02-11", "2016-02-28", true);
        assertIntygsData(testee3, "2016-02-11", "2016-02-28", true);
    }

    private IntygData getIntygsData(String intygsId) {
        return intygDataList.stream()
                .filter(e -> e.getIntygId().equalsIgnoreCase(intygsId))
                .findAny()
                .get();
    }

    private static void assertIntygsData(AktivtIntyg obj, String startDatum, String slutDatum, boolean aktivtIntyg) {
        assertTrue(obj.getStartDatum().equals(LocalDate.parse(startDatum)));
        assertTrue(obj.getSlutDatum().equals(LocalDate.parse(slutDatum)));
        assertTrue(obj.isAktivtIntyg() == aktivtIntyg);
    }

}
