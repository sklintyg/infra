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
package se.inera.intyg.infra.sjukfall.testdata;

import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.testdata.builders.FormagaT;
import se.inera.intyg.infra.sjukfall.testdata.builders.IntygDataT;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Created by Magnus Ekstrand on 2016-02-11.
 */
public class AktivtIntygLineMapper {

    Set<String[]> fields;


    public AktivtIntygLineMapper() {
        fields = new HashSet();
    }

    public static List<IntygData> map(List<String> lines) {
        AktivtIntygLineMapper mapper = new AktivtIntygLineMapper();

        for (String line : lines) {
            String[] splitData = mapper.toArray(line);
            if (splitData != null) {
                mapper.fields.add(splitData);
            }
        }

        return mapper.map(mapper.fields);
    }

    private List<IntygData> map(Set<String[]> fields) {
        List<IntygData> intygData = new ArrayList();

        Iterator<String[]> iter = fields.iterator();
        while (iter.hasNext()) {
            intygData.add(intygData(iter.next()));
        }

        return intygData;
    }

    private IntygData intygData(String[] data) {
        FormagaFieldSetMapper ffsm = new FormagaFieldSetMapper();

        // CHECKSTYLE:OFF MagicNumber
        return new IntygDataT.IntygDataBuilder()
                .intygsId(data[0])
                .diagnoskod(data[5])
                .patientId(data[1])
                .patientNamn(patientNamn(data[2], data[3], data[4]))
                .lakareId(data[8])
                .lakareNamn(data[9])
                .vardenhetId(data[6])
                .vardenhetNamn(data[7])
                .signeringsTidpunkt(LocalDateTime.parse(data[12]))
                .formagor(ffsm.map(data[10]))
                .enkeltIntyg(Boolean.valueOf(data[11]))
                .build();
        // CHECKSTYLE:ON MagicNumber
    }

    private String patientNamn(String fnamn, String mnamn, String enamn) {
        String pnamn = "";

        if (fnamn != null) {
            pnamn = fnamn;
        }
        if (mnamn != null) {
            pnamn = pnamn.isEmpty() ? mnamn : pnamn + " " + mnamn;
        }
        if (enamn != null) {
            pnamn = pnamn.isEmpty() ? enamn : pnamn + " " + enamn;
        }

        return pnamn;
    }

    private String[] toArray(String csv) {
        if (csv != null) {
            return csv.split("\\s*,\\s*");
        }

        return null;
    }

    class FormagaFieldSetMapper {

        public List<Formaga> map(String arbetsformaga) {
            List<Formaga> formagaList = new ArrayList();
            String[] formagor = arbetsformaga.replace("[", "").replace("]", "").split("\\|");

            for (String formaga : formagor) {
                String[] fields = formaga.split(";");
                formagaList.add(formaga(LocalDate.parse(fields[0]), LocalDate.parse(fields[1]), Integer.parseInt(fields[2])));
            }

            return formagaList;
        }

        private Formaga formaga(LocalDate start, LocalDate slut, int nedsatthet) {
            return new FormagaT.FormagaBuilder().startdatum(start).slutdatum(slut).nedsattning(nedsatthet).build();
        }
    }

}
