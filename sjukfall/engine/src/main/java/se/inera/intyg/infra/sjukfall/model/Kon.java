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
package se.inera.intyg.infra.sjukfall.model;

/**
 * @author Magnus Ekstrand on 2017-02-10.
 */
public enum Kon {

    F("Kvinna"), M("Man"), UNKNOWN("Okänt");

    String desc;

    Kon(String desc) {
        this.desc = desc;
    }

    public String getDescription() {
        return this.desc;
    }

    public static Kon getKonFromString(String kon) {

        if (kon != null && kon.length() == 1) {
            if (kon.matches("^\\d*[13579]$")) {
                return M;
            } else if (kon.matches("^\\d*[02468]$")) {
                return F;
            }
        }

        return UNKNOWN;
    }

}
