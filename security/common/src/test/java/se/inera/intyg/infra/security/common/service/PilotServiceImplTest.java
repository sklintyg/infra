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
package se.inera.intyg.infra.security.common.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class PilotServiceImplTest {

    private PilotServiceImpl pilotService;

    @Before
    public void setup() throws Exception {
        pilotService = new PilotServiceImpl();
        ReflectionTestUtils.setField(pilotService, "pilotFile", "pilot.yaml");
        pilotService.init();
    }

    @Test
    public void testService() {
        Map<String, Boolean> resX = pilotService.getFeatures(Arrays.asList("x"));
        assertEquals(3, resX.size());
        assertTrue(resX.get("fk7263.srs"));
        assertTrue(resX.get("lisjp.srs"));
        assertFalse(resX.get("test.srs"));

        Map<String, Boolean> resY = pilotService.getFeatures(Arrays.asList("y"));
        assertEquals(2, resY.size());
        assertTrue(resX.get("fk7263.srs"));
        assertTrue(resX.get("lisjp.srs"));
    }
}
