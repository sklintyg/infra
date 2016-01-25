/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.tools.anonymisering

import org.joda.time.LocalDate
import org.junit.Before
import org.junit.Test

class AnonymiseraDatumTest {

    AnonymiseraDatum anonymiseraDatum

    @Before
    void setUp() {
        anonymiseraDatum = new AnonymiseraDatum()
    }

    @Test
    void anonymiseringÄndrarDag() {
        String datum = "2014-09-20"
        anonymiseraDatum.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)+5}] as Random
        String anonymiseratDatum = anonymiseraDatum.anonymiseraDatum(datum)
        assert anonymiseratDatum == "2014-09-25"
        anonymiseraDatum.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)-5}] as Random
        anonymiseratDatum = anonymiseraDatum.anonymiseraDatum(datum)
        assert anonymiseratDatum == "2014-09-15"
    }

    @Test
    void anonymiseringGerInteSammaDatum() {
        String datum = "2014-09-20"
        100.times {
            String anonymiseratDatum = anonymiseraDatum.anonymiseraDatum(datum)
            assert datum != anonymiseratDatum
        }
    }

    @Test
    void anonymiseringGerNyttDatumInomPlusMinusTvåVeckor() {
        String datum = "2014-09-20"
        LocalDate from = LocalDate.parse("2014-09-06")
        LocalDate tom = LocalDate.parse("2014-10-04")
        100.times {
            LocalDate anonymiseratDatum = LocalDate.parse(anonymiseraDatum.anonymiseraDatum(datum))
            assert from <= anonymiseratDatum && anonymiseratDatum <= tom
        }
    }

}
