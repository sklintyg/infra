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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

/**
 * This class doesn't actually test production code, it just validates that the algorithms we
 * use for digests produce expected values given SecMaker documentation.
 */
public class DigestTest {

    private static final String INTYG_DATA = "Data To Be Signed";
    public static final String EXPECTED_SHA1_DIGEST = "PXLVCJ1DFmrG6OQa4RZ6dMd+0Z4=";
    public static final String EXPECTED_SHA256_DIGEST = "6rHZDWzIBQC4xksvOS0xzXgitPn+4EgJpunODzpWaSo=";
    public static final String DATA_TO_BE_SIGNED = "Data To Be Signed";

    private String original_sha1 = "3d72d5089d43166ac6e8e41ae1167a74c77ed19e";
    private String original_sha256 = "eab1d90d6cc80500b8c64b2f392d31cd7822b4f9fee04809a6e9ce0f3a56692a";

    /**
     *  Note: We have "proof" that the 3d72d5089d43166ac6e8e41ae1167a74c77ed19e shall digest and base64-encode
     * into PXLVCJ1DFmrG6OQa4RZ6dMd+0Z4= from SecMaker documentation.
     * To transform "Data To Be Signed".getBytes() into original_sha1 do:
     * <code>
     * String rebuilt = Hex.encodeHex(MessageDigest.digest("Data To Be Signed").getBytes())
     * rebuilt.equals(original_sha1) == true
     * </code>
     */
    @Test
    public void testDigestSha1() throws NoSuchAlgorithmException, DecoderException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

        byte[] data = messageDigest.digest(DATA_TO_BE_SIGNED.getBytes("UTF-8"));

        // Assert that the hex-encoded string rep is equal to the example string from SecMaker docs
        assertEquals(original_sha1, new String(Hex.encodeHex(messageDigest.digest(DATA_TO_BE_SIGNED.getBytes()))));

        // Lexically, the example string in uppercase is equal to
        assertEquals(original_sha1.toUpperCase(), DatatypeConverter.printHexBinary(data));

        assertEquals(EXPECTED_SHA1_DIGEST, new String(java.util.Base64.getEncoder().encode(data)));
    }

    @Test
    public void testDigestSha1FailsWhenNotNormalized() throws NoSuchAlgorithmException, DecoderException, UnsupportedEncodingException {

        byte[] data = original_sha1.getBytes();

        assertEquals(EXPECTED_SHA1_DIGEST, new String(java.util.Base64.getEncoder().encode(data)));
    }

    @Test
    public void testDigestSha64() throws NoSuchAlgorithmException, DecoderException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] data = messageDigest.digest(INTYG_DATA.getBytes("UTF-8"));
        assertEquals(original_sha256.toUpperCase(), DatatypeConverter.printHexBinary(data));
        assertEquals(EXPECTED_SHA256_DIGEST, new String(java.util.Base64.getEncoder().encode(data)));
    }
}
