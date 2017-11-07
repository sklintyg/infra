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
package se.inera.intyg.infra.integration.srs.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Created by marced on 2017-11-06.
 */
public final class SrsForDiagnosisResponse {
    private final String diagnosisCode;
    private final String atgarderStatusCode;
    private final ImmutableList<String> atgarderObs;
    private final ImmutableList<String> atgarderRek;
    private final String statistikBild;
    private final String statistikStatusCode;

    public SrsForDiagnosisResponse(List<String> atgarderObs, List<String> atgarderRek, String statistikBild,
            String diagnosisCode, String atgarderStatusCode, String statistikStatusCode) {
        if (atgarderObs == null) {
            this.atgarderObs = null;
        } else {
            this.atgarderObs = ImmutableList.copyOf(atgarderObs);
        }
        if (atgarderRek == null) {
            this.atgarderRek = null;
        } else {
            this.atgarderRek = ImmutableList.copyOf(atgarderRek);
        }
        this.statistikBild = statistikBild;
        this.diagnosisCode = diagnosisCode;
        this.atgarderStatusCode = atgarderStatusCode;
        this.statistikStatusCode = statistikStatusCode;
    }

    public ImmutableList<String> getAtgarderObs() {
        return atgarderObs;
    }

    public ImmutableList<String> getAtgarderRek() {
        return atgarderRek;
    }

    public String getStatistikBild() {
        return statistikBild;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public String getAtgarderStatusCode() {
        return atgarderStatusCode;
    }

    public String getStatistikStatusCode() {
        return statistikStatusCode;
    }
}
