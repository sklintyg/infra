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

import java.time.LocalDateTime;
import java.util.List;

// CHECKSTYLE:OFF ParameterNumber
public class SrsResponse {
    private Integer predictionLevel;
    private String predictionDescription;
    private ImmutableList<SrsRecommendation> atgarderObs;
    private ImmutableList<SrsRecommendation> atgarderRek;
    private ImmutableList<SrsRecommendation> atgarderExt;
    private String predictionDiagnosisCode;
    private String predictionDiagnosisDescription;
    private String predictionStatusCode;
    private ImmutableList<SrsQuestionResponse> predictionQuestionsResponses;
    private String predictionPhysiciansOwnOpinionRisk;
    private LocalDateTime predictionTimestamp;
    private String atgarderDiagnosisCode;
    private String atgarderDiagnosisDescription;
    private String atgarderStatusCode;
    private ImmutableList<Integer> statistikNationellStatistik;
    private String statistikDiagnosisCode;
    private String statistikDiagnosisDescription;
    private String statistikStatusCode;
    private Double predictionProbabilityOverLimit;
    private Double predictionPrevalence;

    public SrsResponse(Integer level, String description, List<SrsRecommendation> atgarderObs, List<SrsRecommendation> atgarderRek,
                       List<SrsRecommendation> atgarderExt, String predictionDiagnosisCode, String predictionStatusCode,
                       List<SrsQuestionResponse> predictionQuestionsResponses,
                       String predictionPhysiciansOwnOpinionRisk,
                       LocalDateTime predictionTimestamp,
                       String atgarderDiagnosisCode, String atgarderStatusCode,
                       String statistikDiagnosisCode, String statistikStatusCode, Double predictionProbabilityOverLimit,
                       Double predictionPrevalence, List<Integer> statistikNationellStatistikData) {
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
        if (atgarderExt == null) {
            this.atgarderExt = null;
        } else {
            this.atgarderExt = ImmutableList.copyOf(atgarderExt);
        }

        if (statistikNationellStatistikData == null) {
            this.statistikNationellStatistik = null;
        } else {
            this.statistikNationellStatistik = ImmutableList.copyOf(statistikNationellStatistikData);
        }

        if (predictionQuestionsResponses == null) {
            this.predictionQuestionsResponses = null;
        } else {
            this.predictionQuestionsResponses = ImmutableList.copyOf(predictionQuestionsResponses);
        }

        this.predictionPhysiciansOwnOpinionRisk = predictionPhysiciansOwnOpinionRisk;
        this.predictionTimestamp = predictionTimestamp;

        this.predictionDiagnosisCode = predictionDiagnosisCode;
        this.predictionStatusCode = predictionStatusCode;

        this.atgarderDiagnosisCode = atgarderDiagnosisCode;
        this.atgarderStatusCode = atgarderStatusCode;

        this.statistikDiagnosisCode = statistikDiagnosisCode;
        this.statistikStatusCode = statistikStatusCode;

        this.predictionProbabilityOverLimit = predictionProbabilityOverLimit;
        this.predictionPrevalence = predictionPrevalence;
    }

    public Integer getPredictionLevel() {
        return predictionLevel;
    }

    public String getPredictionDescription() {
        return predictionDescription;
    }

    public ImmutableList<SrsRecommendation> getAtgarderObs() {
        return atgarderObs;
    }

    public ImmutableList<SrsRecommendation> getAtgarderRek() {
        return atgarderRek;
    }

    public ImmutableList<SrsRecommendation> getAtgarderExt() {
        return atgarderExt;
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

    public List<Integer> getStatistikNationellStatistik() {
        return statistikNationellStatistik;
    }

    public Double getPredictionProbabilityOverLimit() {
        return predictionProbabilityOverLimit;
    }

    public String getPredictionPhysiciansOwnOpinionRisk() {
        return predictionPhysiciansOwnOpinionRisk;
    }

    public LocalDateTime getPredictionTimestamp() {
        return predictionTimestamp;
    }

    public ImmutableList<SrsQuestionResponse> getPredictionQuestionsResponses() {
        return predictionQuestionsResponses;
    }

    public Double getPredictionPrevalence() {
        return predictionPrevalence;
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
