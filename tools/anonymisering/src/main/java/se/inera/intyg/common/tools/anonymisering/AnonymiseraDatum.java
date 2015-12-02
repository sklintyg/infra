package se.inera.intyg.common.tools.anonymisering;

import java.util.Random;

import org.joda.time.LocalDate;

public class AnonymiseraDatum {

    public static final int DATE_RANGE = 28;

    // Package scope for testability
    Random random = new Random();

    // CHECKSTYLE:OFF MagicNumber
    public String anonymiseraDatum(String datum) {
        if (datum != null) {
            LocalDate date = LocalDate.parse(datum);
            // random days from -14 to +14, but not 0
            int days = random.nextInt(DATE_RANGE) - DATE_RANGE/2;
            if (days == 0) days = DATE_RANGE/2;
            date = date.plusDays(days);
            return date.toString();
        } else {
            return null;
        }
    }
    // CHECKSTYLE:ON MagicNumber

}
