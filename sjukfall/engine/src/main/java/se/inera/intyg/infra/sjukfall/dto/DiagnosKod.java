/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 *
 * This file is part of SKLIntyg (https://github.com/sklintyg).
 *
 * SKLIntyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SKLIntyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.sjukfall.dto;

/**
 * Created by martin on 10/02/16.
 */
public class DiagnosKod {

    public static final int KOD_LENGTH = 7;

    private String originalCode;
    private String code;
    private String name;

    public DiagnosKod(String line) {
        if (line.length() >= KOD_LENGTH) {
            code = cleanKod(line.substring(0, KOD_LENGTH));
            name = line.substring(KOD_LENGTH).trim();
        } else {
            code = cleanKod(line);
        }
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static String cleanKod(String kod) {
        String cleanedKod = kod.trim().toUpperCase();
        return cleanedKod.replaceAll("[^A-Z0-9\\-]", "");
    }
}
