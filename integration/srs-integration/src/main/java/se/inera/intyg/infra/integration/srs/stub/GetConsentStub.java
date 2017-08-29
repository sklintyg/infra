package se.inera.intyg.infra.integration.srs.stub;

import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;

import java.time.LocalDateTime;

@SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH)
public class GetConsentStub implements GetConsentResponderInterface {

    private final LocalDateTime sparatTid = LocalDateTime.of(2017, 1, 1, 1, 1);
    private static final Logger LOG = LoggerFactory.getLogger(GetConsentStub.class);

    @Override
    public GetConsentResponseType getConsent(GetConsentRequestType getConsentRequestType) {
        LOG.info("Stub received GetConsent-request for {}.", getConsentRequestType.getPersonId());

        GetConsentResponseType response = new GetConsentResponseType();
        response.setSamtycke(true);
        response.setSamtyckesstatus(Samtyckesstatus.JA);
        response.setSparattidpunkt(sparatTid);
        return response;
    }
}
