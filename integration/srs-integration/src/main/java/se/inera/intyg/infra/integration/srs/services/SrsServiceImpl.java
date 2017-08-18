package se.inera.intyg.infra.integration.srs.services;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Bedomningsunderlag;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnos;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Diagnosprediktionstatus;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.GetSRSInformationResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Individ;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Individfaktorer;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Omfattning;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Utdatafilter;
import se.inera.intyg.infra.integration.srs.model.SrsException;
import se.inera.intyg.infra.integration.srs.model.SrsResponse;
import se.inera.intyg.schemas.contract.InvalidPersonNummerException;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.HsaId;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

import java.util.List;
import java.util.stream.Collectors;

public class SrsServiceImpl implements SrsService {

    @Autowired
    private GetSRSInformationResponderInterface getSRSInformationResponderInterface;

    @Override
    public SrsResponse getSrs(Personnummer personnummer, String diagnosisCode) throws InvalidPersonNummerException, SrsException {
        GetSRSInformationResponseType response = getSRSInformationResponderInterface
                .getSRSInformation(createRequest(personnummer, diagnosisCode));
        if (response.getResultCode() != ResultCodeEnum.OK || response.getBedomningsunderlag().isEmpty()
                || response.getBedomningsunderlag().get(0).getPrediktion() == null || response.getBedomningsunderlag().get(0)
                .getPrediktion().getDiagnosprediktion().isEmpty()) {
            throw new IllegalArgumentException("Bad data from SRS");
        } else if (response.getBedomningsunderlag().get(0).getPrediktion().getDiagnosprediktion().get(0).getDiagnosprediktionstatus()
                == Diagnosprediktionstatus.PREDIKTIONSMODELL_SAKNAS) {
            throw new SrsException("Prediktionsmodell saknas");
        }
        Bedomningsunderlag underlag = response.getBedomningsunderlag().get(0);
        int level = Math.min((int) (underlag.getPrediktion().getDiagnosprediktion().get(0).getSannolikhetLangvarig() * 4), 3);
        List<String> atgarder = underlag.getAtgardsrekommendationer().getRekommendation().stream()
                .flatMap(a -> a.getAtgard().stream())
                .map(Atgard::getAtgardsforslag)
                .collect(Collectors.toList());
        return new SrsResponse(level, atgarder);
    }

    private GetSRSInformationRequestType createRequest(Personnummer personnummer, String diagnosisCode)
            throws InvalidPersonNummerException {
        GetSRSInformationRequestType request = new GetSRSInformationRequestType();
        HsaId hsaId = new HsaId();
        hsaId.setExtension("SE5565594230-B31");
        request.setKonsumentId(hsaId);

        Individfaktorer individer = new Individfaktorer();
        Individ individ = new Individ();
        individ.setOmfattning(Omfattning.HELT_NEDSATT);
        Diagnos diagnos = new Diagnos();
        diagnos.setCode(diagnosisCode);
        diagnos.setCodeSystem("1.2.752.116.1.1.1.1.3");
        individ.getDiagnos().add(diagnos);
        individ.setPersonId(personnummer.getNormalizedPnr());
        individer.getIndivid().add(individ);

        // We are currently only interested in recommendations and prediction
        Utdatafilter utdatafilter = new Utdatafilter();
        utdatafilter.setAtgardsrekommendation(true);
        utdatafilter.setFmbinformation(false);
        utdatafilter.setPrediktion(true);
        utdatafilter.setStatistik(false);
        request.setUtdatafilter(utdatafilter);

        request.setIndivider(individer);
        return request;
    }
}
