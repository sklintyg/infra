package se.inera.intyg.infra.xmldsig;

public interface IntygSignature {
    String getCanonicalizedContent();
    String getSigningData();
}
