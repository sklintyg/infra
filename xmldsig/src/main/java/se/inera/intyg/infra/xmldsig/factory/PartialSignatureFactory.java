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

import org.springframework.core.io.ClassPathResource;
import org.w3._2000._09.xmldsig_.CanonicalizationMethodType;
import org.w3._2000._09.xmldsig_.DigestMethodType;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.ObjectFactory;
import org.w3._2000._09.xmldsig_.ReferenceType;
import org.w3._2000._09.xmldsig_.SignatureMethodType;
import org.w3._2000._09.xmldsig_.SignatureType;
import org.w3._2000._09.xmldsig_.SignatureValueType;
import org.w3._2000._09.xmldsig_.SignedInfoType;
import org.w3._2000._09.xmldsig_.TransformType;
import org.w3._2000._09.xmldsig_.TransformsType;
import org.w3._2000._09.xmldsig_.X509DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBElement;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Transform;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Base64;

public final class PartialSignatureFactory {

    private static final String SIGNATURE_ALGORITHM = "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256";
    private static final String TRANSFORM_ALGORITHM = "http://www.w3.org/2000/09/xmldsig#enveloped-signature";

    private PartialSignatureFactory() {

    }

    /**
     * Builds a partially populated {@link SignatureType}.
     *
     * Contains appropriate algorithms and elements for subsequent population of digest, signature value and
     * keyinfo.
     */
    public static SignatureType buildSignature() {
        SignatureType signature = new SignatureType();
        SignedInfoType signedInfo = new SignedInfoType();

        CanonicalizationMethodType canonType = new CanonicalizationMethodType();
        canonType.setAlgorithm(CanonicalizationMethod.EXCLUSIVE);
        signedInfo.setCanonicalizationMethod(canonType);

        SignatureMethodType signatureMethod = new SignatureMethodType();
        signatureMethod.setAlgorithm(SIGNATURE_ALGORITHM);
        signedInfo.setSignatureMethod(signatureMethod);

        ReferenceType referenceType = new ReferenceType();
        DigestMethodType digestMethodType = new DigestMethodType();
        digestMethodType.setAlgorithm(DigestMethod.SHA256);
        referenceType.setDigestMethod(digestMethodType);
        referenceType.setURI("");

        TransformType can = new TransformType();
        can.setAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");

        TransformType xslt1 = new TransformType();
        xslt1.setAlgorithm(Transform.XSLT);
        xslt1.getContent().add(loadXsltElement("stripnamespaces.xslt"));

        TransformType xslt2 = new TransformType();
        xslt2.setAlgorithm(Transform.XSLT);
        xslt2.getContent().add(loadXsltElement("stripmetadata.xslt"));

        TransformType xslt3 = new TransformType();
        xslt3.setAlgorithm(Transform.XSLT);
        xslt3.getContent().add(loadXsltElement("stripparentelement_2.xslt"));

        TransformType xslt4 = new TransformType();
        xslt4.setAlgorithm(Transform.XSLT);
        xslt4.getContent().add(loadXsltElement("stripnamespaces_2.xslt"));


        TransformType enveloped = new TransformType();
        enveloped.setAlgorithm(TRANSFORM_ALGORITHM);

        TransformsType tranforms = new TransformsType();

        // The order here IS significant!! Otherwise, validation will not produce the expected digest.
        tranforms.getTransform().add(enveloped);     // Having enveloped makes sure the <Signature> element is removed when digesting.

        tranforms.getTransform().add(xslt1);
        // tranforms.getTransform().add(xslt4);
        tranforms.getTransform().add(xslt2);
         tranforms.getTransform().add(xslt3);
     //   tranforms.getTransform().add(xslt4);

        tranforms.getTransform().add(can);           // Canonicalization makes sure tags are not self-closed etc.

        referenceType.setTransforms(tranforms);

        signedInfo.getReference().add(referenceType);
        signature.setSignedInfo(signedInfo);

        SignatureValueType signatureValue = new SignatureValueType();
        signature.setSignatureValue(signatureValue);
        return signature;
    }

    /**
     * Builds a {@link KeyInfoType} element with the supplied certificate added into a X509Data->X509Certificate element.
     *
     * @param certificate
     *            Base64-encoded string of a x509 certificate.
     * @return
     *         A KeyInfoType object.
     */
    public static KeyInfoType buildKeyInfo(String certificate) {
        KeyInfoType keyInfo = new KeyInfoType();

        ObjectFactory objectFactory = new ObjectFactory();
        X509DataType x509DataType = objectFactory.createX509DataType();
        JAXBElement<byte[]> x509cert = objectFactory.createX509DataTypeX509Certificate(Base64.getDecoder().decode(certificate));
        x509DataType.getX509IssuerSerialOrX509SKIOrX509SubjectName().add(x509cert);
        keyInfo.getContent().add(objectFactory.createX509Data(x509DataType));
        return keyInfo;
    }

    private static Element loadXsltElement(String path) {

        ClassPathResource cpr = new ClassPathResource(path);
        // Append the SignatureElement as last element of the xml.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            Document doc = dbf.newDocumentBuilder().parse(cpr.getInputStream());
            return doc.getDocumentElement();
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}
