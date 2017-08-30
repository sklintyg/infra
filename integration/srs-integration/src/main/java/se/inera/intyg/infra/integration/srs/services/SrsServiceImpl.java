package se.inera.intyg.infra.integration.srs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getpredictionquestions.v1.GetPredictionQuestionsResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Bedomningsunderlag;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Individ;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Individfaktorer;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponseType;
import se.inera.intyg.infra.integration.srs.model.SjukskrivningsGrad;
import se.inera.intyg.infra.integration.srs.model.SrsConsentResponse;
import se.inera.intyg.infra.integration.srs.model.SrsException;
import se.inera.intyg.infra.integration.srs.model.SrsQuestion;
import se.inera.intyg.infra.integration.srs.model.SrsQuestionResponse;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Diagnos;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.IntygId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SrsServiceImpl implements SrsService {

    private static final int FOUR = 4;
    private static final int THREE = 3;

    private static final String HSA_ROOT = "1.2.752.129.2.1.4.1";
    private static final Logger LOG = LoggerFactory.getLogger(SrsServiceImpl.class);

    @Autowired
    private GetSRSInformationResponderInterface getSRSInformation;

    @Autowired
    private GetPredictionQuestionsResponderInterface getPrediction;

    @Autowired
    private GetConsentResponderInterface getConsent;

    @Autowired
    private SetConsentResponderInterface setConsent;

    @Override
    public SrsResponse getSrs(String intygId, Personnummer personnummer, String diagnosisCode, Utdatafilter filter,
            List<SrsQuestionResponse> questions, SjukskrivningsGrad sjukskrivningsGrad) throws InvalidPersonNummerException, SrsException {
        GetSRSInformationResponseType response = getSRSInformation
                .getSRSInformation(createRequest(intygId, personnummer, diagnosisCode, filter, sjukskrivningsGrad));
        if (response.getResultCode() != ResultCodeEnum.OK || response.getBedomningsunderlag().isEmpty()) {
            throw new IllegalArgumentException("Bad data from SRS");
        }

        Bedomningsunderlag underlag = response.getBedomningsunderlag().get(0);

        Integer level = null;
        String statistikBild = null;
        List<String> atgarder = null;

        if (filter.isPrediktion()) {
            if (underlag.getPrediktion().getDiagnosprediktion().get(0)
                    .getDiagnosprediktionstatus() == Diagnosprediktionstatus.PREDIKTIONSMODELL_SAKNAS) {
                throw new SrsException("Prediktionsmodell saknas");
            }
            level = Math.min((int) (underlag.getPrediktion().getDiagnosprediktion().get(0)
                    .getSannolikhetOvergransvarde() * FOUR), THREE);
        }

        if (filter.isAtgardsrekommendation()) {
            atgarder = underlag.getAtgardsrekommendationer().getRekommendation().stream()
                    .flatMap(a -> a.getAtgard().stream())
                    .map(Atgard::getAtgardsforslag)
                    .collect(Collectors.toList());
        }

        if (filter.isStatistik() && underlag.getStatistik() != null
                && !CollectionUtils.isEmpty(underlag.getStatistik().getStatistikbild())) {
            statistikBild = underlag.getStatistik().getStatistikbild().get(0).getBildadress();
        }

        if (filter.isFmbinformation()) {
            // Handle fmbInformation here
            LOG.info("FMB info");

        }

        return new SrsResponse(level, atgarder, statistikBild);
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
    public SrsConsentResponse getConsent(String hsaId, Personnummer personId) throws InvalidPersonNummerException {
        GetConsentResponseType response = getConsent.getConsent(createGetConsentRequest(hsaId, personId));
        return new SrsConsentResponse(response.getSamtyckesstatus(), response.isSamtycke(),
                response.getSparattidpunkt());
    }

    @Override
    public ResultCodeEnum setConsent(String hsaId, Personnummer personId, boolean samtycke) throws InvalidPersonNummerException {
        SetConsentResponseType resp = setConsent.setConsent(createSetConsentRequest(hsaId, personId, samtycke));
        return resp.getResultCode();
    }

    private GetSRSInformationRequestType createRequest(String intygId, Personnummer personnummer, String diagnosisCode, Utdatafilter filter,
            SjukskrivningsGrad sjukskrivningsGrad) throws InvalidPersonNummerException {
        GetSRSInformationRequestType request = new GetSRSInformationRequestType();
        HsaId hsaId = new HsaId();
        hsaId.setRoot("1.2.752.129.2.1.4.1");
        hsaId.setExtension("SE5565594230-B31");
        request.setKonsumentId(hsaId);
        Individfaktorer individer = new Individfaktorer();
        Individ individ = new Individ();
        individ.setOmfattning(sjukskrivningsGrad.toOmfattning());
        individ.getDiagnos().add(createDiagnos(diagnosisCode));
        individ.setPersonId(personnummer.getNormalizedPnr());
        IntygId intyg = new IntygId();
        intyg.setExtension(intygId);
        intyg.setRoot("SE5565594230-B31");
        individ.setIntygId(intyg);
        individer.getIndivid().add(individ);

        request.setUtdatafilter(filter);

        request.setIndivider(individer);
        return request;
    }

    private SetConsentRequestType createSetConsentRequest(String hsaString, Personnummer personId, boolean samtycke)
            throws InvalidPersonNummerException {
        SetConsentRequestType request = new SetConsentRequestType();
        HsaId hsaId = new HsaId();
        hsaId.setRoot(HSA_ROOT);
        hsaId.setExtension(hsaString);
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
        diagnos.setCodeSystem("1.2.752.116.1.1.1.1.3");
        return diagnos;
    }
}
