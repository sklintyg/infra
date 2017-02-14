/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 * <p/>
 * This file is part of sklintyg (https://github.com/sklintyg).
 * <p/>
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.sjukfall.engine;

import java.time.LocalDate;
import java.util.List;

import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;

/**
 * @author Magnus Ekstrand on 2017-02-10.
 */
public class AktivtIntyg extends IntygData {

    public static final int HASH_SEED = 31;

    private LocalDate startDatum;
    private LocalDate slutDatum;

    private boolean aktivtIntyg;

    public AktivtIntyg(AktivtIntygBuilder builder) {
        super();

        this.startDatum = builder.startDatum;
        this.slutDatum = builder.slutDatum;
        this.aktivtIntyg = builder.aktivtIntyg;

        this.setIntygId(builder.intygData.getIntygId());
        this.setDiagnosKod(builder.intygData.getDiagnosKod());
        this.setPatientId(builder.intygData.getPatientId());
        this.setPatientNamn(builder.intygData.getPatientNamn());
        this.setLakareId(builder.intygData.getLakareId());
        this.setLakareNamn(builder.intygData.getLakareNamn());
        this.setVardenhetId(builder.intygData.getVardenhetId());
        this.setVardenhetNamn(builder.intygData.getVardenhetNamn());
        this.setSigneringsTidpunkt(builder.intygData.getSigneringsTidpunkt());
        this.setFormagor(builder.intygData.getFormagor());
        this.setEnkeltIntyg(builder.intygData.isEnkeltIntyg());
    }

    // Getters and setters

    public LocalDate getStartDatum() {
        return startDatum;
    }

    public LocalDate getSlutDatum() {
        return slutDatum;
    }

    public boolean isAktivtIntyg() {
        return aktivtIntyg;
    }

    public void setAktivtIntyg(boolean aktivtIntyg) {
        this.aktivtIntyg = aktivtIntyg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AktivtIntyg that = (AktivtIntyg) o;
        if (!startDatum.equals(that.startDatum)) {
            return false;
        }

        return slutDatum.equals(that.slutDatum);
    }

    @Override
    public int hashCode() {
        int result = startDatum.hashCode();
        result = HASH_SEED * result + slutDatum.hashCode();
        result = HASH_SEED * result + (aktivtIntyg ? 1 : 0);
        return result;
    }

    public static class AktivtIntygBuilder {

        private final IntygData intygData;

        private LocalDate startDatum;
        private LocalDate slutDatum;

        private boolean aktivtIntyg;

        public AktivtIntygBuilder(IntygData intygData, LocalDate aktivtDatum) {
            this.intygData = intygData;
            this.startDatum = lookupStartDatum(intygData.getFormagor());
            this.slutDatum = lookupSlutDatum(intygData.getFormagor());
            this.aktivtIntyg = hasAktivFormaga(intygData.getFormagor(), aktivtDatum);
        }

        public AktivtIntyg build() {
            return new AktivtIntyg(this);
        }

        private boolean hasAktivFormaga(List<Formaga> formagor, LocalDate aktivtDatum) {
            return formagor.stream()
                    .anyMatch(f -> isAktivFormaga(aktivtDatum, f));
        }

        private boolean isAktivFormaga(LocalDate aktivtDatum, Formaga f) {
            return f.getStartdatum().compareTo(aktivtDatum) < 1 && f.getSlutdatum().compareTo(aktivtDatum) > -1;
        }

        private LocalDate lookupStartDatum(List<Formaga> formagor) {
            Formaga formaga = formagor.stream()
                    .min((o1, o2) -> o1.getStartdatum().compareTo(o2.getStartdatum())).get();
            return formaga.getStartdatum();
        }

        private LocalDate lookupSlutDatum(List<Formaga> formagor) {
            Formaga formaga = formagor.stream()
                    .max((o1, o2) -> o1.getSlutdatum().compareTo(o2.getSlutdatum())).get();
            return formaga.getSlutdatum();
        }

    }

}