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
package se.inera.intyg.infra.xmldsig.service;

import org.apache.commons.io.IOUtils;
import org.apache.xml.security.c14n.Canonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import se.inera.intyg.infra.xmldsig.factory.PartialSignatureFactory;
import se.inera.intyg.infra.xmldsig.util.X509KeySelector;

import javax.annotation.PostConstruct;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.nio.charset.Charset;
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
    private static final String CANONICALIZER_ALGORITHM = CanonicalizationMethod.EXCLUSIVE;
    private static final String DIGEST_ALGORITHM = "SHA-256";

    @PostConstruct
    public void init() {
        org.apache.xml.security.Init.init();
    }

    /**
     * Builds a <KeyInfo/> element with the supplied certificate put into a child X509Certificate element.
     */
    @Override
    public KeyInfoType buildKeyInfoForCertificate(String certificate) {
        return PartialSignatureFactory.buildKeyInfo(certificate);
    }

    @Override
    public boolean validateSignatureValidity(String xmlWithSignedIntyg, boolean checkReferences) {
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            Document doc = dbf.newDocumentBuilder().parse(IOUtils.toInputStream(xmlWithSignedIntyg, Charset.forName("UTF-8")));
            NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                throw new Exception("Cannot find Signature element");
            }
            boolean signatureOk = false;
            for (int index = 0; index < nl.getLength(); index++) {
                signatureOk = verifySignature(checkReferences, fac, nl.item(index));
                if (!signatureOk) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("Caught {} validating signature. Msg: {}", e.getClass().getName(), e.getMessage());
        }
        return false;
    }

    private boolean verifySignature(boolean checkReferences, XMLSignatureFactory fac, Node node)
            throws MarshalException, XMLSignatureException {
        // Create a DOMValidateContext and specify a KeySelector
        // and document context.
        DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), node);

        // Unmarshal the XMLSignature.
        XMLSignature sig = fac.unmarshalXMLSignature(valContext);

        if (checkReferences) {
            return sig.validate(valContext);
        } else {
            return sig.getSignatureValue().validate(valContext);
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
