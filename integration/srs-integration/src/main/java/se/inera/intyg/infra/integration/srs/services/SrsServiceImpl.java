package se.inera.intyg.infra.integration.srs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Atgard;
import se.inera.intyg.clinicalprocess.healthcond.srs.getsrsinformation.v1.Bedomningsunderlag;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.Diagnos;
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

    private static final int FOUR = 4;
    private static final int THREE = 3;
    @Autowired
    private GetSRSInformationResponderInterface getSRSInformationResponderInterface;

    private static final Logger LOG = LoggerFactory.getLogger(SrsServiceImpl.class);

    @Override
    public SrsResponse getSrs(Personnummer personnummer, String diagnosisCode, Utdatafilter filter) throws
            InvalidPersonNummerException, SrsException {
        GetSRSInformationResponseType response = getSRSInformationResponderInterface
                .getSRSInformation(createRequest(personnummer, diagnosisCode, filter));
        if (response.getResultCode() != ResultCodeEnum.OK || response.getBedomningsunderlag().isEmpty()) {
            throw new IllegalArgumentException("Bad data from SRS");
        }

        Bedomningsunderlag underlag = response.getBedomningsunderlag().get(0);

        Integer level = null;
        String statistikBild = null;
        List<String> atgarder = null;

        if (filter.isPrediktion()) {
            if (underlag.getPrediktion().getDiagnosprediktion().get(0).getDiagnosprediktionstatus()
                    == Diagnosprediktionstatus.PREDIKTIONSMODELL_SAKNAS) {
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

        if (filter.isStatistik()) {
            statistikBild = underlag.getStatistik().getStatistikbild().get(0).getBildadress();
        }

        if (filter.isFmbinformation()) {
            // Handle fmbInformation here
            LOG.info("FMB info");

        }

        return new SrsResponse(level, atgarder, statistikBild);
    }

    private GetSRSInformationRequestType createRequest(Personnummer personnummer, String diagnosisCode, Utdatafilter filter)
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

        request.setUtdatafilter(filter);

        request.setIndivider(individer);
        return request;
    }
}
