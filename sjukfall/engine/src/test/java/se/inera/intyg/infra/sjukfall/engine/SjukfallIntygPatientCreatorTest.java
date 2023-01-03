/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.sjukfall.engine;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg;
import se.inera.intyg.infra.sjukfall.testdata.SjukfallIntygGenerator;

/**
 * @author Magnus Ekstrand on 2017-08-31.
 */
@RunWith(MockitoJUnitRunner.class)

public class SjukfallIntygPatientCreatorTest {

    private static final String LOCATION_INTYGSDATA = "classpath:Sjukfall/Patient/intygsdata-patient.csv";

    private static List<IntygData> intygDataList;

    private SjukfallIntygPatientCreator testee;

    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    @BeforeClass
    public static void initTestData() throws IOException {
        SjukfallIntygGenerator generator = new SjukfallIntygGenerator(LOCATION_INTYGSDATA);
        intygDataList = generator.generate().get();

        assertTrue("Expected 5 but was " + intygDataList.size(), intygDataList.size() == 5);
    }

    @Before
    public void setup() {
        testee = new SjukfallIntygPatientCreator();
    }

    @Test
    public void testCreatingMapWithMaxGlappZeroDays() {
        Map<Integer, List<SjukfallIntyg>> map = testee.createMap(intygDataList, 0, activeDate);
        assertTrue("Expected 4 but was " + map.size(), map.size() == 4);
    }

    @Test
    public void testCreatingMapWithMaxGlappOneDays() {
        Map<Integer, List<SjukfallIntyg>> map = testee.createMap(intygDataList, 1, activeDate);
        assertTrue("Expected 3 but was " + map.size(), map.size() == 3);
    }

    @Test
    public void testCreatingMapWithMaxGlappTwoDays() {
        Map<Integer, List<SjukfallIntyg>> map = testee.createMap(intygDataList, 2, activeDate);
        assertTrue("Expected 3 but was " + map.size(), map.size() == 2);
    }

    @Test
    public void testCreatingMapWithMaxGlappThreeDays() {
        Map<Integer, List<SjukfallIntyg>> map = testee.createMap(intygDataList, 3, activeDate);
        assertTrue("Expected 2 but was " + map.size(), map.size() == 2);
    }

    @Test
    public void testCreatingMapWithMaxGlappNineDays() {
        Map<Integer, List<SjukfallIntyg>> map = testee.createMap(intygDataList, 9, activeDate);
        assertTrue("Expected 2 but was " + map.size(), map.size() == 2);
    }

    @Test
    public void testCreatingMapWithMaxGlappTenDays() {
        Map<Integer, List<SjukfallIntyg>> map = testee.createMap(intygDataList, 10, activeDate);
        assertTrue("Expected 2 but was " + map.size(), map.size() == 1);
    }

    @Test
    public void testCreatingMapWithMaxGlappElevenDays() {
        Map<Integer, List<SjukfallIntyg>> map = testee.createMap(intygDataList, 11, activeDate);
        assertTrue("Expected 4 but was " + map.size(), map.size() == 1);
    }
}
