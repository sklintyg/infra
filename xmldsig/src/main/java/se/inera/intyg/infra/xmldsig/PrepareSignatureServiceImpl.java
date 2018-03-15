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

import org.apache.commons.io.IOUtils;
import org.apache.xml.security.c14n.Canonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import se.inera.intyg.infra.xmldsig.exception.IntygXMLDSigException;
import se.inera.intyg.infra.xmldsig.factory.PartialSignatureFactory;
import se.inera.intyg.infra.xmldsig.model.SignatureType;
import se.inera.intyg.infra.xmldsig.util.XsltUtil;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class PrepareSignatureServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(XMLDSigServiceImpl.class);

    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final String UTF_8 = "UTF-8";

    @PostConstruct
    public void init() {
        org.apache.xml.security.Init.init();
    }

    /**
     * Prepares an XMLDSig signature, a canonicalized SignedInfo and the canonicalized XML that the digest is based on.
     *
     * Given the supplied XML, the XML is canonicalized and a SHA-256 digest is created and Base64-encoded into the
     * DigestValue field.
     *
     * Also, relevant algorithms for digest, signature and canonicalization method are specified on the body of the
     * returned {@link SignatureType}.
     *
     * @param intygXml
     *            XML document to be canonicalized and digested.
     */
    public IntygSignature prepareSignature(String intygXml) {
        // 1. Transform into our base canonical form without <Register..> and dynamic attributes.
        String xml = tranformIntoIntygXml(intygXml);

        // 2. Run EXCLUSIVE canonicalization
        xml = canonicalizeXml(xml);

        // 3. Produce digest
        byte[] digestBytes = generateDigest(xml);

        // 4. Produce partial SignatureType
        SignatureType signatureType = PartialSignatureFactory.buildSignature();
        signatureType.getSignedInfo().getReference().get(0).setDigestValue(Base64.getDecoder().decode(digestBytes));

        // 5. Build the actual canonicalized <SignedInfo> to pass as payload to a sign function.
        String signedInfoForSigning = buildSignedInfoForSigning(signatureType);

        // 6. Populate and return
        return new IntygSignature(signatureType, xml, signedInfoForSigning);
    }

    /**
     * Writes the <SignatureValue> element into the Signature.
     *
     * @param signatureType
     * @param xml
     * @return
     */
    public String encodeSignatureIntoSignedXml(SignatureType signatureType, String xml) {
        // Append the SignatureElement as last element of the xml.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);

        try {
            Document doc = dbf.newDocumentBuilder().parse(IOUtils.toInputStream(xml));
            DOMResult res = new DOMResult();
            JAXBContext context = JAXBContext.newInstance(signatureType.getClass());
            context.createMarshaller().marshal(signatureType, res);
            Node sigNode = res.getNode();
            Node importedNode = doc.importNode(sigNode.getFirstChild(), true);
            doc.getDocumentElement().appendChild(importedNode);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            t.transform(source, result);

            String xmlWithSignature = sw.toString();

            return Base64.getEncoder().encodeToString(xmlWithSignature.getBytes(Charset.forName(UTF_8)));
        } catch (SAXException | IOException | ParserConfigurationException | JAXBException | TransformerException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Takes the SignedInfo element, marshals it, runs exclusive canonicalizxation and returns the resulting XML as string.
     *
     * The SignedInfo element serves as input to the sign function in XMLDSig.
     *
     * @param signatureType
     *            must contain a complete SignedInfoType including document digest, references, algorithms etc.
     * @return
     *         the resulting XML as string.
     */
    private String buildSignedInfoForSigning(SignatureType signatureType) {
        try {
            JAXBContext jc = JAXBContext.newInstance(SignatureType.class);

            // Serialize SignatureType into XML (<Signature>...</Signature>)
            StringWriter sw = new StringWriter();
            Marshaller marshaller = jc.createMarshaller();
            marshaller.marshal(signatureType, sw);
            String str = sw.toString();

            // Use XSLT to remove the parent element. (This transfers the xmldsig namespace declaration into the
            // <SignedInfo> element which is according to spec.)
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            XsltUtil.transform(IOUtils.toInputStream(str), out1, "stripparentelement.xslt");

            // Run the canonicalization to produce the final string we're to sign on.
            return canonicalizeXml(new String(out1.toByteArray(), Charset.forName(UTF_8)));
        } catch (JAXBException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private String tranformIntoIntygXml(String xml) {

        try (ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                ByteArrayOutputStream out3 = new ByteArrayOutputStream()) {

            // Use XSLT to remove unwanted elements and the parent element.
            XsltUtil.transform(IOUtils.toInputStream(xml), out1, "stripnamespaces.xslt");
            XsltUtil.transform(IOUtils.toInputStream(new String(out1.toByteArray(), Charset.forName(UTF_8))), out2, "stripmetadata.xslt");
            XsltUtil.transform(IOUtils.toInputStream(new String(out2.toByteArray(), Charset.forName(UTF_8))), out3,
                    "stripparentelement.xslt");

            return new String(out3.toByteArray(), Charset.forName(UTF_8));
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new IntygXMLDSigException(e.getMessage());
        }
    }

    private String canonicalizeXml(String intygXml) {
        try {
            Canonicalizer canonicalizer = Canonicalizer.getInstance(CanonicalizationMethod.EXCLUSIVE);
            byte[] canonicalizedXmlAsBytes = canonicalizer.canonicalize(intygXml.getBytes(UTF_8));
            return new String(canonicalizedXmlAsBytes, UTF_8);
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + " caught canonicalizing intyg XML, message: " + e.getMessage());
            throw new IllegalArgumentException(e.getCause());
        }
    }

    private byte[] generateDigest(String canonXmlString) {
        try {
            MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] sha256 = digest.digest(canonXmlString.getBytes(UTF_8));
            return Base64.getEncoder().encode(sha256);
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.error("{} caught during digest and base64-encoding, message: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}