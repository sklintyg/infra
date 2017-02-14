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

import static org.junit.Assert.assertEquals;
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
import java.util.Map;

/**
 * Created by Magnus Ekstrand on 10/02/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AktivtIntygResolverReduceRightTest {

    private static final String LOCATION_INTYGSDATA = "classpath:AktivtIntygResolverTest/intygsdata-resolver-right.csv";

    private static List<IntygData> intygDataList;

    private AktivtIntygResolver resolver;


    @BeforeClass
    public static void initTestData() throws IOException {
        AktivtIntygGenerator generator = new AktivtIntygGenerator(LOCATION_INTYGSDATA);
        intygDataList = generator.generate().get();
    }

    @Before
    public void setup() {
        resolver = new AktivtIntygResolver();
    }

    @Test
    public void testFall1() {
        List<AktivtIntyg> result = getTestData("fall-1-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
    }

    @Test
    public void testFall2() {
        List<AktivtIntyg> result = getTestData("fall-2-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 2 but was " + result.size(), result.size() == 2);
        assertEquals("fall-2-intyg-1", result.get(0).getIntygId());
        assertEquals("fall-2-intyg-2", result.get(1).getIntygId());
    }

    @Test
    public void testFall3() {
        List<AktivtIntyg> result = getTestData("fall-3-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 0 but was " + result.size(), result.size() == 0);
    }

    @Test
    public void testFall4() {
        List<AktivtIntyg> result = getTestData("fall-4-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
        assertEquals("fall-4-intyg-1", result.get(0).getIntygId());
        assertEquals("fall-4-intyg-2", result.get(1).getIntygId());
        assertEquals("fall-4-intyg-3", result.get(2).getIntygId());
    }

    @Test
    public void testFall5() {
        List<AktivtIntyg> result = getTestData("fall-5-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 3 but was " + result.size(), result.size() == 3);
        assertEquals("fall-5-intyg-4", result.get(0).getIntygId());
        assertEquals("fall-5-intyg-1", result.get(1).getIntygId());
        assertEquals("fall-5-intyg-2", result.get(2).getIntygId());
    }

    @Test
    public void testFall6() {
        List<AktivtIntyg> result = getTestData("fall-6-right", "2016-02-10", 5, "2016-01-31");
        assertTrue("Expected 2 but was " + result.size(), result.size() == 2);
    }

    private List<AktivtIntyg> getTestData(String key, String aktivtDatum , int maxIntygsGlapp, String initialtDatum) {
        Map<String, List<AktivtIntyg>> data = getTestData(aktivtDatum);
        return resolver.reduceRight(data.get(key), maxIntygsGlapp, LocalDate.parse(initialtDatum));
    }

    private Map<String, List<AktivtIntyg>> getTestData(String aktivtDatum) {
        return resolver.toMap(intygDataList, LocalDate.parse(aktivtDatum));
    }

    private Map<String, List<AktivtIntyg>> getTestData(LocalDate aktivtDatum) {
        return resolver.toMap(intygDataList, aktivtDatum);
    }
}
