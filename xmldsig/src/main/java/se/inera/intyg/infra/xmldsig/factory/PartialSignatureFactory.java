/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

public final class PartialSignatureFactory {

    private static final String SIGNATURE_ALGORITHM = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String TRANSFORM_ALGORITHM = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";


    private PartialSignatureFactory() {

    }

    public static SignatureType buildSignature() {
        SignatureType signature = new SignatureType();
        SignedInfoType signedInfo = new SignedInfoType();

        CanonicalizationMethodType canonType = new CanonicalizationMethodType();
        canonType.setAlgorithm(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS);
        signedInfo.setCanonicalizationMethod(canonType);

        SignatureMethodType signatureMethod = new SignatureMethodType();
        signatureMethod.setAlgorithm(SIGNATURE_ALGORITHM);
        signedInfo.setSignatureMethod(signatureMethod);

        ReferenceType referenceType = new ReferenceType();
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm(DigestMethod.SHA256);
        referenceType.setDigestMethod(digestMethodType);
        referenceType.setURI("");

        TransformType transform = new TransformType();
        transform.setAlgorithm(TRANSFORM_ALGORITHM);
        TransformsType tranforms = new TransformsType();
        tranforms.getTransform().add(transform);
        referenceType.setTransforms(tranforms);

        signedInfo.getReference().add(referenceType);
        signature.setSignedInfo(signedInfo);

        SignatureValueType signatureValue = new SignatureValueType();
        signature.setSignatureValue(signatureValue);
        return signature;
    }
}
