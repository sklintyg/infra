/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.nias.stub.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

public final class StubSignUtil {

    private static final Logger LOG = LoggerFactory.getLogger(StubSignUtil.class);

    private StubSignUtil() {
    }

    public static Keys loadFromKeystore() {
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource("keystore.jks").getInputStream(), "12345678".toCharArray());

            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("1",
                    new KeyStore.PasswordProtection("12345678".toCharArray()));
            return new Keys((RSAPrivateKey) keyEntry.getPrivateKey(), (X509Certificate) ks.getCertificate("1"));

        } catch (KeyStoreException | UnrecoverableEntryException | CertificateException | NoSuchAlgorithmException | IOException e) {
            LOG.error("Error loading fake signing keys from keystore.jks: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
