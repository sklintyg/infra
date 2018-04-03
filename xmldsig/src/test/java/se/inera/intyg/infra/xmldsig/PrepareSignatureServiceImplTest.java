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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.SignatureValueType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.Signature;

public class PrepareSignatureServiceImplTest {

    private String certAtString = "MIIB+zCCAWQCCQCUxqAHHrhg+jANBgkqhkiG9w0BAQsFADBCMQswCQYDVQQGEwJTRTELMAkGA1UECAwCVkcxEzARBgNVBAcMCkdvdGhlbmJ1cmcxETAPBgNVBAoMCENhbGxpc3RhMB4XDTE4MDMxMDIwMDY0MFoXDTIxMTIwNDIwMDY0MFowQjELMAkGA1UEBhMCU0UxCzAJBgNVBAgMAlZHMRMwEQYDVQQHDApHb3RoZW5idXJnMREwDwYDVQQKDAhDYWxsaXN0YTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA4cB6VC0f9ne0UKC/XzsoP5ocv7WyGt5378f/DGnVAF3aWzderzLnXMqSdGbLOuEzUUdbjYgQkqQSs6wy872KLf0RzQzllxwpBQJ/2r+CrW6tROJa0FYEIhgWDdRGlS+9+hd3E9Ilz2PTZDF4c1C+4l/xq149OCgiAGfadeBZA5MCAwEAATANBgkqhkiG9w0BAQsFAAOBgQDU+Mrw98Qm8K0U8A208Ee01PZeIpqC9CIRIXJd0PFwXJjTlGIWckwrdsgbGtwOAlA2rzAx/FUhQD4/1F4G5mo/DrtOzzx9fKE0+MQreTC/HOm61ja3cWm4yI5G0W7bLTBBhsEoOzclycNK/QjeP+wYO+k11mtPM4SP4kCj3gh97g==";

    private PrepareSignatureServiceImpl testee = new PrepareSignatureServiceImpl();



    @Before
    public void init() {
        org.apache.xml.security.Init.init();
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl") ; //"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"); // "" "net.sf.saxon.jaxp.SaxonTransformerFactory");

    }


    @Test
    public void testBuildPreparedSignature() throws IOException {
        InputStream xmlResource = getXmlResource("classpath:/unsigned/signed-lisjp-i18n-noxmldsig.xml");
        String xml = IOUtils.toString(xmlResource);
        IntygXMLDSignature intygXMLDSignature = testee.prepareSignature(xml);

        byte[] signature = createSignature(intygXMLDSignature.getSigningData().getBytes(Charset.forName("UTF-8")));
        SignatureValueType svt = new SignatureValueType();
        svt.setValue(signature);
        intygXMLDSignature.getSignatureType().setSignatureValue(svt);

        // Stuff KeyInfo
        KeyInfoType keyInfo = new XMLDSigServiceImpl().buildKeyInfoForCertificate(certAtString);
        intygXMLDSignature.getSignatureType().setKeyInfo(keyInfo);

        try {
            String resXml =  testee.encodeSignatureIntoSignedXml(intygXMLDSignature.getSignatureType(), xml);
            System.out.println(resXml);
            Assert.assertTrue(new XMLDSigServiceImpl().validateSignatureValidity(resXml, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    private String extractIntygFromRegisterCertificate(InputStream xmlResource) {
//        try {
//            JAXBContext jc = JAXBContext.newInstance(RegisterCertificateType.class, DatePeriodType.class);
//            Unmarshaller unmarshaller = jc.createUnmarshaller();
//            JAXBElement<RegisterCertificateType> jaxbElement = unmarshaller.unmarshal(new StreamSource(xmlResource), RegisterCertificateType.class);
//
//            JAXBContext jc2 = JAXBContext.newInstance(Intyg.class, DatePeriodType.class);
//            QName qname = new QName("urn:riv:clinicalprocess:healthcond:certificate:3", "Intyg");
//            JAXBElement<Intyg> root = new JAXBElement<>(qname, Intyg.class, jaxbElement.getValue().getIntyg());
//            Marshaller marshaller = jc2.createMarshaller();
//            StringWriter sw = new StringWriter();
//            marshaller.marshal(root, sw);
//            System.out.println(sw.toString());
//            return sw.toString();
//        } catch (JAXBException e) {
//            throw new RuntimeException(e);
//        }
//    }

//    private void signSignedInfo(IntygXMLDSignature intygSignature) {
//        try {
//            JAXBContext jc = JAXBContext.newInstance(SignatureType.class);
//
//            StringWriter sw = new StringWriter();
//            Marshaller marshaller = jc.createMarshaller();
//            marshaller.marshal(intygSignature.getSignatureType(), sw);
//
//            String str = sw.toString();
//
//            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
//            XsltUtil.transform(IOUtils.toInputStream(str), out1, "stripparentelement.xslt");
//
//            str = new String(out1.toByteArray(), Charset.forName("UTF-8"));
//            System.out.println("Transformed: " + str);
//
//
//            String canonicalizedSignedInfoXml = testee.canonicalizeXml(str);
//            System.out.println("Canonicalized: " + canonicalizedSignedInfoXml);
//
//         //   byte[] signedInfoDigest = testee.generateDigest(canonicalizedSignedInfoXml);
//         //   System.out.println("digest: " + new  String(signedInfoDigest));
//
//            byte[] signature = createSignature(canonicalizedSignedInfoXml.getBytes());
//            System.out.println("signature: " + Base64.getEncoder().encodeToString(signature));
//
//            SignatureValueType svt = new SignatureValueType();
//            svt.setValue(signature);
//            intygSignature.getSignatureType().setSignatureValue(svt);
//        } catch (JAXBException e) {
//            throw new RuntimeException(e.getCause());
//        }
//    }

    private InputStream getXmlResource(String source) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext()) {
            Resource resource = context.getResource(source);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private byte[] createSignature(byte[] digest) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource("keystore.jks").getInputStream(), "12345678".toCharArray());

            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("1",
                    new KeyStore.PasswordProtection("12345678".toCharArray()));
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(keyEntry.getPrivateKey());
            rsa.update(digest);
            byte[] signatureBytes = rsa.sign();
            return signatureBytes;
        } catch (Exception e) {
            throw new IllegalStateException("Not possible to sign digest: " + e.getMessage());
        }
    }
}
