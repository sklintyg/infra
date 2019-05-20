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

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getownopinion.v1.GetOwnOpinionRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getownopinion.v1.GetOwnOpinionResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getownopinion.v1.GetOwnOpinionResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v2.*;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.GetSRSInformationForDiagnosisRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.GetSRSInformationForDiagnosisResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformationfordiagnosis.v1.GetSRSInformationForDiagnosisResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setownopinion.v1.SetOwnOpinionRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setownopinion.v1.SetOwnOpinionResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.setownopinion.v1.SetOwnOpinionResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.types.v1.*;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.srs.model.*;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class SrsServiceImpl implements SrsService {

    private static final int POSTNUMMER_LENGTH = 5;

    private static final String HSA_ROOT = "1.2.752.129.2.1.4.1";
    private static final String CONSUMER_HSA_ID = "SE5565594230-B31";
    private static final String DIAGNOS_CODE_SYSTEM = "1.2.752.116.1.1.1.1.3";

    @Autowired
    private GetSRSInformationResponderInterface getSRSInformation;
    @Autowired
    private GetPredictionQuestionsResponderInterface getPrediction;
    @Autowired
    private GetConsentResponderInterface getConsent;
    @Autowired
    private SetConsentResponderInterface setConsent;
    @Autowired
    private GetDiagnosisCodesResponderInterface getDiagnosisCodes;
    @Autowired
    private GetSRSInformationForDiagnosisResponderInterface getSRSInformationForDiagnosis;
    @Autowired
    private GetOwnOpinionResponderInterface getOwnOpinion;
    @Autowired
    private SetOwnOpinionResponderInterface setOwnOpinion;

    @Override
    public SrsResponse getSrs(IntygUser user, String intygId, Personnummer personnummer, String diagnosisCode, Utdatafilter filter,
            List<SrsQuestionResponse> questions) throws InvalidPersonNummerException {

        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Answers are required to construct a valid request.");
        }
        GetSRSInformationResponseType response = getSRSInformation.getSRSInformation(
                createRequest(user, intygId, personnummer, diagnosisCode, filter, questions));

        if (response.getResultCode() != ResultCodeEnum.OK) {
            throw new IllegalArgumentException("Bad data from SRS");
        }

        // Schema mandates that this is of 1..*
        Bedomningsunderlag underlag = response.getBedomningsunderlag().get(0);

        Integer level = null;
        String description = null;
        String prediktionStatusCode = null;
        String statistikBild = null;
        String statistikStatusCode = null;
        List<Integer> statistikNationellStatistik = null;
        String predictionDiagnosisCode = null;
        String atgarderDiagnosisCode = null;
        String statistikDiagnosisCode = null;
        List<SrsRecommendation> atgarderObs = null;
        List<SrsRecommendation> atgarderRek = null;
        String atgarderStatusCode = null;
        Double predictionProbabilityOverLimit = null;
        Double predictionPrevalence = null;
        String prediktionLakarbedomningRisk = null;
        List<SrsQuestionResponse> prediktionsFragorSvar = null;
        LocalDateTime prediktionBerakningstidpunkt = null;

        if (underlag == null || underlag.getPrediktion() == null || underlag.getPrediktion().getDiagnosprediktion().isEmpty()
                || underlag.getPrediktion().getDiagnosprediktion().get(0)
                .getDiagnosprediktionstatus() == Diagnosprediktionstatus.PREDIKTIONSMODELL_SAKNAS) {
            prediktionStatusCode = Diagnosprediktionstatus.PREDIKTIONSMODELL_SAKNAS.value();
        } else if (filter.isPrediktion()) {
                level = underlag.getPrediktion().getDiagnosprediktion().get(0).getRisksignal().getRiskkategori().intValueExact();
                description = underlag.getPrediktion().getDiagnosprediktion().get(0).getRisksignal().getBeskrivning();
                prediktionStatusCode = underlag.getPrediktion().getDiagnosprediktion().get(0).getDiagnosprediktionstatus().value();
                predictionDiagnosisCode = Optional.ofNullable(underlag.getPrediktion().getDiagnosprediktion().get(0).getDiagnos())
                        .map(CVType::getCode)
                        .orElse(null);
                predictionProbabilityOverLimit = underlag.getPrediktion().getDiagnosprediktion().get(0).getSannolikhetOvergransvarde();
                predictionPrevalence = underlag.getPrediktion().getDiagnosprediktion().get(0).getPrevalens();
        } else if (underlag.getPrediktion().getDiagnosprediktion().get(0) != null) {
            // Always add prevalence if we have it regardless if the user requested prediction on a personal level
            predictionPrevalence = underlag.getPrediktion().getDiagnosprediktion().get(0).getPrevalens();
            predictionDiagnosisCode = Optional.ofNullable(underlag.getPrediktion().getDiagnosprediktion().get(0).getDiagnos())
                    .map(CVType::getCode)
                    .orElse(null);
            // Also check if we have a historic prediction
            if (underlag.getPrediktion().getDiagnosprediktion().get(0).getSannolikhetOvergransvarde() != null) {
                level = underlag.getPrediktion().getDiagnosprediktion().get(0).getRisksignal().getRiskkategori().intValueExact();
                description = underlag.getPrediktion().getDiagnosprediktion().get(0).getRisksignal().getBeskrivning();
                prediktionStatusCode = underlag.getPrediktion().getDiagnosprediktion().get(0).getDiagnosprediktionstatus().value();
//                predictionDiagnosisCode = Optional.ofNullable(underlag.getPrediktion().getDiagnosprediktion().get(0).getDiagnos())
//                        .map(CVType::getCode)
//                        .orElse(null);
                predictionProbabilityOverLimit = underlag.getPrediktion().getDiagnosprediktion().get(0).getSannolikhetOvergransvarde();

                if (underlag.getPrediktion().getDiagnosprediktion().get(0).getPrediktionsfaktorer() != null) {
                    prediktionsFragorSvar = underlag.getPrediktion().getDiagnosprediktion().get(0)
                            .getPrediktionsfaktorer().getFragasvar().stream()
                            .map((fs) -> SrsQuestionResponse.create(fs.getFrageidSrs(), fs.getSvarsidSrs()))
                            .collect(Collectors.toList());
                }
                if (underlag.getPrediktion().getDiagnosprediktion().get(0).getLakarbedomningRisk() != null) {
                    prediktionLakarbedomningRisk = underlag.getPrediktion().getDiagnosprediktion().get(0).getLakarbedomningRisk().value();
                }
                prediktionBerakningstidpunkt = underlag.getPrediktion().getDiagnosprediktion().get(0).getBerakningstidpunkt();
            }
        }

        if (filter.isAtgardsrekommendation()) {
            atgarderDiagnosisCode = underlag.getAtgardsrekommendationer().getRekommendation().stream()
                    .map(Atgardsrekommendation::getDiagnos)
                    .filter(Objects::nonNull)
                    .map(CVType::getCode)
                    .findAny().orElse(null);
            Map<Atgardstyp, List<Atgard>> tmp = underlag
                    .getAtgardsrekommendationer().getRekommendation().stream()
                    .flatMap(a -> a.getAtgard().stream())
                    .collect(Collectors.groupingBy(Atgard::getAtgardstyp));
            if (tmp.containsKey(Atgardstyp.OBS)) {
                atgarderObs = tmp.get(Atgardstyp.OBS).stream()
                        .sorted(Comparator.comparing(Atgard::getPrioritet))
                        .map((atgard) -> SrsRecommendation.create(atgard.getAtgardsforslag()))
                        .collect(Collectors.toList());
            } else {
                atgarderObs = Collections.emptyList();
            }

            if (tmp.containsKey(Atgardstyp.REK)) {
                atgarderRek = tmp.get(Atgardstyp.REK).stream()
                        .sorted(Comparator.comparing(Atgard::getPrioritet))
                        .map((atgard) -> SrsRecommendation.create(atgard.getAtgardsforslag()))
                        .collect(Collectors.toList());
            } else {
                atgarderRek = Collections.emptyList();
            }

            // They are all for the same diagnosis and all have the same code.
            atgarderStatusCode = underlag.getAtgardsrekommendationer().getRekommendation().stream()
                    .map(Atgardsrekommendation::getAtgardsrekommendationstatus)
                    .map(Atgardsrekommendationstatus::toString)
                    .findAny()
                    .orElse(null);
        }

        if (filter.isStatistik() && underlag.getStatistik() != null
                && !CollectionUtils.isEmpty(underlag.getStatistik().getStatistikbild())) {
            statistikDiagnosisCode = Optional.ofNullable(underlag.getStatistik().getStatistikbild().get(0).getDiagnos())
                    .map(CVType::getCode)
                    .orElse(null);
            statistikBild = underlag.getStatistik().getStatistikbild().get(0).getBildadress();
            statistikStatusCode = underlag.getStatistik().getStatistikbild().get(0).getStatistikstatus().toString();
            statistikNationellStatistik =
                    underlag.getStatistik().getStatistikbild().get(0).getData().stream()
                            .map((d) -> d.getIndividerAckumulerat().intValue()).collect(Collectors.toList());
        }
        return new SrsResponse(level, description, atgarderObs, atgarderRek, statistikBild, predictionDiagnosisCode,
                prediktionStatusCode, prediktionsFragorSvar, prediktionLakarbedomningRisk, prediktionBerakningstidpunkt,
                atgarderDiagnosisCode, atgarderStatusCode, statistikDiagnosisCode,
                statistikStatusCode, predictionProbabilityOverLimit, predictionPrevalence, statistikNationellStatistik);
    }

    @Override
    public List<SrsQuestion> getQuestions(String diagnos) {
        GetPredictionQuestionsRequestType request = new GetPredictionQuestionsRequestType();
        request.setDiagnos(createDiagnos(diagnos));
        GetPredictionQuestionsResponseType response = getPrediction.getPredictionQuestions(request);
        return response.getPrediktionsfraga().stream()
                .map(SrsQuestion::convert)
                .sorted(Comparator.comparing(SrsQuestion::getPriority))
                .collect(Collectors.toList());
    }

    @Override
    public Samtyckesstatus getConsent(String careUnitHsaId, Personnummer personId) throws InvalidPersonNummerException {
        GetConsentResponseType response = getConsent.getConsent(createGetConsentRequest(careUnitHsaId, personId));
        return response.getSamtyckesstatus();
    }

    @Override
    public ResultCodeEnum setConsent(String careUnitHsaId, Personnummer personId, boolean samtycke) throws InvalidPersonNummerException {
        SetConsentResponseType resp = setConsent.setConsent(createSetConsentRequest(careUnitHsaId, personId, samtycke));
        return resp.getResultCode();
    }

    @Override
    public EgenBedomningRiskType getOwnOpinion(String careGiverHsaId, String careUnitHsaId, String certificateId, String diagnosisCode) {
        GetOwnOpinionResponseType resp =
                getOwnOpinion.getOwnOpinion(createGetOwnOpinionRequest(careGiverHsaId, careUnitHsaId, certificateId, diagnosisCode));
        return resp.getEgenBedomningRisk();
    }

    @Override
    public ResultCodeEnum setOwnOpinion(String careGiverHsaId, String careUnitHsaId, String certificateId, String diagnosisCode,
                                        EgenBedomningRiskType ownOpinion) {
        SetOwnOpinionResponseType resp =
                setOwnOpinion.setOwnOpinion(createSetOwnOpinionRequest(careGiverHsaId, careUnitHsaId,
                        certificateId, diagnosisCode, ownOpinion));
        return resp.getResultCode();
    }

    @Override
    public List<String> getAllDiagnosisCodes() {
        GetDiagnosisCodesResponseType response = getDiagnosisCodes.getDiagnosisCodes(new GetDiagnosisCodesRequestType());
        return response.getDiagnos().stream()
                .map(CVType::getCode)
                .collect(Collectors.toList());
    }

    @Override
    public SrsForDiagnosisResponse getSrsForDiagnose(String diagnosCode) {

        if (diagnosCode == null || diagnosCode.isEmpty()) {
            throw new IllegalArgumentException("diagnosCode is required to construct a valid request.");
        }

        GetSRSInformationForDiagnosisRequestType request = new GetSRSInformationForDiagnosisRequestType();
        request.setDiagnos(createDiagnos(diagnosCode));
        GetSRSInformationForDiagnosisResponseType response = getSRSInformationForDiagnosis.getSRSInformationForDiagnosis(request);

        if (response.getResultCode() != ResultCodeEnum.OK) {
            throw new IllegalArgumentException("Bad data from SRS");
        }

        return createDiagnoseResponse(response);

    }

    private SrsForDiagnosisResponse createDiagnoseResponse(GetSRSInformationForDiagnosisResponseType response) {
        String resultDiagnosCode = null;

        // We need a null-check here in case there were no info available for the requested diagnosis code.
        if (hasAtgardsrekommendationWithDiagnosisCode(response)) {
            resultDiagnosCode = response.getAtgardsrekommendation().getDiagnos().getCode();
        }

        String atgarderStatusCode;
        String statistikStatusCode;
        String statistikBild;
        String statistikDiagnosCode;

        // Ugh. maybe we should create common xsd types for these common subtypes...
        final Atgardsrekommendation atgardsrekommendation = response
                .getAtgardsrekommendation();

        // filter out all OBS types sorted by priority
        final List<String> atgarderObs = atgardsrekommendation.getAtgard().stream().sorted(Comparator
                .comparing(Atgard::getPrioritet))
                .filter(a -> a.getAtgardstyp()
                        .equals(Atgardstyp.OBS))
                .map(Atgard::getAtgardsforslag)
                .collect(Collectors.toList());

        // filter out all REK types sorted by priority
        final List<String> atgarderRek = atgardsrekommendation.getAtgard().stream().sorted(Comparator
                .comparing(Atgard::getPrioritet))
                .filter(a -> a.getAtgardstyp()
                        .equals(Atgardstyp.REK))
                .map(Atgard::getAtgardsforslag)
                .collect(Collectors.toList());

        atgarderStatusCode = atgardsrekommendation.getAtgardsrekommendationstatus().name();

        if (response.getStatistik() != null
                && !CollectionUtils.isEmpty(response.getStatistik().getStatistikbild())) {
            final Statistikbild statistikbild = response.getStatistik().getStatistikbild().get(0);
            statistikStatusCode = statistikbild.getStatistikstatus().name();
            statistikBild = statistikbild.getBildadress();
            statistikDiagnosCode = statistikbild.getDiagnos() != null ? statistikbild.getDiagnos().getCode() : null;
        } else {
            statistikStatusCode = Statistikstatus.STATISTIK_SAKNAS.name();
            statistikBild = null;
            statistikDiagnosCode = null;
        }

        return new SrsForDiagnosisResponse(atgarderObs, atgarderRek,
                resultDiagnosCode, atgarderStatusCode, statistikBild, statistikStatusCode, statistikDiagnosCode);
    }

    private boolean hasAtgardsrekommendationWithDiagnosisCode(GetSRSInformationForDiagnosisResponseType response) {
        return response.getAtgardsrekommendation().getAtgardsrekommendationstatus() != Atgardsrekommendationstatus.INFORMATION_SAKNAS
                && response.getAtgardsrekommendation().getDiagnos() != null;
    }

    private GetSRSInformationRequestType createRequest(IntygUser user, String intygId, Personnummer personnummer, String diagnosisCode,
            Utdatafilter filter, List<SrsQuestionResponse> questions)
            throws InvalidPersonNummerException {

        GetSRSInformationRequestType request = new GetSRSInformationRequestType();
        request.setVersion("2.0");
        request.setKonsumentId(createHsaId(CONSUMER_HSA_ID));

        request.setAnvandareId(createHsaId(user.getHsaId()));

        Prediktionsfaktorer faktorer = new Prediktionsfaktorer();
        faktorer.setPostnummer(getPostnummer(user));
        faktorer.getFragasvar().addAll(questions.stream().map(SrsQuestionResponse::convert).collect(Collectors.toList()));
        request.setPrediktionsfaktorer(faktorer);

        Individfaktorer individer = new Individfaktorer();
        Individ individ = new Individ();
        individ.getDiagnos().add(createDiagnos(diagnosisCode));
        individ.setPersonId(personnummer.getPersonnummer());
        IntygId intyg = new IntygId();
        intyg.setExtension(intygId);
        intyg.setRoot(user.getValdVardenhet().getId());
        individ.setIntygId(intyg);
        individer.getIndivid().add(individ);

        request.setUtdatafilter(filter);

        request.setIndivider(individer);
        return request;
    }

    private String getPostnummer(IntygUser user) {
        String postnummer;
        if (user.getValdVardenhet() instanceof Vardenhet) {
            postnummer = ((Vardenhet) user.getValdVardenhet()).getPostnummer();
        } else {
            return ""; // What is default?
        }

        if (Strings.isNullOrEmpty(postnummer)) {
            return ""; // What is default?
        }
        String trimmed = postnummer.replace(" ", "");
        if (trimmed.length() != POSTNUMMER_LENGTH) {
            return ""; // What is default?

        }
        return trimmed;
    }

    private HsaId createHsaId(String hsaIdCode) {
        HsaId hsaId = new HsaId();
        hsaId.setRoot(HSA_ROOT);
        hsaId.setExtension(hsaIdCode);
        return hsaId;
    }

    private SetOwnOpinionRequestType createSetOwnOpinionRequest(String careGiverHsaId, String careUnitHsaId,
                                                                String certificateId, String diagnosis, EgenBedomningRiskType opinion) {
        SetOwnOpinionRequestType request = new SetOwnOpinionRequestType();
        request.setVardgivareId(createHsaId(careGiverHsaId));
        request.setVardenhetId(createHsaId(careUnitHsaId));
        request.setDiagnos(createDiagnos(diagnosis));
        IntygId intyg = new IntygId();
        intyg.setExtension(certificateId);
        intyg.setRoot(careUnitHsaId);
        request.setIntygId(intyg);
        request.setEgenBedomningRisk(opinion);
        return request;
    }

    private GetOwnOpinionRequestType createGetOwnOpinionRequest(String careGiverHsaId, String careUnitHsaId, String certificateId,
                                                                String diagnosis) {
        GetOwnOpinionRequestType request = new GetOwnOpinionRequestType();
        request.setVardgivareId(createHsaId(careGiverHsaId));
        request.setVardenhetId(createHsaId(careUnitHsaId));
        request.setDiagnos(createDiagnos(diagnosis));
        IntygId intyg = new IntygId();
        intyg.setExtension(certificateId);
        intyg.setRoot(careUnitHsaId);
        request.setIntygId(intyg);
        return request;
    }

    private SetConsentRequestType createSetConsentRequest(String careUnitHsaId, Personnummer personId, boolean samtycke)
            throws InvalidPersonNummerException {
        SetConsentRequestType request = new SetConsentRequestType();
        HsaId hsaId = createHsaId(careUnitHsaId);
        request.setVardenhetId(hsaId);
        request.setPersonId(personId.getPersonnummer());
        request.setSamtycke(samtycke);
        return request;
    }

    private GetConsentRequestType createGetConsentRequest(String careUnitHsaId, Personnummer personnummer)
            throws InvalidPersonNummerException {
        GetConsentRequestType request = new GetConsentRequestType();
        HsaId hsaId = new HsaId();
        hsaId.setExtension(careUnitHsaId);
        hsaId.setRoot(HSA_ROOT);
        request.setVardenhetId(hsaId);
        request.setPersonId(personnummer.getPersonnummer());
        return request;
    }

    private Diagnos createDiagnos(String diagnosisCode) {
        Diagnos diagnos = new Diagnos();
        diagnos.setCode(diagnosisCode);
        diagnos.setCodeSystem(DIAGNOS_CODE_SYSTEM);
        return diagnos;
    }
}
