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


import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.infra.sjukfall.dto.Vardenhet;

/**
 * Created by Magnus Ekstrand on 2016-02-10.
 */
public final class LakareT {

    public LakareT() {
    }

    public static class LakareBuilder implements Builder<Lakare> {

        private String hsaId;
        private String fullstandigtNamn;
        private Vardenhet vardenhet;

        public LakareBuilder() {
        }

        public LakareBuilder hsaId(String hsaId) {
            this.hsaId = hsaId;
            return this;
        }

        public LakareBuilder fullstandigtNamn(String fullstandigtNamn) {
            this.fullstandigtNamn = fullstandigtNamn;
            return this;
        }

        public LakareBuilder enhet(Vardenhet enhet) {
            this.vardenhet = enhet;
            return this;
        }

        @Override
        public Lakare build() {
            Lakare lakare = new Lakare(hsaId, fullstandigtNamn, vardenhet);
            return lakare;
        }
    }
}
