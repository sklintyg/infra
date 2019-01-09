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

import junit.framework.Assert;
import org.junit.Test;
import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by marced on 19/02/16.
 */
public class SjukfallLangdCalculatorTest {

    @Test
    public void testGetEffectiveNumberOfSickDaysNointervals() throws Exception {

        assertEquals(0, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(null));

        List<SjukfallIntyg> intygsUnderlag = new ArrayList<>();
        assertEquals(0, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));
    }

    /**
     * Test FALL1 from confluence document /Krav/Rehabstod/Berakning av sjukfall.
     * @throws Exception
     */
    @Test
    public void testGetEffectiveNumberOfSickDaysFall1() throws Exception {

        List<SjukfallIntyg> intygsUnderlag = new ArrayList<>();

        // First add a simple intyg with a simple interval
        intygsUnderlag.add(createIntyg(createInterval("2016-02-01", "2016-02-10")));
        intygsUnderlag.add(createIntyg(createInterval("2016-02-12", "2016-02-20")));
        assertEquals(19, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));
    }

    /**
     * Test FALL3 from confluence document /Krav/Rehabstod/Berakning av sjukfall.
     * @throws Exception
     */
    @Test
    public void testGetEffectiveNumberOfSickDaysFall3() throws Exception {

        List<SjukfallIntyg> intygsUnderlag = new ArrayList<>();

        // First add a simple intyg with a simple interval
        intygsUnderlag.add(createIntyg(createInterval("2016-02-01", "2016-02-10")));
        intygsUnderlag.add(createIntyg(createInterval("2016-02-12", "2016-02-20")));
        intygsUnderlag.add(createIntyg(createInterval("2016-02-21", "2016-02-25")));
        assertEquals(24, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));
    }

    /**
     * Test FALL5 from confluence document /Krav/Rehabstod/Berakning av sjukfall.
     * @throws Exception
     */
    @Test
    public void testGetEffectiveNumberOfSickDaysFall5() throws Exception {

        List<SjukfallIntyg> intygsUnderlag = new ArrayList<>();

        // First add a simple intyg with a simple interval
        intygsUnderlag.add(createIntyg(createInterval("2016-02-12", "2016-02-20")));
        intygsUnderlag.add(createIntyg(createInterval("2016-02-12", "2016-02-25")));
        assertEquals(14, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));
    }

    /**
     * Test FALL6 from confluence document /Krav/Rehabstod/Berakning av sjukfall.
     * @throws Exception
     */
    @Test
    public void testGetEffectiveNumberOfSickDaysFall6() throws Exception {

        List<SjukfallIntyg> intygsUnderlag = new ArrayList<>();

        // First add a simple intyg with a simple interval
        intygsUnderlag.add(createIntyg(createInterval("2016-02-12", "2016-02-20")));
        intygsUnderlag.add(createIntyg(createInterval("2016-02-15", "2016-02-25")));
        assertEquals(14, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));
    }

    /**
     * Test that intervals that are abut are counted equals as spanning the entire period.
     * @throws Exception
     */
    @Test
    public void testGetEffectiveNumberOfSickDaysAbutIntervals() throws Exception {

        List<SjukfallIntyg> intygsUnderlag = new ArrayList<>();
        intygsUnderlag.add(createIntyg(createInterval("2016-02-12", "2016-02-20"), createInterval("2016-02-21", "2016-02-26"), createInterval("2016-02-26", "2016-03-19")));

        List<SjukfallIntyg> intygsUnderlag2 = new ArrayList<>();
        intygsUnderlag2.add(createIntyg(createInterval("2016-02-12", "2016-03-19")));


        assertEquals(37, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));
        assertEquals(37, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag2));
    }

    @Test
    public void testGetEffectiveNumberOfSickDays() throws Exception {
        List<SjukfallIntyg> intygsUnderlag = new ArrayList<>();

        // First add a simple intyg with a simple interval
        intygsUnderlag.add(createIntyg(createInterval("2016-01-20", "2016-02-10")));
        assertEquals(22, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));

        // Add one that should be "swallowed" entirely by the first and should therefore not effect the length
        intygsUnderlag.add(createIntyg(createInterval("2016-02-02", "2016-02-10")));
        assertEquals(22, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));

        // Add another intyg where one of the intervals should overlap/extend the first (by 2 days) and adds  separate
        // interval of 20 days
        intygsUnderlag.add(createIntyg(createInterval("2016-01-25", "2016-02-12"), createInterval("2016-02-20", "2016-03-10")));
        assertEquals(22 + 2 + 20, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));

        // add another that effectively encompasses all the previous ones except a few days of the last one
        intygsUnderlag.add(createIntyg(createInterval("2016-01-02", "2016-03-08")));
        assertEquals(69, SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(intygsUnderlag));

    }

    @Test
    public void testMergeIntervalsSimple() throws Exception {

        List<LocalDateInterval> intervals = new ArrayList<>();
        final LocalDateInterval a = createInterval("2016-01-01", "2016-01-20");
        final LocalDateInterval b = createInterval("2016-01-20", "2016-02-10");
        intervals.add(a);
        intervals.add(b);

        final List<LocalDateInterval> result = SjukfallLangdCalculator.mergeIntervals(intervals);

        assertEquals(1, result.size());
        assertEquals(a.getStartDate(), result.get(0).getStartDate());
        assertEquals(b.getEndDate(), result.get(0).getEndDate());

    }

    @Test
    public void testMergeIntervalsDontMergeDiscreteIntervals() throws Exception {

        List<LocalDateInterval> intervals = new ArrayList<>();
        final LocalDateInterval a = createInterval("2016-01-01", "2016-01-20");
        final LocalDateInterval b = createInterval("2016-01-21", "2016-02-10");
        intervals.add(a);
        intervals.add(b);

        final List<LocalDateInterval> result = SjukfallLangdCalculator.mergeIntervals(intervals);

        assertEquals(2, result.size());
        Assert.assertTrue(result.contains(a));
        Assert.assertTrue(result.contains(b));

    }

    @Test
    public void testMergeIntervalsComplex() throws Exception {

        // First add a simple intyg with a simple interval
        LocalDateInterval a = createInterval("2016-01-20", "2016-02-10");

        // Add one that should be "swallowed" entirely by the first and should therefore not effect the length
        LocalDateInterval aCopy = createInterval("2016-02-02", "2016-02-10");

        // Add another intyg where one of the intervals should overlap/extend the first (by 2 days) and adds  separate
        // interval of 20 days
        final LocalDateInterval b1 = createInterval("2016-01-25", "2016-02-12");
        final LocalDateInterval b2 = createInterval("2016-02-20", "2016-03-10");

        // add another that effectively encompasses all the previous ones except a few days of the last one
        final LocalDateInterval c = createInterval("2016-01-02", "2016-03-08");

        List<LocalDateInterval> intervals = new ArrayList<>();
        intervals.add(a);
        intervals.add(aCopy);
        intervals.add(b1);
        intervals.add(b2);
        intervals.add(c);

        final List<LocalDateInterval> result = SjukfallLangdCalculator.mergeIntervals(intervals);
        assertEquals(1, result.size());
        assertEquals(c.getStartDate(), result.get(0).getStartDate());
        assertEquals(b2.getEndDate(), result.get(0).getEndDate());
    }

    private SjukfallIntyg createIntyg(LocalDateInterval... intervals) {
        final List<Formaga> formagor = new ArrayList();

        for (LocalDateInterval i : intervals) {
            int nedsattning = 100;
            formagor.add(new Formaga(i.getStartDate(), i.getEndDate(), nedsattning));
        }

        IntygData intygData = new IntygData();
        intygData.setFormagor(formagor);

        SjukfallIntyg.SjukfallIntygBuilder builder = new SjukfallIntyg.SjukfallIntygBuilder(intygData, LocalDate.now(), 0);
        return builder.build();
    }

    private LocalDateInterval createInterval(String startDate, String endDate) {
        return new LocalDateInterval(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }
}
