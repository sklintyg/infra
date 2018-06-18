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
package se.inera.intyg.infra.xmldsig.filter;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import se.inera.intyg.infra.xmldsig.util.X509KeySelector;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.crypto.dsig.spec.XPathFilter2ParameterSpec;
import javax.xml.crypto.dsig.spec.XPathType;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class WithFilterTest {

    private static final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><wrapper><intyg><intygs-id><root>TSTNMT2321000156-1077</root><extension>9f02dd2f-f57c-4a73-8190-2fe602cd6e27</extension></intygs-id></intyg></wrapper>";
    //  private static final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:RegisterCertificateType xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" xmlns:ns5=\"urn:riv:clinicalprocess:healthcond:certificate:3.2\" xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\" xmlns:ns4=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"><ns2:intyg><intygs-id><ns3:root>TSTNMT2321000156-1077</ns3:root><ns3:extension>9f02dd2f-f57c-4a73-8190-2fe602cd6e27</ns3:extension></intygs-id><typ><ns3:code>LISJP</ns3:code><ns3:codeSystem>b64ea353-e8f6-4832-b563-fc7d46f29548</ns3:codeSystem><ns3:displayName>Läkarintyg för sjukpenning</ns3:displayName></typ><version>1.0</version><signeringstidpunkt>2018-03-08T11:54:22</signeringstidpunkt><skickatTidpunkt>2018-03-08T11:54:22</skickatTidpunkt><patient><person-id><ns3:root>1.2.752.129.2.1.3.1</ns3:root><ns3:extension>191212121212</ns3:extension></person-id><fornamn></fornamn><efternamn></efternamn><postadress></postadress><postnummer></postnummer><postort></postort></patient><skapadAv><personal-id><ns3:root>1.2.752.129.2.1.4.1</ns3:root><ns3:extension>TSTNMT2321000156-1079</ns3:extension></personal-id><fullstandigtNamn>Arnold Johansson</fullstandigtNamn><forskrivarkod>0000000</forskrivarkod><enhet><enhets-id><ns3:root>1.2.752.129.2.1.4.1</ns3:root><ns3:extension>TSTNMT2321000156-1077</ns3:extension></enhets-id><arbetsplatskod><ns3:root>1.2.752.29.4.71</ns3:root><ns3:extension>1234567890</ns3:extension></arbetsplatskod><enhetsnamn>NMT vg3 ve1</enhetsnamn><postadress>NMT gata 3</postadress><postnummer>12345</postnummer><postort>Testhult</postort><telefonnummer>0101112131416</telefonnummer><epost>enhet3@webcert.invalid.se</epost><vardgivare><vardgivare-id><ns3:root>1.2.752.129.2.1.4.1</ns3:root><ns3:extension>TSTNMT2321000156-102Q</ns3:extension></vardgivare-id><vardgivarnamn>NMT vg3</vardgivarnamn></vardgivare></enhet></skapadAv><relation><typ><ns3:code>ERSATT</ns3:code><ns3:codeSystem>c2362fcd-eda0-4f9a-bd13-b3bbaf7f2146</ns3:codeSystem><ns3:displayName>Ersatter</ns3:displayName></typ><intygs-id><ns3:root>TSTNMT2321000156-1077</ns3:root><ns3:extension>9020fbb9-e387-40b0-ba75-ac2746e4736b</ns3:extension></intygs-id></relation><svar id=\"1\"><instans>1</instans><delsvar id=\"1.1\"><ns3:cv><ns3:code>UNDERSOKNING</ns3:code><ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem><ns3:displayName>Min undersokning av patienten</ns3:displayName></ns3:cv></delsvar><delsvar id=\"1.2\">2016-08-01</delsvar></svar><svar id=\"1\"><instans>2</instans><delsvar id=\"1.1\"><ns3:cv><ns3:code>TELEFONKONTAKT</ns3:code><ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem><ns3:displayName>Min telefonkontakt med patienten</ns3:displayName></ns3:cv></delsvar><delsvar id=\"1.2\">2016-08-02</delsvar></svar><svar id=\"1\"><instans>3</instans><delsvar id=\"1.1\"><ns3:cv><ns3:code>JOURNALUPPGIFTER</ns3:code><ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem><ns3:displayName>Journaluppgifter fran den</ns3:displayName></ns3:cv></delsvar><delsvar id=\"1.2\">2016-08-03</delsvar></svar><svar id=\"1\"><instans>4</instans><delsvar id=\"1.1\"><ns3:cv><ns3:code>ANNAT</ns3:code><ns3:codeSystem>KV_FKMU_0001</ns3:codeSystem><ns3:displayName>Annat</ns3:displayName></ns3:cv></delsvar><delsvar id=\"1.2\">2016-08-04</delsvar><delsvar id=\"1.3\">Telepatisk kommunikation</delsvar></svar><svar id=\"28\"><instans>1</instans><delsvar id=\"28.1\"><ns3:cv><ns3:code>NUVARANDE_ARBETE</ns3:code><ns3:codeSystem>KV_FKMU_0002</ns3:codeSystem><ns3:displayName>Nuvarande arbete</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"29\"><delsvar id=\"29.1\">Siare</delsvar></svar><svar id=\"6\"><delsvar id=\"6.2\"><ns3:cv><ns3:code>D50</ns3:code><ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem><ns3:displayName>Jarnbristanemi</ns3:displayName></ns3:cv></delsvar><delsvar id=\"6.1\">Jarnbristanemi</delsvar><delsvar id=\"6.4\"><ns3:cv><ns3:code>G10</ns3:code><ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem><ns3:displayName>Huntingtons sjukdom</ns3:displayName></ns3:cv></delsvar><delsvar id=\"6.3\">Huntingtons sjukdom</delsvar><delsvar id=\"6.6\"><ns3:cv><ns3:code>T241</ns3:code><ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem><ns3:displayName>Brannskada av forsta graden pa hoft och nedre extremitet utom fotled och fot</ns3:displayName></ns3:cv></delsvar><delsvar id=\"6.5\">Brannskada av forsta graden pa hoft och nedre extremitet utom fotled och fot</delsvar></svar><svar id=\"35\"><delsvar id=\"35.1\">Inga fynd gjordes</delsvar></svar><svar id=\"17\"><delsvar id=\"17.1\">Har svart att sitta och ligga.. Och sta. Far huka sig.</delsvar></svar><svar id=\"19\"><delsvar id=\"19.1\">Meditering, sjalvmedicinering</delsvar></svar><svar id=\"20\"><delsvar id=\"20.1\">Inga planerade atgarder. Patienten har ingen almanacka.</delsvar></svar><svar id=\"32\"><instans>1</instans><delsvar id=\"32.1\"><ns3:cv><ns3:code>HELT_NEDSATT</ns3:code><ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem><ns3:displayName>100%</ns3:displayName></ns3:cv></delsvar><delsvar id=\"32.2\"><ns3:datePeriod><ns3:start>2016-08-08</ns3:start><ns3:end>2016-08-22</ns3:end></ns3:datePeriod></delsvar></svar><svar id=\"32\"><instans>2</instans><delsvar id=\"32.1\"><ns3:cv><ns3:code>TRE_FJARDEDEL</ns3:code><ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem><ns3:displayName>75%</ns3:displayName></ns3:cv></delsvar><delsvar id=\"32.2\"><ns3:datePeriod><ns3:start>2016-08-23</ns3:start><ns3:end>2016-08-24</ns3:end></ns3:datePeriod></delsvar></svar><svar id=\"32\"><instans>3</instans><delsvar id=\"32.1\"><ns3:cv><ns3:code>HALFTEN</ns3:code><ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem><ns3:displayName>50%</ns3:displayName></ns3:cv></delsvar><delsvar id=\"32.2\"><ns3:datePeriod><ns3:start>2016-08-25</ns3:start><ns3:end>2016-08-27</ns3:end></ns3:datePeriod></delsvar></svar><svar id=\"32\"><instans>4</instans><delsvar id=\"32.1\"><ns3:cv><ns3:code>EN_FJARDEDEL</ns3:code><ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem><ns3:displayName>25%</ns3:displayName></ns3:cv></delsvar><delsvar id=\"32.2\"><ns3:datePeriod><ns3:start>2016-08-29</ns3:start><ns3:end>2016-11-26</ns3:end></ns3:datePeriod></delsvar></svar><svar id=\"37\"><delsvar id=\"37.1\">Har foljt beslutstodet till punkt och pricka.</delsvar></svar><svar id=\"33\"><delsvar id=\"33.1\">true</delsvar><delsvar id=\"33.2\">Har bra och daliga dagar. Battre att jobba 22h-24h de bra dagarna sa patienten kan vila sedan.</delsvar></svar><svar id=\"34\"><delsvar id=\"34.1\">true</delsvar></svar><svar id=\"39\"><delsvar id=\"39.1\"><ns3:cv><ns3:code>ATER_X_ANTAL_DGR</ns3:code><ns3:codeSystem>KV_FKMU_0006</ns3:codeSystem><ns3:displayName>Patienten kommer med stor sannolikhet att aterga helt i nuvarande sysselsattning efter x antal dagar</ns3:displayName></ns3:cv></delsvar><delsvar id=\"39.3\"><ns3:cv><ns3:code>SEXTIO_DGR</ns3:code><ns3:codeSystem>KV_FKMU_0007</ns3:codeSystem><ns3:displayName>60 dagar</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>1</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>ARBETSTRANING</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Arbetstraning</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>2</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>ARBETSANPASSNING</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Arbetsanpassning</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>3</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>SOKA_NYTT_ARBETE</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Soka nytt arbete</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>4</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>BESOK_ARBETSPLATS</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Besok pa arbetsplatsen</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>5</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>ERGONOMISK</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Ergonomisk bedomning</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>6</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>HJALPMEDEL</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Hjalpmedel</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>7</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>KONFLIKTHANTERING</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Konflikthantering</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>8</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>KONTAKT_FHV</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Kontakt med foretagshalsovard</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>9</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>OMFORDELNING</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>Omfordelning av arbetsuppgifter</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"40\"><instans>10</instans><delsvar id=\"40.1\"><ns3:cv><ns3:code>OVRIGA_ATGARDER</ns3:code><ns3:codeSystem>KV_FKMU_0004</ns3:codeSystem><ns3:displayName>ovrigt</ns3:displayName></ns3:cv></delsvar></svar><svar id=\"44\"><delsvar id=\"44.1\">Darfor.</delsvar></svar><svar id=\"25\"><delsvar id=\"25.1\">Inga ovriga upplysningar.</delsvar></svar><svar id=\"26\"><delsvar id=\"26.1\">true</delsvar><delsvar id=\"26.2\">Alltid roligt att prata med FK.</delsvar></svar></ns2:intyg></ns2:RegisterCertificateType>";
            private static final String xpath = "//extension[text() = '9f02dd2f-f57c-4a73-8190-2fe602cd6e27']/../..";

    @Test
    public void test() throws Exception {

        org.apache.xml.security.Init.init();
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");

        // Base64 base64 = new Base64();
        // Create a DOM XMLSignatureFactory that will be used to
        // generate the enveloped signature.
        final XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Create a Reference to the enveloped document (in this case,
        // you are signing the whole document, so a URI of "" signifies
        // that, and also specify the SHA1 digest algorithm and
        // the ENVELOPED Transform.
        final List<XPathType> xpaths = new ArrayList<XPathType>() {
            {
                add(new XPathType(xpath, XPathType.Filter.INTERSECT));
            }
        };
        List<Transform> transforms = new ArrayList<>();
        // RQP9uqPpyauvZ0xH8BbBWnUEHIo=
        XMLStructure sheet = new DOMStructure(loadXsltElement("transforms/stripall.xslt"));

        transforms.add(fac.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#", (TransformParameterSpec) null));
        transforms.add(fac.newTransform(Transform.XSLT, new XSLTTransformParameterSpec(sheet)));
        transforms.add(fac.newTransform(Transform.XPATH2, new XPathFilter2ParameterSpec(xpaths)));
        transforms.add(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));

        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA256, null),
                transforms,
                null, null);

        // Create the SignedInfo.
        SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
                (C14NMethodParameterSpec) null),
                fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", null),
                Collections.singletonList(ref));

        // Load the KeyStore and get the signing key and certificate.
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new ClassPathResource("keystore.jks").getInputStream(), "12345678".toCharArray());
        KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("1",
                new KeyStore.PasswordProtection("12345678".toCharArray()));
        X509Certificate cert = (X509Certificate) keyEntry.getCertificate();

        // Create the KeyInfo containing the X509Data.
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(cert);
        X509Data xd = kif.newX509Data(x509Content);
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));

        // Instantiate the document to be signed.
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));

        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext(keyEntry.getPrivateKey(), doc.getDocumentElement());

        // Create the XMLSignature, but don't sign it yet.
        XMLSignature signature = fac.newXMLSignature(si, ki);

        // Marshal, generate, and sign the enveloped signature.
        signature.sign(dsc);

        // Output the resulting document.
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(System.out));

        validate(fac, doc);
    }

    private static void validate(XMLSignatureFactory fac, Document doc) throws Exception {

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
