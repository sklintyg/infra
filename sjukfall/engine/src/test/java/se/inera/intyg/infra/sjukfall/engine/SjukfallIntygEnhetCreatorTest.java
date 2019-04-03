/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg;
import se.inera.intyg.infra.sjukfall.testdata.SjukfallIntygGenerator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


/**
 * Created by Magnus Ekstrand on 2016-02-16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SjukfallIntygEnhetCreatorTest {

    private static final String LOCATION_INTYGSDATA = "classpath:Sjukfall/Enhet/intygsdata-creator.csv";

    private static List<IntygData> intygDataList;

    private SjukfallIntygEnhetCreator creator;

    private LocalDate activeDate = LocalDate.parse("2016-02-16");

    @BeforeClass
    public static void initTestData() throws IOException {
        SjukfallIntygGenerator generator = new SjukfallIntygGenerator(LOCATION_INTYGSDATA);
        intygDataList = generator.generate().get();

        assertTrue("Expected 16 but was " + intygDataList.size(), intygDataList.size() == 16);
    }

    @Before
    public void setup() {
        creator = new SjukfallIntygEnhetCreator();
    }

    @Test
    public void testCreatingMap() {
        Map<String, List<SjukfallIntyg>> map = creator.createMap(intygDataList, activeDate);
        assertTrue("Expected 7 but was " + map.size(), map.size() == 7);
    }

    @Test
    public void testReducedMap() {
        Map<String, List<SjukfallIntyg>> map = creator.createMap(intygDataList, activeDate);
        Map<String, List<SjukfallIntyg>> reducedMap = creator.reduceMap(map);

        // Map should be reduced with one entry
        assertTrue("Expected 6 but was " + reducedMap.size(), reducedMap.size() == 6);
    }

    @Test
    public void testSortedMap() {
        Map<String, List<SjukfallIntyg>> map = creator.createMap(intygDataList, activeDate);
        Map<String, List<SjukfallIntyg>> sortedMap = creator.sortValues(map);

        for (Map.Entry<String, List<SjukfallIntyg>> entry : sortedMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                SjukfallIntyg[] arr = entry.getValue().toArray(new SjukfallIntyg[entry.getValue().size()]);
                // Check sort order when list size is greater than one
                for (int i = 0; i < arr.length - 1; i++) {
                    assertTrue(arr[i].getSlutDatum().isBefore(arr[i + 1].getSlutDatum()));
                }
            }
        }
    }

    @Test
    public void testSetActiveCertificate() {
        Map<String, List<SjukfallIntyg>> map = creator.createMap(intygDataList, activeDate);
        Map<String, List<SjukfallIntyg>> activeMap = creator.setActive(map);

        // It can only be zero or one active object
        assertTrue(activeMap.entrySet().stream()
                .allMatch(e -> e.getValue().stream()
                        .filter(o -> o.isAktivtIntyg()).count() < 2));

    }

    /*
     * Test methods below refers to specified cases in SjukfallEnhet.xlsx.
     * https://inera-certificate.atlassian.net/wiki/pages/viewpage.action?pageId=39747618&preview=/39747618/39747617/Sjukfall.xlsx
     */

    @Test
    public void testFall1() {
        String key = "19791110-9291";
        Map<String, List<SjukfallIntyg>> map = creator.create(intygDataList, activeDate);

        List<SjukfallIntyg> list = map.get(key);

        assertTrue("Expected 2 but was " + list.size(), list.size() == 2);
        assertStartDate(list.get(0), "2016-02-01");
        assertEndDate(list.get(1), "2016-02-20");
        assertTrue(list.get(1).isAktivtIntyg());
    }

    @Test
    public void testFall2() {
        String key = "19791123-9262";
        Map<String, List<SjukfallIntyg>> map = creator.create(intygDataList, activeDate);

        List<SjukfallIntyg> list = map.get(key);

        assertTrue("Expected 2 but was " + list.size(), list.size() == 2);
        assertStartDate(list.get(0), "2016-02-01");
        assertEndDate(list.get(1), "2016-02-20");
        assertTrue(list.get(1).isAktivtIntyg());
    }

    @Test
    public void testFall3() {
        String key = "19791212-9280";
        Map<String, List<SjukfallIntyg>> map = creator.create(intygDataList, activeDate);

        List<SjukfallIntyg> list = map.get(key);

        assertTrue("Expected 3 but was " + list.size(), list.size() == 3);
        assertStartDate(list.get(0), "2016-02-01");
        assertEndDate(list.get(2), "2016-02-25");
        assertTrue(list.get(1).isAktivtIntyg());
    }

    @Test
    public void testFall4() {
        String key = "19800113-9297";
        Map<String, List<SjukfallIntyg>> map = creator.create(intygDataList, activeDate);

        List<SjukfallIntyg> list = map.get(key);

        assertTrue("Expected 3 but was " + list.size(), list.size() == 3);
        assertStartDate(list.get(0), "2016-02-01");
        assertEndDate(list.get(2), "2016-02-25");
        assertTrue(list.get(1).isAktivtIntyg());
    }

    @Test
    public void testFall5() {
        String key = "19800124-9286";
        Map<String, List<SjukfallIntyg>> map = creator.create(intygDataList, activeDate);

        List<SjukfallIntyg> list = map.get(key);

        assertTrue("Expected 2 but was " + list.size(), list.size() == 2);
        assertStartDate(list.get(0), "2016-02-12");
        assertEndDate(list.get(1), "2016-02-25");
        assertTrue(list.get(0).isAktivtIntyg());
        assertFalse(list.get(1).isAktivtIntyg());
    }

    @Test
    public void testFall6() {
        String key = "19800207-9294";
        Map<String, List<SjukfallIntyg>> map = creator.create(intygDataList, activeDate);

        List<SjukfallIntyg> list = map.get(key);

        assertTrue("Expected 2 but was " + list.size(), list.size() == 2);
        assertStartDate(list.get(0), "2016-02-12");
        assertEndDate(list.get(1), "2016-02-25");
        assertFalse(list.get(0).isAktivtIntyg());
        assertTrue(list.get(1).isAktivtIntyg());
    }

    @Test
    public void testFall7() {
        String key = "19800228-9224";
        Map<String, List<SjukfallIntyg>> map = creator.create(intygDataList, activeDate);

        assertNull(map.get(key));
    }


    private static void assertStartDate(SjukfallIntyg intygsData, String datum) {
        assertTrue(intygsData.getStartDatum().equals(LocalDate.parse(datum)));
    }

    private static void assertEndDate(SjukfallIntyg intygsData, String datum) {
        assertTrue(intygsData.getSlutDatum().equals(LocalDate.parse(datum)));
    }
}
