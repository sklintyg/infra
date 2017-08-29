package se.inera.intyg.infra.integration.srs.stub;

import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.setconsent.v1.SetConsentResponseType;
import se.riv.clinicalprocess.healthcond.certificate.types.v2.ResultCodeEnum;

@SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH)
public class SetConsentStub implements SetConsentResponderInterface {
    private static final Logger LOG = LoggerFactory.getLogger(SetConsentStub.class);

    @Override
    public SetConsentResponseType setConsent(SetConsentRequestType setConsentRequestType) {
        LOG.info("Stub received SetConsent-request for {}.", setConsentRequestType.getPersonId());
        SetConsentResponseType response = new SetConsentResponseType();
        response.setResultCode(ResultCodeEnum.OK);
        return response;
    }
}
