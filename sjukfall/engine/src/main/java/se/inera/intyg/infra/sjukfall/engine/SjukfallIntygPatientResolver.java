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
import se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Magnus Ekstrand on 2017-02-10.
 */
public class SjukfallIntygPatientResolver {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallIntygPatientResolver.class);

    private SjukfallIntygPatientCreator creator;

    // constructor

    public SjukfallIntygPatientResolver(SjukfallIntygPatientCreator creator) {
        this.creator = creator;
    }

    // api

    /**
     * Method is resolving sjukfall for a health care unit based on the unit's certificate information.
     * A map with patient id as key and a list of certificates associated with a sjukfall as value, will be returned.
     */
    public Map<Integer, List<SjukfallIntyg>> resolve(final List<IntygData> intygsData,
                                                     final int maxIntygsGlapp, final LocalDate aktivtDatum) {

        LOG.debug("Start resolving certificate information...");
        LOG.debug("  - max days between certificates: {}, active date: {}", maxIntygsGlapp, aktivtDatum);

        if (intygsData == null || intygsData.isEmpty()) {
            LOG.debug("There was no in-data! Returning empty list");
            return new HashMap<>();
        }

        if (maxIntygsGlapp < 0) {
            LOG.debug("Maximal days between certificates was {}. Value must be equal or greater than zero", maxIntygsGlapp);
            return new HashMap<>();
        }

        // Create a map with an enumerated Integer as key holding each sjukfalls intygsdata.
        // The map's values are sorted by signeringsTidpunkt with descending order.
        //Map<Integer, List<SjukfallIntyg>> createdMap = createMap(intygsData, maxIntygsGlapp, aktivtDatum);

        // Reduce the list
        //Map<Integer, List<SjukfallIntyg>> reducedMap = reduceMap(createdMap, maxIntygsGlapp, aktivtDatum);

        LOG.debug("...stop resolving certificate information.");
        //return reducedMap;
        return creator.create(intygsData, maxIntygsGlapp, aktivtDatum);
    }

    // - - -  Package scope  - - -

    /**
     * Method returns a map with intermediate IntygsData objects.
     */

    /*
    Map<Integer, List<SjukfallIntyg>> createMap(List<IntygData> intygsData, int maxIntygsGlapp,  LocalDate aktivtDatum) {
        if (intygsData == null || intygsData.isEmpty()) {
            return new HashMap<>();
        }

    }

    /*
    Map<Integer, List<SjukfallIntyg>> reduceMap(Map<Integer, List<SjukfallIntyg>> intygsMap, int maxIntygsGlapp,
                                                        LocalDate aktivtDatum) {

        LOG.debug("  - Reduce certificates. Future certificates will be removed.");

        Map<Integer, List<SjukfallIntyg>> resultMap = new HashMap<>();

        // For each entry in map, lookup "aktivtIntyg" within the list of IntygsData
        for (Map.Entry<Integer, List<SjukfallIntyg>> entry : intygsMap.entrySet()) {
            List<SjukfallIntyg> reducedList = reduceList(entry.getValue(), maxIntygsGlapp, aktivtDatum);
            if (!reducedList.isEmpty()) {
                resultMap.put(entry.getKey(), reducedList);
            }
        }

        return resultMap;
    }

    List<SjukfallIntyg> reduceList(List<SjukfallIntyg> values, int maxIntygsGlapp, LocalDate aktivtDatum) {
        return values.stream()
            .filter(value -> aktivtDatum.plusDays(maxIntygsGlapp + 1).isAfter(value.getStartDatum()))
            .collect(Collectors.toList());
    }
    */

}
