package se.inera.intyg.infra.xmldsig.factory;

import se.inera.intyg.infra.xmldsig.model.CanonicalizationMethodType;
import se.inera.intyg.infra.xmldsig.model.DigestMethodType;
import se.inera.intyg.infra.xmldsig.model.ReferenceType;
import se.inera.intyg.infra.xmldsig.model.SignatureMethodType;
import se.inera.intyg.infra.xmldsig.model.SignatureType;
import se.inera.intyg.infra.xmldsig.model.SignatureValueType;
import se.inera.intyg.infra.xmldsig.model.SignedInfoType;
import se.inera.intyg.infra.xmldsig.model.TransformType;
import se.inera.intyg.infra.xmldsig.model.TransformsType;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;

public class PartialSignatureFactory {

    public static SignatureType buildSignature() {
        SignatureType signature = new SignatureType();
        SignedInfoType signedInfo = new SignedInfoType();

        CanonicalizationMethodType canonType = new CanonicalizationMethodType();
        canonType.setAlgorithm(CanonicalizationMethod.EXCLUSIVE);
        signedInfo.setCanonicalizationMethod(canonType);

        SignatureMethodType signatureMethod = new SignatureMethodType();
        signatureMethod.setAlgorithm("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        signedInfo.setSignatureMethod(signatureMethod);

        ReferenceType referenceType = new ReferenceType();
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm(DigestMethod.SHA256);
        referenceType.setDigestMethod(digestMethodType);

        referenceType.setURI("");
        TransformType transform = new TransformType();
        transform.setAlgorithm("http://www.w3.org/2000/09/xmldsig#enveloped-signature");

        TransformsType tranforms = new TransformsType();
        tranforms.getTransform().add(transform);
        referenceType.setTransforms(tranforms);
        // referenceType.setDigestValue(new String("dummy").getBytes(Charset.forName("UTF-8")));
        signedInfo.getReference().add(referenceType);

        signature.setSignedInfo(signedInfo);
        SignatureValueType signatureValue = new SignatureValueType();
        // signatureValue.setValue(new String("dummy").getBytes(Charset.forName("UTF-8")));
        signature.setSignatureValue(signatureValue);

        return signature;
    }
}
