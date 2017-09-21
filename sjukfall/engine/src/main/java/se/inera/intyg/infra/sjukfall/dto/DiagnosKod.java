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
package se.inera.intyg.infra.sjukfall.dto;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by martin on 10/02/16.
 */
public class DiagnosKod {

    private static final int KOD_LENGTH = 7;

    private String originalCode;
    private String cleanedCode;
    private String name;

    public DiagnosKod(String originalCode) {
        if (StringUtils.isBlank(originalCode)) {
            throw new IllegalArgumentException("Argument 'originalCode' in call to DiagnosKod is either empty, null or blank");
        }

        this.originalCode = originalCode;
        if (this.originalCode.length() >= KOD_LENGTH) {
            this.cleanedCode = cleanKod(this.originalCode.substring(0, KOD_LENGTH));
            this.name = this.originalCode.substring(KOD_LENGTH).trim();
        } else {
            this.cleanedCode = cleanKod(this.originalCode);
        }
    }

    public String getOriginalCode() {
        return originalCode;
    }

    public String getName() {
        return name;
    }

    public String getCleanedCode() {
        return cleanedCode;
    }

    public static String cleanKod(String kod) {
        String cleanedKod = kod.trim().toUpperCase();
        return cleanedKod.replaceAll("[^A-Z0-9\\-]", "");
    }
}
