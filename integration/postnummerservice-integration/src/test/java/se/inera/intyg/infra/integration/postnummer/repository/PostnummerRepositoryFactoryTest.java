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
package se.inera.intyg.infra.integration.postnummer.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.integration.postnummer.model.Omrade;

@RunWith(MockitoJUnitRunner.class)
public class PostnummerRepositoryFactoryTest {

    private PostnummerRepositoryFactory factory = new PostnummerRepositoryFactory();

    private static final String LINE_1 = "13100;NACKA;01;STOCKHOLM;0182;NACKA;01";
    private static final String LINE_1_POSTNUMMER = "13100";
    private static final String LINE_1_POSTORT = "NACKA";
    private static final String LINE_1_LAN = "STOCKHOLM";
    private static final String LINE_1_KOMMUN = "NACKA";

    @Test
    public void testCreateOmradeFromString() {

        Omrade res = factory.createOmradeFromString(LINE_1);

        assertNotNull(res);
        assertEquals(LINE_1_POSTNUMMER, res.getPostnummer());
        assertEquals(LINE_1_POSTORT, res.getPostort());
        assertEquals(LINE_1_KOMMUN, res.getKommun());
        assertEquals(LINE_1_LAN, res.getLan());
    }

    @Test
    public void testCreateOmradeWithSetters() {
        Omrade control = factory.createOmradeFromString(LINE_1);
        Omrade test = new Omrade(null, null, null, null);
        test.setKommun(LINE_1_KOMMUN);
        test.setLan(LINE_1_LAN);
        test.setPostnummer(LINE_1_POSTNUMMER);
        test.setPostort(LINE_1_POSTORT);
        assertTrue(control.hashCode() == test.hashCode());
    }
}
