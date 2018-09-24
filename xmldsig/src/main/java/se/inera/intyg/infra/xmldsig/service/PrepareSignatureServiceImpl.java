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
import org.w3._2000._09.xmldsig_.ObjectFactory;
import org.w3._2000._09.xmldsig_.SignatureType;
import org.w3._2000._09.xmldsig_.SignedInfoType;
import org.w3._2002._06.xmldsig_filter2.XPathType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import se.inera.intyg.infra.xmldsig.exception.IntygXMLDSigException;
import se.inera.intyg.infra.xmldsig.factory.PartialSignatureFactory;
import se.inera.intyg.infra.xmldsig.model.IntygXMLDSignature;
import se.inera.intyg.infra.xmldsig.util.XsltUtil;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class PrepareSignatureServiceImpl implements PrepareSignatureService {

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
     * @param intygsId
     *            The ID of the intyg is required for the XPath expression selecting the content to be digested.
     */
    @Override
    public IntygXMLDSignature prepareSignature(String intygXml, String intygsId) {

        // 1. Transform into our base canonical form without namespaces and dynamic attributes.
        String xml = tranformIntoIntygXml(intygXml);

        // 2. Run XPath to pick out <intyg> element.
        xml = applyXPath(intygsId, xml);

        // 3. Run EXCLUSIVE canonicalization
        xml = canonicalizeXml(xml);

        // 4. Produce digest of the <intyg>...</intyg> in canonical form.
        byte[] digestBytes = generateDigest(xml);

        // 5. Produce unfinished SignatureType
        SignatureType signatureType = PartialSignatureFactory.buildSignature(intygsId, digestBytes);

        // 6. Build the actual canonicalized <SignedInfo> to pass as payload to a sign function.
        String signedInfoForSigning = buildSignedInfoForSigning(signatureType);

        // 7. Populate and return
        return IntygXMLDSignature.IntygXMLDSignatureBuilder.anIntygXMLDSignature()
                .withIntygJson("set later...")
                .withCanonicalizedIntygXml(xml)
                .withSignedInfoForSigning(signedInfoForSigning)
                .withSignatureType(signatureType)
                .build();
    }

    private String applyXPath(String intygsId, String xml) {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        InputSource inputSource = new InputSource(new StringReader(xml));
        try {
            return nodeToString((Node) xpath.compile("//intygs-id/extension[text()='" + intygsId + "']/../..")
                    .evaluate(inputSource, XPathConstants.NODE));
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "no");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    /**
     * Writes the <SignatureValue> element into the Signature.
     *
     * @param signatureType
     * @param xml
     * @return
     */
    @Override
    public String encodeSignatureIntoSignedXml(SignatureType signatureType, String xml) {
        // Append the SignatureElement as last element of the xml.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        try {
            Document doc = dbf.newDocumentBuilder().parse(IOUtils.toInputStream(xml, Charset.forName("UTF-8")));
            DOMResult res = new DOMResult();

            JAXBContext context = JAXBContext.newInstance(SignatureType.class, XPathType.class);
            JAXBElement<SignatureType> signature = new ObjectFactory().createSignature(signatureType);
            context.createMarshaller().marshal(signature, res);
            Node sigNode = res.getNode();
            Node importedNode = doc.importNode(sigNode.getFirstChild(), true);

            doc.getDocumentElement().getFirstChild().appendChild(importedNode);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);

            t.transform(source, result);

            return sw.toString();

            // return xmlWithSignature.getBytes(Charset.forName(UTF_8));
        } catch (SAXException | IOException | ParserConfigurationException | JAXBException | TransformerException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /**
     * Takes the SignedInfoType from the signatureType element, marshals it, runs exclusive canonicalization and returns
     * the resulting SignedInfo XML as string.
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
            JAXBElement<SignedInfoType> signature = new ObjectFactory().createSignedInfo(signatureType.getSignedInfo());
            JAXBContext jc = JAXBContext.newInstance(SignedInfoType.class, XPathType.class);
            // Serialize SignedInfoType into XML (<SignedInfo>...</SignedInfo>)
            StringWriter sw = new StringWriter();
            Marshaller marshaller = jc.createMarshaller();
            marshaller.marshal(signature, sw);

            // Run the canonicalization to produce the final string we're to sign on.
            return canonicalizeXml(sw.toString());
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    private String tranformIntoIntygXml(String xml) {

        try (ByteArrayOutputStream out1 = new ByteArrayOutputStream()) {

            // Use XSLT to remove unwanted elements and the parent element.
            // Note that the "stripall.xslt" performs BOTH local() on all nodes AND filters unwanted stuff out.
            XsltUtil.transform(IOUtils.toInputStream(xml), out1, "transforms/stripall.xslt");

            return new String(out1.toByteArray(), Charset.forName(UTF_8));
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

    private byte[] generateDigest(String stringToDigest) {
        try {
            MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] sha256 = digest.digest(stringToDigest.getBytes(UTF_8));
            return Base64.getEncoder().encode(sha256);
        } catch (IOException | NoSuchAlgorithmException e) {
            LOG.error("{} caught during digest and base64-encoding, message: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
