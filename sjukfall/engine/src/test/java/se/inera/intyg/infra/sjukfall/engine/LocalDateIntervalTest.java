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

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import org.junit.Test;


/**
 * Created by marced on 19/02/16.
 */
public class LocalDateIntervalTest {

    @Test
    public void testGetDurationInDays() throws Exception {
        assertEquals(1, new LocalDateInterval(LocalDate.parse("2016-01-01"), LocalDate.parse("2016-01-01")).getDurationInDays());
        assertEquals(31, new LocalDateInterval(LocalDate.parse("2016-01-01"), LocalDate.parse("2016-01-31")).getDurationInDays());
        assertEquals(3, new LocalDateInterval(LocalDate.parse("2016-02-28"), LocalDate.parse("2016-03-01")).getDurationInDays());
        assertEquals(5, new LocalDateInterval(LocalDate.parse("2016-01-28"), LocalDate.parse("2016-02-01")).getDurationInDays());
    }
}
