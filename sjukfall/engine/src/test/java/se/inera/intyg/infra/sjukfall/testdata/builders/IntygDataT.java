/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of rehabstod (https://github.com/sklintyg/rehabstod).
 *
 * rehabstod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rehabstod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.sjukfall.testdata.builders;

import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class IntygDataT {

    private IntygDataT() {
    }

    public static class IntygDataBuilder implements Builder<IntygData> {

        private String intygId;
        private String diagnoskod;
        private String patientId;
        private String patientNamn;
        private String lakareId;
        private String lakareNamn;
        private String vardenhetId;
        private String vardenhetNamn;

        private LocalDateTime signeringsTidpunkt;

        private List<Formaga> formagor;

        private boolean enkeltIntyg;

        public IntygDataBuilder() {
        }

        public IntygDataBuilder intygsId(String intygsId) {
            this.intygId = intygsId;
            return this;
        }

        public IntygDataBuilder diagnoskod(String diagnoskod) {
            this.diagnoskod = diagnoskod;
            return this;
        }

        public IntygDataBuilder patientId(String patientId) {
            this.patientId = patientId;
            return this;
        }

        public IntygDataBuilder patientNamn(String patientNamn) {
            this.patientNamn = patientNamn;
            return this;
        }

        public IntygDataBuilder lakareId(String lakareId) {
            this.lakareId = lakareId;
            return this;
        }

        public IntygDataBuilder lakareNamn(String lakareNamn) {
            this.lakareNamn = lakareNamn;
            return this;
        }

        public IntygDataBuilder vardenhetId(String vardenhetId) {
            this.vardenhetId = vardenhetId;
            return this;
        }

        public IntygDataBuilder vardenhetNamn(String vardenhetNamn) {
            this.vardenhetNamn = vardenhetNamn;
            return this;
        }

        public IntygDataBuilder formagor(List<Formaga> formagor) {
            this.formagor = formagor;
            return this;
        }

        public IntygDataBuilder enkeltIntyg(boolean enkeltIntyg) {
            this.enkeltIntyg = enkeltIntyg;
            return this;
        }

        public IntygDataBuilder signeringsTidpunkt(LocalDateTime signeringsTidpunkt) {
            this.signeringsTidpunkt = signeringsTidpunkt;
            return this;
        }

        @Override
        public IntygData build() {
            IntygData intygData = new IntygData();
            intygData.setIntygId(this.intygId);
            intygData.setDiagnosKod(this.diagnoskod);
            intygData.setPatientId(this.patientId);
            intygData.setPatientNamn(this.patientNamn);
            intygData.setLakareId(this.lakareId);
            intygData.setLakareNamn(this.lakareNamn);
            intygData.setVardenhetId(this.vardenhetId);
            intygData.setVardenhetNamn(this.vardenhetNamn);
            intygData.setSigneringsTidpunkt(this.signeringsTidpunkt);
            intygData.setFormagor(this.formagor);
            intygData.setEnkeltIntyg(this.enkeltIntyg);

            return intygData;
        }

    }
}
