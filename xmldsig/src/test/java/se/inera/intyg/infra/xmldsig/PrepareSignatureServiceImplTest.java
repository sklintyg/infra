/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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

import static se.inera.intyg.infra.xmldsig.model.FakeSignatureConstants.FAKE_KEYSTORE_ALIAS;
import static se.inera.intyg.infra.xmldsig.model.FakeSignatureConstants.FAKE_KEYSTORE_NAME;
import static se.inera.intyg.infra.xmldsig.model.FakeSignatureConstants.FAKE_KEYSTORE_PASSWORD;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.Signature;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3._2000._09.xmldsig_.KeyInfoType;
import org.w3._2000._09.xmldsig_.SignatureValueType;
import se.inera.intyg.infra.xmldsig.factory.PartialSignatureFactory;
import se.inera.intyg.infra.xmldsig.model.IntygXMLDSignature;
import se.inera.intyg.infra.xmldsig.service.PrepareSignatureServiceImpl;
import se.inera.intyg.infra.xmldsig.service.XMLDSigServiceImpl;

public class PrepareSignatureServiceImplTest {

    private String certAtString = "MIIB+zCCAWQCCQCUxqAHHrhg+jANBgkqhkiG9w0BAQsFADBCMQswCQYDVQQGEwJTRTELMAkGA1UECAwCVkcxEzARBgNVBAcMCkdvdGhlbmJ1cmcxETAPBgNVBAoMCENhbGxpc3RhMB4XDTE4MDMxMDIwMDY0MFoXDTIxMTIwNDIwMDY0MFowQjELMAkGA1UEBhMCU0UxCzAJBgNVBAgMAlZHMRMwEQYDVQQHDApHb3RoZW5idXJnMREwDwYDVQQKDAhDYWxsaXN0YTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA4cB6VC0f9ne0UKC/XzsoP5ocv7WyGt5378f/DGnVAF3aWzderzLnXMqSdGbLOuEzUUdbjYgQkqQSs6wy872KLf0RzQzllxwpBQJ/2r+CrW6tROJa0FYEIhgWDdRGlS+9+hd3E9Ilz2PTZDF4c1C+4l/xq149OCgiAGfadeBZA5MCAwEAATANBgkqhkiG9w0BAQsFAAOBgQDU+Mrw98Qm8K0U8A208Ee01PZeIpqC9CIRIXJd0PFwXJjTlGIWckwrdsgbGtwOAlA2rzAx/FUhQD4/1F4G5mo/DrtOzzx9fKE0+MQreTC/HOm61ja3cWm4yI5G0W7bLTBBhsEoOzclycNK/QjeP+wYO+k11mtPM4SP4kCj3gh97g==";

    private PrepareSignatureServiceImpl testee = new PrepareSignatureServiceImpl();

    @Before
    public void init() {
        org.apache.xml.security.Init.init();
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    }

    @Test
    public void testBuildPreparedSignature() throws IOException {
        InputStream xmlResource = getXmlResource("classpath:/unsigned/unsigned-lisjp-i18n.xml");
        String xml = IOUtils.toString(xmlResource, StandardCharsets.UTF_8);
        IntygXMLDSignature intygXMLDSignature = testee.prepareSignature(xml, "9f02dd2f-f57c-4a73-8190-2fe602cd6e27",
            PartialSignatureFactory.DEFAULT_SIGNATURE_ALGORITHM);

        byte[] signature = createSignature(intygXMLDSignature.getSigningData().getBytes(StandardCharsets.UTF_8));
        SignatureValueType svt = new SignatureValueType();
        svt.setValue(signature);
        intygXMLDSignature.getSignatureType().setSignatureValue(svt);

        // Stuff KeyInfo
        String pubStr = null;
        try {
            pubStr = Base64.getEncoder().encodeToString(this.publicKey.getEncoded());
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        KeyInfoType keyInfo = new XMLDSigServiceImpl().buildKeyInfoForCertificate(pubStr);
        intygXMLDSignature.getSignatureType().setKeyInfo(keyInfo);

        try {
            String resXml = testee.encodeSignatureIntoSignedXml(intygXMLDSignature.getSignatureType(), xml);
            System.out.println(resXml);
            Assert.assertTrue(new XMLDSigServiceImpl().validateSignatureValidity(resXml, true).isValid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream getXmlResource(String source) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext()) {
            Resource resource = context.getResource(source);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private X509Certificate publicKey;

    private byte[] createSignature(byte[] digest) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource(FAKE_KEYSTORE_NAME).getInputStream(), FAKE_KEYSTORE_PASSWORD.toCharArray());

            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(FAKE_KEYSTORE_ALIAS,
                new KeyStore.PasswordProtection(FAKE_KEYSTORE_PASSWORD.toCharArray()));
            this.publicKey = (X509Certificate) ks.getCertificate(FAKE_KEYSTORE_ALIAS);
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
