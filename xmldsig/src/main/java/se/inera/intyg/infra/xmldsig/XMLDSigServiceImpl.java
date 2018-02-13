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
package se.inera.intyg.infra.xmldsig;

import org.apache.commons.io.output.NullWriter;
import org.apache.xml.security.c14n.Canonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import se.inera.intyg.infra.xmldsig.factory.PartialSignatureFactory;
import se.inera.intyg.infra.xmldsig.model.KeyInfoType;
import se.inera.intyg.infra.xmldsig.model.SignatureType;

import javax.annotation.PostConstruct;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Provides Intyg-specific functionality for preparing XMLDSig signatures.
 *
 * @author eriklupander
 */
@Service
public class XMLDSigServiceImpl implements XMLDSigService {

    private static final Logger LOG = LoggerFactory.getLogger(XMLDSigServiceImpl.class);
    private static final String CANONICALIZER_ALGORITHM = CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS;
    private static final String DIGEST_ALGORITHM = "SHA-256";

    @PostConstruct
    public void init() {
        org.apache.xml.security.Init.init();
    }

    /**
     * Prepares an XMLDSig signature.
     *
     * Given the supplied XML, the XML is canonicalized and a SHA-256 digest is created and Base64-encoded into the
     * DigestValue field.
     *
     * Also, relevant algorithms for digest, signature and canonicalization method are specified on the body of the
     * returned {@link SignatureType}.
     *
     * @param intygXml
     *            XML document to be canonicalized and digested.
     * @return
     *         Partially filled SignatureType, i.e. everything except SignatureValue and KeyInfo should be populated.
     */
    @Override
    public SignatureType prepareSignature(String intygXml) {
        SignatureType signatureType = PartialSignatureFactory.buildSignature();

        String canonicalizedXml = canonicalizeXml(intygXml);
        byte[] digest = generateDigest(canonicalizedXml);
        signatureType.getSignedInfo().getReference().get(0).setDigestValue(digest);
        validate(signatureType);
        return signatureType;
    }

    /**
     * Builds a <KeyInfo/> element with the supplied certificate put into a child X509Certificate element.
     */
    @Override
    public KeyInfoType buildKeyInfoForCertificate(String certificate) {
        return PartialSignatureFactory.buildKeyInfo(certificate);
    }

    /**
     * Loads the XMLDSig schema and validates our output SignatureType vs the schema.
     *
     * @param signatureType
     */
    @Override
    public void validate(SignatureType signatureType) {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            ClassPathResource classPathResource = new ClassPathResource("schemas/xmldsig.xsd");

            Schema schema = sf.newSchema(classPathResource.getFile());
            JAXBContext jc = JAXBContext.newInstance(SignatureType.class);

            Marshaller marshaller = jc.createMarshaller();
            marshaller.setSchema(schema);
            marshaller.setEventHandler(event -> {
                LOG.error("Error validating Signature element vs schema: {}", event.getMessage());
                return false;
            });
            marshaller.marshal(signatureType, new NullWriter());
        } catch (SAXException | JAXBException | IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * This method takes the canonalized string (or any string we want a digest for) and computes a SHA-256 hash of it.
     */
    private byte[] generateDigest(String canonXmlString) {
        try {
            MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] sha256 = digest.digest(canonXmlString.getBytes("UTF-8"));
            return java.util.Base64.getEncoder().encode(sha256);
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.error("{} caught during digest and base64-encoding, message: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String canonicalizeXml(String intygXml) {
        try {
            Canonicalizer canonicalizer = Canonicalizer.getInstance(CANONICALIZER_ALGORITHM);
            byte[] canonicalizedXmlAsBytes = canonicalizer.canonicalize(intygXml.getBytes("UTF-8"));
            return new String(canonicalizedXmlAsBytes, "UTF-8");
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + " caught canonicalizing intyg XML, message: " + e.getMessage());
            throw new IllegalArgumentException(e.getCause());
        }
    }
}
