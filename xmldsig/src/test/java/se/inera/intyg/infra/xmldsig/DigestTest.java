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
import org.springframework.core.io.Resource;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

/**
 * This class doesn't actually test production code, it just validates that the algorithms we
 * use for digests produce expected values given SecMaker documentation.
 */
public class DigestTest {

    private  String canon =  "<ns2:intyg xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\"><intygs-id xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">TSTNMT2321000156-1077</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">b6a106a1-7056-417f-9065-356bdb73d2f3</ns3:extension></intygs-id><typ xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><ns3:code xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">LISJP</ns3:code><ns3:codeSystem xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">b64ea353-e8f6-4832-b563-fc7d46f29548</ns3:codeSystem><ns3:displayName xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">Läkarintyg för sjukpenning</ns3:displayName></typ><version xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\">1.0</version><signeringstidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\">2018-03-09T17:20:27</signeringstidpunkt><skickatTidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\">2018-03-09T17:20:27</skickatTidpunkt><patient xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><person-id xmlns=\"\"><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.3.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">191212121212</ns3:extension></person-id><fornamn xmlns=\"\"/><efternamn xmlns=\"\"/><postadress xmlns=\"\"/><postnummer xmlns=\"\"/><postort xmlns=\"\"/></patient><skapadAv xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><personal-id xmlns=\"\"><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.4.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">TSTNMT2321000156-1079</ns3:extension></personal-id><fullstandigtNamn xmlns=\"\">Arnold Johansson</fullstandigtNamn><forskrivarkod xmlns=\"\">0000000</forskrivarkod><enhet xmlns=\"\"><enhets-id><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.4.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">TSTNMT2321000156-1077</ns3:extension></enhets-id><arbetsplatskod><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.29.4.71</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1234567890</ns3:extension></arbetsplatskod><enhetsnamn>NMT vg3 ve1</enhetsnamn><postadress>NMT gata 3</postadress><postnummer>12345</postnummer><postort>Testhult</postort><telefonnummer>0101112131416</telefonnummer><epost>enhet3@webcert.invalid.se</epost><vardgivare><vardgivare-id><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.4.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">TSTNMT2321000156-102Q</ns3:extension></vardgivare-id><vardgivarnamn>NMT vg3</vardgivarnamn></vardgivare></enhet></skapadAv><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"27\"><delsvar xmlns=\"\" id=\"27.1\">true</delsvar></svar><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"6\"><delsvar xmlns=\"\" id=\"6.2\"><ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"><ns3:code>J22</ns3:code><ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem><ns3:displayName>Icke specificerad akut infektion i nedre luftvägarna</ns3:displayName></ns3:cv></delsvar><delsvar xmlns=\"\" id=\"6.1\">Icke specificerad akut infektion i nedre luftvägarna</delsvar></svar><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"32\"><instans xmlns=\"\">1</instans><delsvar xmlns=\"\" id=\"32.1\"><ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"><ns3:code>EN_FJARDEDEL</ns3:code><ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem><ns3:displayName>25%</ns3:displayName></ns3:cv></delsvar><delsvar xmlns=\"\" id=\"32.2\"><ns3:datePeriod xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"><ns3:start>2018-03-09</ns3:start><ns3:end>2018-04-05</ns3:end></ns3:datePeriod></delsvar></svar><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"26\"><delsvar xmlns=\"\" id=\"26.1\">false</delsvar></svar></ns2:intyg>";

    private static final String INTYG_DATA = "Data To Be Signed";
    public static final String EXPECTED_SHA1_DIGEST = "PXLVCJ1DFmrG6OQa4RZ6dMd+0Z4=";
    public static final String EXPECTED_SHA256_DIGEST = "6rHZDWzIBQC4xksvOS0xzXgitPn+4EgJpunODzpWaSo=";
    public static final String DATA_TO_BE_SIGNED = "Data To Be Signed";

    private String original_sha1 = "3d72d5089d43166ac6e8e41ae1167a74c77ed19e";
    private String original_sha256 = "eab1d90d6cc80500b8c64b2f392d31cd7822b4f9fee04809a6e9ce0f3a56692a";

    @Before
    public void init() {
        org.apache.xml.security.Init.init();
    }

    /**
     * Note: We have "proof" that the 3d72d5089d43166ac6e8e41ae1167a74c77ed19e shall digest and base64-encode
     * into PXLVCJ1DFmrG6OQa4RZ6dMd+0Z4= from SecMaker documentation.
     * To transform "Data To Be Signed".getBytes() into original_sha1 do:
     * <code>
     * String rebuilt = Hex.encodeHex(MessageDigest.digest("Data To Be Signed").getBytes())
     * rebuilt.equals(original_sha1) == true
     * </code>
     */
    @Test
    public void testDigestSha1() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

        byte[] data = messageDigest.digest(DATA_TO_BE_SIGNED.getBytes("UTF-8"));

        // Lexically, the example string in uppercase is equal to
        assertEquals(original_sha1.toUpperCase(), DatatypeConverter.printHexBinary(data));

        assertEquals(EXPECTED_SHA1_DIGEST, new String(java.util.Base64.getEncoder().encode(data)));
    }

    @Test
    public void testDigestSha64() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] data = messageDigest.digest(INTYG_DATA.getBytes("UTF-8"));
        assertEquals(original_sha256.toUpperCase(), DatatypeConverter.printHexBinary(data));
        assertEquals(EXPECTED_SHA256_DIGEST, new String(java.util.Base64.getEncoder().encode(data)));
    }


    @Test
    public  void testDigest() throws IOException {

        String digest = new XMLDSigServiceImpl().digestToBase64(canon);
        System.out.println(digest);
        System.out.println(Base64.getEncoder().encodeToString(digest.getBytes()));

        InputStream xmlResource = getXmlResource("unsigned/signed-lisjp.xml");
        String purexml = IOUtils.toString(xmlResource);
        String canonxml = new XMLDSigServiceImpl().canonicalizeXml(purexml);
        String digested = new XMLDSigServiceImpl().digestToBase64(canonxml);
        System.out.println(digested);
        System.out.println(Base64.getEncoder().encodeToString(digested.getBytes()));

    }

    private InputStream getXmlResource(String source) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext()) {
            Resource resource = context.getResource(source);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


}
