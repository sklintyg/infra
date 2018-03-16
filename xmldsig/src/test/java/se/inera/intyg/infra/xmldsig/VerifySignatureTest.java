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

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import se.inera.intyg.infra.xmldsig.factory.PartialSignatureFactory;
import se.inera.intyg.infra.xmldsig.model.SignatureType;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.Assert.assertTrue;

public class VerifySignatureTest {

    private static final String x509cert = "-----BEGIN CERTIFICATE-----\nMIIB+zCCAWQCCQCUxqAHHrhg+jANBgkqhkiG9w0BAQsFADBCMQswCQYDVQQGEwJTRTELMAkGA1UECAwCVkcxEzARBgNVBAcMCkdvdGhlbmJ1cmcxETAPBgNVBAoMCENhbGxpc3RhMB4XDTE4MDMxMDIwMDY0MFoXDTIxMTIwNDIwMDY0MFowQjELMAkGA1UEBhMCU0UxCzAJBgNVBAgMAlZHMRMwEQYDVQQHDApHb3RoZW5idXJnMREwDwYDVQQKDAhDYWxsaXN0YTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA4cB6VC0f9ne0UKC/XzsoP5ocv7WyGt5378f/DGnVAF3aWzderzLnXMqSdGbLOuEzUUdbjYgQkqQSs6wy872KLf0RzQzllxwpBQJ/2r+CrW6tROJa0FYEIhgWDdRGlS+9+hd3E9Ilz2PTZDF4c1C+ 4l/xq149OCgiAGfadeBZA5MCAwEAATANBgkqhkiG9w0BAQsFAAOBgQDU+Mrw98Qm8K0U8A208Ee0 1PZeIpqC9CIRIXJd0PFwXJjTlGIWckwrdsgbGtwOAlA2rzAx/FUhQD4/1F4G5mo/DrtOzzx9fKE0 +MQreTC/HOm61ja3cWm4yI5G0W7bLTBBhsEoOzclycNK/QjeP+wYO+k11mtPM4SP4kCj3gh97g==\n-----END CERTIFICATE-----";

    @Before
    public void init() {
        org.apache.xml.security.Init.init();
    }

    @Test
    public void testVerifySignature() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(new ByteArrayInputStream(x509cert.getBytes("UTF-8")));
            RSAPublicKey rsaPublicKey = (RSAPublicKey) cert.getPublicKey();
            System.out.println("PubKey: " + rsaPublicKey.getAlgorithm());
            System.out.println("PubKey: " + rsaPublicKey.getFormat());

            String signatureData = "HWYgpenlEQPVKqO0KExAoSJuAGASCOvyqoxgKU3uP1YJza+04bctAfLNG8DhHksEhWmJxQtM9EVeKS7iXM/lP1h7Ky2YC5W59gfjXPStw56xubfbQthcCnLxs7+vpT7lW+xD14ifj82JmSDII/b5xJpWPLWa6z8XnHe/0H3btyE=";

            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(rsaPublicKey);
            verifier.update("LV2Zi4O2uAo9M8MvoPkIHSqUPJSl9C5j2ayJlcAR5HY=".getBytes("UTF-8"));
            boolean isValid = verifier.verify(Base64.getDecoder().decode(signatureData));
            System.out.println("Is valid: " + isValid);
        } catch (Exception e) {
            System.err.println("Error validating: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testValidateRaw() {

        SignatureType sigType = PartialSignatureFactory.buildSignature();
        sigType.getSignedInfo().getReference().get(0).setDigestValue("WgHuGKMXQjmTYaIF9+J9L8e/p4UMtjAtM6ibCRA95VE=".getBytes());
        boolean result = validateSignature(sigType,
                "ixz14VxDRAy+R/7M4kUq9pMkCm+Q9ZBL0KVYPHLnpI6dtKwfZJgpQBmqyDbfjMpBiZVUoXCK7/gftCGmiKB4HK4VqQ+Rn73GQFxpgwzq0nx6KScEEoDsYCyFs/4/0MrDUVuujIhtIul3WSGzXNBdtZFr4SDrwdwUuqreIqz4cz8=");

        assertTrue(result);
    }

    private boolean validateSignature(SignatureType signatureType, String rawSignature) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource("keystore.jks").getInputStream(), "12345678".toCharArray());

            PublicKey loadedPublicKey = ks.getCertificate("1").getPublicKey();

            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(loadedPublicKey);
            verifier.update(signatureType.getSignedInfo().getReference().get(0).getDigestValue());   // WRONG!!!
            return verifier.verify(Base64.getDecoder().decode(rawSignature.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    // libOiMeeGDBbLRzbHVnXsiGfU59fI8H2MoUCUl1R05mUSKXGfwZrWiLqG2S4v3nC2jwBaA2lQSULsBBevZo6WfYlVCRyUtcE/Om0JsJtGkCPowhJHehIjaOMwykLA15tQOqMO6VVv+ZpIGakh+tr1pPMj1FnemzlDbMcG//ubanHdR+/G2IOYJco47PxNF2kwJX7AYKF255FyfLgI00Pvl/E0ItymI16wz2leAmASlSqbbZ7FeC4+lXh0+M/eYo5FVfuiybEqus8MN9gdIZ+WO9aE0GRtih5k3DwkTteT4KIWeKD5tevQiV3/3fdT5kLSDx/p7tOuQa0Xp9ekj1B5g==

    private boolean validateSignature(SignatureType signatureType, String rawSignature, String certificate) {
        try {
            // X509EncodedKeySpec spec1 = new X509EncodedKeySpec(
            // IOUtils.toByteArray(new ByteArrayInputStream(Base64.getDecoder().decode(publicKeyString))));

            // KeyFactory kf1 = KeyFactory.getInstance("RSA");
            // PublicKey loadedPublicKey = (PublicKey) kf1.generatePublic(spec1);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(new ByteArrayInputStream(certificate.getBytes("UTF-8")));
            RSAPublicKey loadedPublicKey = (RSAPublicKey) cert.getPublicKey();

            System.out.println("  modulus: " + loadedPublicKey.getModulus());
            // Signature verifier = Signature.getInstance("SHA256withRSA");
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(loadedPublicKey);
            verifier.update(signatureType.getSignedInfo().getReference().get(0).getDigestValue());    // WRONG!!!
            return verifier.verify(Base64.getDecoder().decode(rawSignature.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    @Test
    public void testCreateSignature() {
        String digest = "XP7vTFaPaH+/6EJZlfejZfxxwMcTgrdQafEELWSY8Wk=";
        createSignature(digest);
    }

    private void createSignature(String digest) {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource("keystore.jks").getInputStream(), "12345678".toCharArray());

            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("1",
                    new KeyStore.PasswordProtection("12345678".toCharArray()));

            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(keyEntry.getPrivateKey());
            rsa.update(Base64.getDecoder().decode(digest));
            byte[] signatureBytes = rsa.sign();

            System.out.println("Encoded: " + Base64.getEncoder().encodeToString(signatureBytes));
        } catch (Exception e) {
            throw new IllegalStateException("Not possible to sign digest: " + e.getMessage());
        }
    }
}
