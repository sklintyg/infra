package se.inera.certificate.tools.anonymisering

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
