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
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import se.inera.intyg.infra.xmldsig.model.SignatureValueType;

import javax.xml.bind.JAXBContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.Signature;
import java.util.Base64;

public class PrepareSignatureServiceImplTest {

    private PrepareSignatureServiceImpl testee = new PrepareSignatureServiceImpl();

    @Before
    public void init() {
        org.apache.xml.security.Init.init();
    }


    @Test
    public void testBuildPreparedSignature() throws IOException {
        InputStream xmlResource = getXmlResource("classpath:/unsigned/signed-lisjp-i18n.xml");
        String xml = IOUtils.toString(xmlResource);
        IntygXMLDSignature intygXMLDSignature = testee.prepareSignature(xml);
        byte[] signature = createSignature(intygXMLDSignature.getSigningData().getBytes(Charset.forName("UTF-8")));
        SignatureValueType svt = new SignatureValueType();
        svt.setValue(signature);
        intygXMLDSignature.getSignatureType().setSignatureValue(svt);

        JAXBContext context = null;
        try {
//            context = JAXBContext.newInstance(intygXMLDSignature.getSignatureType().getClass());
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
//
//            StringWriter sw = new StringWriter();
//            marshaller.marshal(intygXMLDSignature.getSignatureType(), sw);
//            String canonicalizedSignatureXml = new XMLDSigServiceImpl().canonicalizeXml(sw.toString());
//            int insertAt = xml.indexOf("</ns2:intyg>");
//            xml = xml.substring(0, insertAt) + canonicalizedSignatureXml + xml.substring(insertAt);

            String base64 = testee.encodeSignatureIntoSignedXml(intygXMLDSignature.getSignatureType(), xml);
            System.out.println(new String(Base64.getDecoder().decode(base64), Charset.forName("UTF-8")));
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
