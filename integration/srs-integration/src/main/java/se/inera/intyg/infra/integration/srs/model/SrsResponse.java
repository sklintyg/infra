/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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

import com.google.common.collect.ImmutableList;

import java.util.List;

// CHECKSTYLE:OFF ParameterNumber
public class SrsResponse {
    private Integer predictionLevel;
    private String predictionDescription;
    private ImmutableList<String> atgarderObs;
    private ImmutableList<String> atgarderRek;
    private String statistikBild;
    private String predictionDiagnosisCode;
    private String predictionDiagnosisDescription;
    private String predictionStatusCode;
    private String atgarderDiagnosisCode;
    private String atgarderDiagnosisDescription;
    private String atgarderStatusCode;
    private String statistikDiagnosisCode;
    private String statistikDiagnosisDescription;
    private String statistikStatusCode;

    public SrsResponse(Integer level, String description, List<String> atgarderObs, List<String> atgarderRek, String statistikBild,
            String predictionDiagnosisCode, String predictionStatusCode, String atgarderDiagnosisCode, String atgarderStatusCode,
            String statistikDiagnosisCode, String statistikStatusCode) {
        this.predictionLevel = level;
        this.predictionDescription = description;
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

        this.predictionDiagnosisCode = predictionDiagnosisCode;
        this.predictionStatusCode = predictionStatusCode;

        this.atgarderDiagnosisCode = atgarderDiagnosisCode;
        this.atgarderStatusCode = atgarderStatusCode;

        this.statistikDiagnosisCode = statistikDiagnosisCode;
        this.statistikStatusCode = statistikStatusCode;
    }

    public Integer getPredictionLevel() {
        return predictionLevel;
    }

    public String getPredictionDescription() {
        return predictionDescription;
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

    public String getPredictionStatusCode() {
        return predictionStatusCode;
    }

    public String getAtgarderStatusCode() {
        return atgarderStatusCode;
    }

    public String getStatistikStatusCode() {
        return statistikStatusCode;
    }

    public String getPredictionDiagnosisCode() {
        return predictionDiagnosisCode;
    }

    public String getPredictionDiagnosisDescription() {
        return predictionDiagnosisDescription;
    }

    public String getAtgarderDiagnosisCode() {
        return atgarderDiagnosisCode;
    }

    public String getAtgarderDiagnosisDescription() {
        return atgarderDiagnosisDescription;
    }

    public String getStatistikDiagnosisCode() {
        return statistikDiagnosisCode;
    }

    public String getStatistikDiagnosisDescription() {
        return statistikDiagnosisDescription;
    }

    public void setPredictionDiagnosisDescription(String predictionDiagnosisDescription) {
        this.predictionDiagnosisDescription = predictionDiagnosisDescription;
    }

    public void setAtgarderDiagnosisDescription(String atgarderDiagnosisDescription) {
        this.atgarderDiagnosisDescription = atgarderDiagnosisDescription;
    }

    public void setStatistikDiagnosisDescription(String statistikDiagnosisDescription) {
        this.statistikDiagnosisDescription = statistikDiagnosisDescription;
    }
}
// CHECKSTYLE:ON ParameterNumber
