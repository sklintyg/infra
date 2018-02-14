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
package se.inera.intyg.infra.integration.nias.stub.util;

import org.apache.cxf.helpers.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class StubSignUtil {

    private StubSignUtil() {
    }

    public static RSAPublicKey loadPublicKey() {
        try {
            BufferedInputStream bis = loadResourceAsStream("public_key.der");

            X509EncodedKeySpec spec1 = new X509EncodedKeySpec(IOUtils.readBytesFromStream(bis));
            KeyFactory kf1 = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf1.generatePublic(spec1);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("Cannot load private key in stub: " + e.getMessage());
        }
    }

    public static RSAPrivateKey loadPrivateKey() {
        try {
            BufferedInputStream bis2 = loadResourceAsStream("private_key.der");
            byte[] privKeyBytes = IOUtils.readBytesFromStream(bis2);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalArgumentException("Cannot load private key in stub: " + e.getMessage());
        }
    }

    private static BufferedInputStream loadResourceAsStream(String name) throws IOException {
        ClassPathResource classPathResource2 = new ClassPathResource(name);
        return new BufferedInputStream(classPathResource2.getInputStream());
    }
}
