package se.inera.intyg.infra.integration.srs.services;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getdiagnosiscodes.v1.GetDiagnosisCodesResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgardstyp;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Bedomningsunderlag;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Individ;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Individfaktorer;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Prediktionsfaktorer;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponseType;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.srs.model.SrsException;
import se.inera.intyg.infra.integration.srs.model.SrsQuestion;
import se.inera.intyg.infra.integration.srs.model.SrsQuestionResponse;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.infra.security.common.model.IntygUser;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.CVType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Diagnos;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SrsServiceImpl implements SrsService {

    private static final int FOUR = 4;
    private static final int THREE = 3;
    private static final int POSTNUMMER_LENGTH = 5;

    private static final String HSA_ROOT = "1.2.752.129.2.1.4.1";
    private static final String CONSUMER_HSA_ID = "SE5565594230-B31";
    private static final String DIAGNOS_CODE_SYSTEM = "1.2.752.116.1.1.1.1.3";

    private static final Logger LOG = LoggerFactory.getLogger(SrsServiceImpl.class);
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

    @Override
    public SrsResponse getSrs(IntygUser user, String intygId, Personnummer personnummer, String diagnosisCode, Utdatafilter filter,
            List<SrsQuestionResponse> questions) throws InvalidPersonNummerException, SrsException {

        GetSRSInformationResponseType response = getSRSInformation.getSRSInformation(
                createRequest(user, intygId, personnummer, diagnosisCode, filter, questions));

        if (response.getResultCode() != ResultCodeEnum.OK) {
            throw new IllegalArgumentException("Bad data from SRS");
        }

        // Schema mandates that this is of 1..*
        Bedomningsunderlag underlag = response.getBedomningsunderlag().get(0);

        Integer level = null;
        String description = null;
        String statistikBild = null;
        List<String> atgarderObs = null;
        List<String> atgarderRek = null;

        if (filter.isPrediktion()) {
            if (underlag.getPrediktion().getDiagnosprediktion().isEmpty()
                    || underlag.getPrediktion().getDiagnosprediktion().get(0).getDiagnosprediktionstatus()
                    == Diagnosprediktionstatus.PREDIKTIONSMODELL_SAKNAS) {
                throw new SrsException("Prediktionsmodell saknas");
            }
            level = underlag.getPrediktion().getDiagnosprediktion().get(0).getRisksignal().getRiskkategori().intValueExact();
            description = underlag.getPrediktion().getDiagnosprediktion().get(0).getRisksignal().getBeskrivning();
        }

        if (filter.isAtgardsrekommendation()) {
            Map<Atgardstyp, List<Atgard>> tmp = underlag
                    .getAtgardsrekommendationer().getRekommendation().stream()
                    .flatMap(a -> a.getAtgard().stream())
                    .collect(Collectors.groupingBy(Atgard::getAtgardstyp));

            if (tmp.containsKey(Atgardstyp.OBS)) {
                atgarderObs = tmp.get(Atgardstyp.OBS).stream()
                        .sorted(Comparator.comparing(Atgard::getPrioritet))
                        .map(Atgard::getAtgardsforslag)
                        .collect(Collectors.toList());
            } else {
                atgarderObs = Collections.emptyList();
            }

            if (tmp.containsKey(Atgardstyp.REK)) {
                atgarderRek = tmp.get(Atgardstyp.REK).stream()
                        .sorted(Comparator.comparing(Atgard::getPrioritet))
                        .map(Atgard::getAtgardsforslag)
                        .collect(Collectors.toList());
            } else {
                atgarderRek = Collections.emptyList();
            }
        }

        if (filter.isStatistik() && underlag.getStatistik() != null
                && !CollectionUtils.isEmpty(underlag.getStatistik().getStatistikbild())) {
            statistikBild = underlag.getStatistik().getStatistikbild().get(0).getBildadress();
        }

        return new SrsResponse(level, description, atgarderObs, atgarderRek, statistikBild);
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
    public Samtyckesstatus getConsent(String hsaId, Personnummer personId) throws InvalidPersonNummerException {
        GetConsentResponseType response = getConsent.getConsent(createGetConsentRequest(hsaId, personId));
        return response.getSamtyckesstatus();
    }

    @Override
    public ResultCodeEnum setConsent(String hsaId, Personnummer personId, boolean samtycke) throws InvalidPersonNummerException {
        SetConsentResponseType resp = setConsent.setConsent(createSetConsentRequest(hsaId, personId, samtycke));
        return resp.getResultCode();
    }

    @Override
    public List<String> getAllDiagnosisCodes() {
        GetDiagnosisCodesResponseType response = getDiagnosisCodes.getDiagnosisCodes(new GetDiagnosisCodesRequestType());
        return response.getDiagnos().stream()
                .map(CVType::getCode)
                .collect(Collectors.toList());
    }

    private GetSRSInformationRequestType createRequest(IntygUser user, String intygId, Personnummer personnummer, String diagnosisCode,
            Utdatafilter filter, List<SrsQuestionResponse> questions)
            throws InvalidPersonNummerException {

        GetSRSInformationRequestType request = new GetSRSInformationRequestType();
        request.setVersion("1.0");
        request.setKonsumentId(createHsaId(CONSUMER_HSA_ID));

        request.setAnvandareId(createHsaId(user.getHsaId()));

        Prediktionsfaktorer faktorer = new Prediktionsfaktorer();
        faktorer.setPostnummer(getPostnummer(user));
        faktorer.getFragasvar().addAll(questions.stream().map(SrsQuestionResponse::convert).collect(Collectors.toList()));
        request.setPrediktionsfaktorer(faktorer);

        Individfaktorer individer = new Individfaktorer();
        Individ individ = new Individ();
        individ.getDiagnos().add(createDiagnos(diagnosisCode));
        individ.setPersonId(personnummer.getNormalizedPnr());
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

    private SetConsentRequestType createSetConsentRequest(String hsaString, Personnummer personId, boolean samtycke)
            throws InvalidPersonNummerException {
        SetConsentRequestType request = new SetConsentRequestType();
        HsaId hsaId = createHsaId(hsaString);
        request.setVardgivareId(hsaId);
        request.setPersonId(personId.getNormalizedPnr());
        request.setSamtycke(samtycke);
        return request;
    }

    private GetConsentRequestType createGetConsentRequest(String hsaString, Personnummer personnummer)
            throws InvalidPersonNummerException {
        GetConsentRequestType request = new GetConsentRequestType();
        HsaId hsaId = new HsaId();
        hsaId.setExtension(hsaString);
        hsaId.setRoot(HSA_ROOT);
        request.setVardgivareId(hsaId);
        request.setPersonId(personnummer.getNormalizedPnr());
        return request;
    }

    private Diagnos createDiagnos(String diagnosisCode) {
        Diagnos diagnos = new Diagnos();
        diagnos.setCode(diagnosisCode);
        diagnos.setCodeSystem(DIAGNOS_CODE_SYSTEM);
        return diagnos;
    }
}
