package se.inera.intyg.infra.xmldsig;

import se.inera.intyg.infra.xmldsig.model.SignatureType;

public interface XMLDSigService {
    SignatureType prepareSignature(String intygXml);
}
