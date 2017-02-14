/**
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.sjukfall.dto;

import java.time.LocalDate;

/**
 * @author Magnus Ekstrand on 2017-02-14.
 */
public class IntygParametrar {

    private String vardgivareId;
    private String vardgivareNamn;

    private Sortering sortering;
    private LangdIntervall langdIntervall;

    private int maxIntygsGlapp;
    private LocalDate aktivtDatum;

    // constructor

    public IntygParametrar(Sortering sortering, LangdIntervall langdIntervall, int maxIntygsGlapp, LocalDate aktivtDatum) {
        this.sortering = sortering;
        this.langdIntervall = langdIntervall;
        this.maxIntygsGlapp = maxIntygsGlapp;
        this.aktivtDatum = aktivtDatum;
    }

    // getters and setters

    public String getVardgivareId() {
        return vardgivareId;
    }

    public void setVardgivareId(String vardgivareId) {
        this.vardgivareId = vardgivareId;
    }

    public String getVardgivareNamn() {
        return vardgivareNamn;
    }

    public void setVardgivareNamn(String vardgivareNamn) {
        this.vardgivareNamn = vardgivareNamn;
    }

    public Sortering getSortering() {
        return sortering;
    }

    public LangdIntervall getLangdIntervall() {
        return langdIntervall;
    }

    public int getMaxIntygsGlapp() {
        return maxIntygsGlapp;
    }

    public LocalDate getAktivtDatum() {
        return aktivtDatum;
    }

}