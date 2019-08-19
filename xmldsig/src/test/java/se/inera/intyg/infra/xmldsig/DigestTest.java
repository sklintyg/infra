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
package se.inera.intyg.infra.xmldsig;

import org.junit.Test;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

public class DigestTest {

    @Test
    public void digestTest() {
        String data = "Data To Be Signed";
        String base64Digest  = sha256AsBase64(data);
        assertEquals("PXLVCJ1DFmrG6OQa4RZ6dMd+0Z4=", base64Digest);
    }

    private String sha256AsBase64(String signingData) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            sha.update(signingData.getBytes(Charset.forName("UTF-8")));
            return Base64.getEncoder().encodeToString(sha.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Unable to digest signingData");
    }
}
