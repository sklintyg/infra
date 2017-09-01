/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.infra.sjukfall.dto.IntygData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Magnus Ekstrand on 2017-02-10.
 */
public class SjukfallIntygPatientCreator {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallIntygPatientCreator.class);

    // - - - API - - -

    public Map<Integer, List<SjukfallIntyg>> create(final List<IntygData> intygsData, final int maxIntygsGlapp,
            final LocalDate aktivtDatum) {
        LOG.debug("Start creating a map of 'sjukfallintyg'...");

        // Create the map
        Map<Integer, List<SjukfallIntyg>> map = createMap(intygsData, maxIntygsGlapp, aktivtDatum);

        LOG.debug("...stop creating a map of 'sjukfallintyg'.");
        return map;
    }

    // - - - Package scope - - -

    Map<Integer, List<SjukfallIntyg>> createMap(final List<IntygData> intygsData, final int maxIntygsGlapp, final LocalDate aktivtDatum) {
        LOG.debug("  2. Create the map");

        // Transform intygsdata to an internal format
        List<SjukfallIntyg> sjukfallIntygList = createList(intygsData, aktivtDatum);

        // Ensure the that indygsData list is sort by 'signeringsTidpunkt' with descending order
        Comparator<SjukfallIntyg> dateComparator = Comparator.comparing(SjukfallIntyg::getSigneringsTidpunkt, Comparator.reverseOrder());

        sjukfallIntygList = sjukfallIntygList.stream()
                .sorted(dateComparator)
                .collect(Collectors.toList());

        return collecIntyg(sjukfallIntygList, maxIntygsGlapp);
    }

    // - - - Private scope - - -

    private List<SjukfallIntyg> createList(List<IntygData> intygsData, LocalDate aktivtDatum) {
        LOG.debug("     a. Transform 'intygsdata' to intermediate format");

        List<SjukfallIntyg> list = new ArrayList<>();
        for (IntygData i : intygsData) {
            SjukfallIntyg v = new SjukfallIntyg.SjukfallIntygBuilder(i, aktivtDatum).build();
            list.add(v);
        }

        return list;
    }

    /**
     * Collect certificates inside a map where each entry's value is a list with SjukfallIntyg.
     */
    private Map<Integer, List<SjukfallIntyg>> collecIntyg(List<SjukfallIntyg> intygsData, int maxIntygsGlapp) {

        Map<Integer, List<SjukfallIntyg>> map = new HashMap<>();

        Integer key = 0;
        SjukfallIntyg first = intygsData.get(0);
        collectIntyg(intygsData, map, key, first, maxIntygsGlapp);

        return map;
    }

    // Iterate over intygsdata and collect continuous certificates.
    private void collectIntyg(List<SjukfallIntyg> input, Map<Integer, List<SjukfallIntyg>> output, Integer key, SjukfallIntyg first,
            int maxIntygsGlapp) {

        if (input.contains(first)) {
            input.remove(first);
        }

        // Add to output map
        output.computeIfAbsent(key, v -> new ArrayList<>()).add(first);

        // Return if no more input
        if (input.isEmpty()) {
            return;
        }
        int nextKey = key;

        // Lookup if the first and second intyg are within the specified amount of days
        // Rule: startDatum - slutDatum < maxIntygsGlapp
        SjukfallIntyg second = input.get(0);
        if (second.getSlutDatum().isBefore(first.getStartDatum().minusDays(maxIntygsGlapp + 1))) {
            nextKey++;
        }

        // Recursive call
        collectIntyg(input, output, nextKey, second, maxIntygsGlapp);
    }
}
