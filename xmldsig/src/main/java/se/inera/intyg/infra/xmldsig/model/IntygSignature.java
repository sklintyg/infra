package se.inera.intyg.infra.xmldsig.model;

public interface IntygSignature {
    String getOriginalXml();
    String getSigningData();
}
