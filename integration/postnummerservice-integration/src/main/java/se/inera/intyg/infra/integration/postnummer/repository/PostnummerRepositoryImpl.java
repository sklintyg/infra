/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import se.inera.intyg.infra.integration.postnummer.model.Omrade;

@Component
public class PostnummerRepositoryImpl implements PostnummerRepository {

    private Map<String, List<Omrade>> postnummerRepository = new HashMap<String, List<Omrade>>();
    private List<String> kommunList = new ArrayList<String>();

    @Override
    public List<Omrade> getOmradeByPostnummer(String postnummer) {
        return postnummerRepository.get(postnummer);
    }

    @Override
    public List<String> getKommunList() {
        return kommunList;
    }

    @Override
    public int nbrOfPostnummer() {
        return postnummerRepository.size();
    }

    void addOmrade(Omrade omrade) {
        String postnummer = omrade.getPostnummer();
        if (postnummerRepository.containsKey(postnummer)) {
            postnummerRepository.get(postnummer).add(omrade);
        } else {
            List<Omrade> omradeList = new ArrayList<Omrade>();
            omradeList.add(omrade);
            postnummerRepository.put(postnummer, omradeList);
        }

        if (!kommunList.contains(omrade.getKommun())) {
            kommunList.add(omrade.getKommun());
        }
    }
}
