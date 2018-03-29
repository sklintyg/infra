package se.inera.intyg.infra.xmldsig;

public interface IntygSignature {
    String getOriginalXml();
    String getSigningData();
}
