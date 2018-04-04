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

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;

public class ReferenceSignatureTester {

    @Test
    public void createReferenceSignature() throws Exception {
        // Create a DOM XMLSignatureFactory that will be used to
        // generate the enveloped signature.
        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Create a canonicalization transform
        ;
        // Create a Reference to the enveloped document (in this case,
        // you are signing the whole document, so a URI of "" signifies
        // that, and also specify the SHA1 digest algorithm and
        // the ENVELOPED Transform.
        List transforms = new ArrayList();
        //transforms.add(fac.newTransform(
        //        "http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec) null));
       // DOMStructure stylesheet = new DOMStructure(loadXslt("stripparentelement_2.xslt"));
    //    DOMXSLTTransform stylesheet = new DOMXSLTTransform();
    //    stylesheet.init(new XSLTTransformParameterSpec(new DOMStructure(loadXslt("stripparentelement_2.xslt"))));
        //   XSLTTransformParameterSpec params = new XSLTTransformParameterSpec(stylesheet);
        transforms.add(fac.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec) null));
        /*
        transforms.add(fac.newTransform(Transform.XSLT, new XSLTTransformParameterSpec(new DOMStructure(loadXslt("stripnamespaces.xslt")))));
        transforms.add(fac.newTransform(Transform.XSLT, new XSLTTransformParameterSpec(new DOMStructure(loadXslt("stripmetadata.xslt")))));
        transforms.add(fac.newTransform(Transform.XSLT, new XSLTTransformParameterSpec(new DOMStructure(loadXslt("stripparentelement_2.xslt")))));
        transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        */
        transforms.add(fac.newTransform(Transform.XSLT, new XSLTTransformParameterSpec(new DOMStructure(loadXslt("stripall.xslt")))));
        transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));


        /*
        <Transform Algorithm="http://www.w3.org/TR/1999/REC-xslt-19991116">
            <xs:transform xmlns:xs="http://www.w3.org/1999/XSL/Transform" version="1.0">
              <xs:template match="/">
                <xs:apply-templates />
              </xs:template>
            <xs:template match="elementToTransform">
                <transformedElement xmlns="" />
              </xs:template>
            </xs:transform>
          </Transform>
         */
        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA256, null),
                transforms, null, null);

        SignatureMethod sm = fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
                (SignatureMethodParameterSpec) null);

        // Create the SignedInfo.
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
                (C14NMethodParameterSpec) null),
                sm,
                Collections.singletonList(ref));

        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new ClassPathResource("keystore.jks").getInputStream(), "12345678".toCharArray());

        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("1",
                new KeyStore.PasswordProtection("12345678".toCharArray()));
        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List x509Content = new ArrayList();
        // x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        ClassPathResource classPathResource = new ClassPathResource("unsigned/signed-lisjp.xml");
        Document doc = dbf.newDocumentBuilder().parse(classPathResource.getInputStream());

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), doc.getDocumentElement());

        // Create the XMLSignature, but don't sign it yet.
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // Marshal, generate, and sign the enveloped signature.
        signature.sign(dsc);

        // Output the resulting document.
        //TransformerFactory tf = TransformerFactory.newInstance();
        TransformerFactory tf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null);
        Transformer trans = tf.newTransformer();

       // trans.setOutputProperty(OutputKeys.INDENT, "yes");
      //  trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        trans.transform(new DOMSource(doc), new StreamResult(System.out));

        // START VALIDATION
        validate(fac, doc);
    }

    private void validate(XMLSignatureFactory fac, Document doc) throws Exception {
        Thread.sleep(1000L);
        System.out.println("START VALIDATION\n");
       // XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Find Signature element.
        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        }

        // Create a DOMValidateContext and specify a KeySelector
        // and document context.
        DOMValidateContext valContext = new DOMValidateContext(new X509KeySelector(), nl.item(0));

        // Unmarshal the XMLSignature.
        XMLSignature sig = fac.unmarshalXMLSignature(valContext);

        // Validate the XMLSignature.
        boolean coreValidity = sig.validate(valContext);

        // Check core validation status.
        if (!coreValidity) {
            System.err.println("Signature failed core validation");
            boolean sv = sig.getSignatureValue().validate(valContext);
            System.out.println("signature validation status: " + sv);
            if (!sv) {
                // Check the validation status of each Reference.
                Iterator i = sig.getSignedInfo().getReferences().iterator();
                for (int j = 0; i.hasNext(); j++) {
                    boolean refValid = ((Reference) i.next()).validate(valContext);
                    System.out.println("ref[" + j + "] validity status: " + refValid);
                }
            }
        } else {
            System.out.println("Signature passed core validation");
        }
    }

    private Node loadXslt(String path) {

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




    public void generateSignatureforResumen(String originalXmlFilePath, KeyStore tokenKeyStore, String pin) throws Exception {
        // Get the XML Document object
        Document doc = null; // getXmlDocument(originalXmlFilePath);
        // Create XML Signature Factory
        PrivateKey signatureKey_ = null;
        PublicKey pubKey_ = null;
        X509Certificate signingCertificate_ = null;
        Boolean prik = false;
        Boolean pubk = false;
        Enumeration aliases = tokenKeyStore.aliases();
        while (aliases.hasMoreElements()) {
            String keyAlias = aliases.nextElement().toString();
            java.security.Key key = tokenKeyStore.getKey(keyAlias, pin.toCharArray());
            if (key instanceof java.security.interfaces.RSAPrivateKey) {
                Certificate[] certificateChain = tokenKeyStore.getCertificateChain(keyAlias);
                X509Certificate signerCertificate = (X509Certificate) certificateChain[0];
                boolean[] keyUsage = signerCertificate.getKeyUsage();
                // check for digital signature or non-repudiation,
                // but also accept if none is set
                if ((keyUsage == null) || keyUsage[0] || keyUsage[1]) {
                    signatureKey_ = (PrivateKey) key;
                    signingCertificate_ = signerCertificate;
                    prik = true;
                    pubKey_ = signerCertificate.getPublicKey();
                    break;
                }
            }
        }

        if (signatureKey_ == null) {
            throw new GeneralSecurityException(
                    "Found no signature key. Ensure that a valid card is inserted.");
        }

        XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");
        Reference ref = null;
        SignedInfo signedInfo = null;
        try {
            ref = xmlSigFactory.newReference("", xmlSigFactory.newDigestMethod(DigestMethod.SHA1, null),
                    Collections.singletonList(xmlSigFactory.newTransform(Transform.ENVELOPED,
                            (TransformParameterSpec) null)),
                    null, null);
            signedInfo = xmlSigFactory.newSignedInfo(
                    xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                            (C14NMethodParameterSpec) null),
                    xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(ref));

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        KeyInfoFactory kif = xmlSigFactory.getKeyInfoFactory();
        X509Data x509data = kif.newX509Data(Collections.nCopies(1, signingCertificate_));
        KeyValue kval = kif.newKeyValue(pubKey_);
        List keyInfoItems = new ArrayList();
        keyInfoItems.add(kval);
        keyInfoItems.add(x509data);
        // Object list[];
        KeyInfo keyInfo = kif.newKeyInfo(keyInfoItems);
        // Create a new XML Signature
        XMLSignature xmlSignature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);

        DOMSignContext domSignCtx = new DOMSignContext((Key) signatureKey_, doc.getDocumentElement());

        try {
            // Sign the document
            xmlSignature.sign(domSignCtx);
        } catch (MarshalException ex) {
            ex.printStackTrace();
        } catch (XMLSignatureException ex) {
            ex.printStackTrace();
        }
        // Store the digitally signed document inta a location
        // storeSignedDoc(doc, destnSignedXmlFilePath);
    }
}
