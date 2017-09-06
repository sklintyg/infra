package se.inera.intyg.infra.integration.srs.stub;

import org.apache.cxf.annotations.SchemaValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentRequestType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponderInterface;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.GetConsentResponseType;
import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;
import se.inera.intyg.infra.integration.srs.stub.repository.ConsentRepository;
import se.inera.intyg.infra.integration.srs.stub.repository.model.Consent;
import se.inera.intyg.schemas.contract.Personnummer;

import java.util.Optional;

@SchemaValidation(type = SchemaValidation.SchemaValidationType.BOTH)
public class GetConsentStub implements GetConsentResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(GetConsentStub.class);

    @Autowired
    private ConsentRepository consentRepository;

    @Override
    public GetConsentResponseType getConsent(GetConsentRequestType getConsentRequestType) {
        LOG.info("Stub received GetConsent-request for {}.", getConsentRequestType.getPersonId());

        Optional<Consent> consent = consentRepository
                .getConsent(new Personnummer(getConsentRequestType.getPersonId()), getConsentRequestType.getVardgivareId().getExtension());
        GetConsentResponseType response = new GetConsentResponseType();
        if (consent.isPresent()) {
            response.setSamtycke(true);
            response.setSamtyckesstatus(Samtyckesstatus.JA);
            response.setSparattidpunkt(consent.get().getTimestamp());
        } else {
            response.setSamtyckesstatus(Samtyckesstatus.INGET);
        }
        return response;
    }
}
