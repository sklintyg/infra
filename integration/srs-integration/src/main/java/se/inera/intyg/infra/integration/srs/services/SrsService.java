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
package se.inera.intyg.infra.integration.srs.services;

import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v2.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v2.Utdatafilter;
import se.inera.intyg.infra.integration.srs.model.SrsForDiagnosisResponse;
import se.inera.intyg.infra.integration.srs.model.SrsQuestion;
import se.inera.intyg.infra.integration.srs.model.SrsQuestionResponse;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.EgenBedomningRiskType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.util.List;

public interface SrsService {

    /**
     * Perform a getSrsInformation for a given Personnummer and diagnosis.
     *
     * @param user
     *            user which made the request
     * @param intygId
     *            id of the intyg used for SRS.
     * @param personnummer
     *            {@link Personnummer} for the patient concerned.
     * @param diagnosisCode
     *            string representation of the diagnosis code.
     * @param filter
     *            Utdatafilter with desired response filters.
     * @param answers
     *            Answers from the user.
     * @return {@link SrsResponse} with {@link Diagnosprediktionstatus} OK or PREDIKTIONSMODELL_SAKNAS
     * @throws InvalidPersonNummerException
     */
    SrsResponse getSrs(IntygUser user, String intygId, Personnummer personnummer, String diagnosisCode, Utdatafilter filter,
            List<SrsQuestionResponse> answers) throws InvalidPersonNummerException;

    /**
     * Retreives the questions to be displayed in the GUI.
     *
     * @param diagnos
     *            the diagnosCode.
     * @return a sorted list of questions to be displayed
     */
    List<SrsQuestion> getQuestions(String diagnos);

    Samtyckesstatus getConsent(String careUnitHsaId, Personnummer personId) throws InvalidPersonNummerException;

    ResultCodeEnum setConsent(String careUnitHsaId, Personnummer personId, boolean samtycke) throws InvalidPersonNummerException;

    /**
     * Get's a previously set "user's own opinion" on a given care giver and certificate.
     * @param careGiverHsaId the hsa id of the care giver
     * @param careUnitHsaId the HSA-id of the certificate's/user's care unit
     * @param certificateId the id of the certificate
     * @param diagnosisCode the diagnosis code for which the risk was predicted
     * @return a previously set opinion or null if no such opinion has been set for the given care unit and certificate
     */
    EgenBedomningRiskType getOwnOpinion(String careGiverHsaId, String careUnitHsaId, String certificateId, String diagnosisCode);

    /**
     * Sets the user's own opinion on the risk prediction.
     * @param careGiverHsaId the HSA-id of the user's care giver
     * @param careUnitHsaId the HSA-id of the certificate's/user's care unit
     * @param certificateId the id of the certificate for which the risk was predicted
     * @param diagnosisCode the diagnosis code for which the risk was predicted
     * @param ownOpinion the users own opinion
     * @return
     */
    ResultCodeEnum setOwnOpinion(String careGiverHsaId, String careUnitHsaId, String certificateId, String diagnosisCode,
                                 EgenBedomningRiskType ownOpinion);

    /**
     * Fetches all the diagnosis codes which are supported by SRS.
     *
     * @return a list containing all the supported diagnosis codes. All sub-diagnosis are also supported.
     */
    List<String> getAllDiagnosisCodes();

    /**
     * Fetches all non-predictive parts of SRS info based on the supplied diagnose code.
     *
     * @param diagnosisCode
     *            string representation of the diagnosis code.
     *
     * @return {@link SrsForDiagnosisResponse} with static srs info related to the supplied diagnosis code
     */
    SrsForDiagnosisResponse getSrsForDiagnose(String diagnosisCode);
}
