package se.inera.intyg.infra.integration.srs.stub;

import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponseType;
import se.inera.intyg.infra.integration.srs.stub.repository.ConsentRepository;
import se.inera.intyg.schemas.contract.Personnummer;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

@SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH)
public class SetConsentStub implements SetConsentResponderInterface {
    private static final Logger LOG = LoggerFactory.getLogger(SetConsentStub.class);

    @Autowired
    private ConsentRepository consentRepository;

    @Override
    public SetConsentResponseType setConsent(SetConsentRequestType setConsentRequestType) {
        LOG.info("Stub received SetConsent-request for {}.", setConsentRequestType.getPersonId());

        consentRepository
                .setConsent(new Personnummer(setConsentRequestType.getPersonId()), setConsentRequestType.getVardgivareId().getExtension(),
                        setConsentRequestType.isSamtycke());
        SetConsentResponseType response = new SetConsentResponseType();
        response.setResultCode(ResultCodeEnum.OK);
        return response;
    }
}
