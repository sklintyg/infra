package se.inera.intyg.infra.integration.srs.services;

import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.infra.integration.srs.model.SjukskrivningsGrad;
import se.inera.intyg.infra.integration.srs.model.SrsException;
import se.inera.intyg.infra.integration.srs.model.SrsQuestion;
import se.inera.intyg.infra.integration.srs.model.SrsQuestionResponse;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.util.List;

public interface SrsService {

    /**
     * Perform a getSrsInformation for a given Personnummer and diagnosis.
     *
     * @param intygId            id of the intyg used for SRS.
     * @param personnummer       {@link Personnummer} for the patient concerned.
     * @param diagnosisCode      string representation of the diagnosis code.
     * @param filter             Utdatafilter with desired response filters.
     * @param answers            Answers from the user.
     * @param sjukskrivningsGrad The degree of sickleave considered for the patient.
     * @return {@link SrsResponse} with {@link Diagnosprediktionstatus} OK or PREDIKTIONSMODELL_SAKNAS
     * @throws InvalidPersonNummerException
     * @throws SrsException
     */
    SrsResponse getSrs(String intygId, Personnummer personnummer, String diagnosisCode, Utdatafilter filter,
            List<SrsQuestionResponse> answers, SjukskrivningsGrad sjukskrivningsGrad) throws InvalidPersonNummerException, SrsException;

    /**
     * Retreives the questions to be displayed in the GUI.
     *
     * @param diagnos the diagnosCode.
     * @return a sorted list of questions to be displayed
     */
    List<SrsQuestion> getQuestions(String diagnos);

    Samtyckesstatus getConsent(String hsaId, Personnummer personId) throws InvalidPersonNummerException;

    ResultCodeEnum setConsent(String hsaId, Personnummer personId, boolean samtycke) throws InvalidPersonNummerException;

}
