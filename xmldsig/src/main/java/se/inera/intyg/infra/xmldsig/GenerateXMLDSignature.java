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

import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;

/**
 * Class for signing and arbitrary XML document with XMLDSig, returning an OutputStream with the resulting
 * DOM including the <Signature> element.
 *
 * Derived from official Oracle samples
 * https://docs.oracle.com/javase/9/security/java-xml-digital-signature-api-overview-and-tutorial.htm
 * #JSSEC-GUID-E7E9239F-C973-4D05-AC3F-53F714C259DB
 *
 * @author eriklupander
 */
public class GenerateXMLDSignature {

    public GenerateXMLDSignature() {
        Canonicalizer.registerDefaultAlgorithms();
    }

    public String prepareSignature(String xml) {
        String canonizedXml = canon(xml);

        String hash = generateDigest(canonizedXml);
        return hash;
    }

    public String canon(String str) {

        try {
            Canonicalizer canonicalizer = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_OMIT_COMMENTS); // ALGO_ID_C14N_WITH_COMMENTS
            byte[] data = canonicalizer.canonicalize(str.getBytes("UTF-8"));
            return new String(data, "UTF-8");
        } catch (InvalidCanonicalizerException e) {
            e.printStackTrace();
        } catch (CanonicalizationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateDigest(String canonXmlString) {
        // com.sun.org.apache.xml.internal.security.Init.init();
        try {

            // get instance of the message digest based on the SHA-256 hashing algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // call the digest method passing the byte stream on the text, this directly updates the message
            // being digested and perform the hashing
            byte[] hash = digest.digest(canonXmlString.getBytes(StandardCharsets.UTF_8));

            // encode the endresult byte hash
            byte[] encodedBytes = Base64.getEncoder().encode(hash);
            return new String(encodedBytes, Charset.forName("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public void generateSignature(InputStream is) {
        // Create a DOM XMLSignatureFactory that will be used to generate the
        // enveloped signature
        try {
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            // Create a Reference to the enveloped document (in this case we are
            // signing the whole document, so a URI of "" signifies that) and
            // also specify the SHA256 digest algorithm and the ENVELOPED Transform.
            DigestMethod digestMethod = fac.newDigestMethod(DigestMethod.SHA256, null);

            Reference ref = fac.newReference("", digestMethod,
                    Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                    null, null);

            // Create the SignedInfo
            // SignedInfo si =
            fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                    (C14NMethodParameterSpec) null),
                    // fac.newSignatureMethod("http://www.w3.org/2000/09/xmldsig#dsa-sha256", null),
                    fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
                    Collections.singletonList(ref));

            // Create a RSA KeyPair
            // KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            // kpg.initialize(2048);
            // KeyPair kp = kpg.generateKeyPair();
            //
            // // Create a KeyValue containing the DSA PublicKey that was generated
            // KeyInfoFactory kif = fac.getKeyInfoFactory();
            // KeyValue kv = kif.newKeyValue(kp.getPublic());
            //
            // // Create a KeyInfo and add the KeyValue to it
            // KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

            // Instantiate the document to be signed
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().parse(is);

            // Create a DOMSignContext and specify the DSA PrivateKey and
            // location of the resulting XMLSignature's parent element
            // DOMSignContext dsc = new DOMSignContext
            // (null, doc.getDocumentElement());
            // // DOMPreSignedContext preSignedContext = new DOMPreSignedContext(doc.getDocumentElement());
            //
            // DOMReference dref = (DOMReference) ref;
            // dref.digest(preSignedContext);
            //
            // //DOMSignContext context = (DOMSignContext)dsc;
            // DOMXMLSignature signature = (DOMXMLSignature) fac.newXMLSignature(si, null);
            // signature.marshal(preSignedContext.getParent(),
            // DOMUtils.getSignaturePrefix(preSignedContext), preSignedContext);

            // Create the XMLSignature (but don't sign it yet)
            // XMLSignature signature = fac.newXMLSignature()newXMLSignature(si, ki);
            // XMLSignature signature = fac.newXMLSignature(si, null, null, "id", "VALUE FROM EFOS");
            // Marshal, generate (and sign) the enveloped signature
            // signature.sign(dsc);

            // output the resulting document
            OutputStream os = System.out;

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(os));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
