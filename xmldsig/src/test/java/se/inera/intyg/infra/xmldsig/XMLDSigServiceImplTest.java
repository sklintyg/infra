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
import se.inera.intyg.infra.xmldsig.model.ReferenceType;
import se.inera.intyg.infra.xmldsig.model.SignatureType;
import se.inera.intyg.infra.xmldsig.model.SignedInfoType;
import se.inera.intyg.infra.xmldsig.model.X509DataType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Transform;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class XMLDSigServiceImplTest {

    private static final String EXPECTED_DIGEST = "+2khtln+nX9ktnZTekxpHplpTlPRKgMJt2WJqYhDa9I=";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String INTYG_XML_LOCATION = "classpath:/unsigned/lisjp.xml";

    private XMLDSigServiceImpl testee = new XMLDSigServiceImpl();

    @Before
    public void init() {
        testee.init();
    }

    @Test
    public void testBuildPartialSignatureWithDigest() throws IOException {
        InputStream xmlResourceInputStream = getXmlResource();
        SignatureType signatureType = testee.prepareSignature(IOUtils.toString(xmlResourceInputStream));
        SignedInfoType signedInfo = signatureType.getSignedInfo();

        ReferenceType referenceType = signedInfo.getReference().get(0);

        byte[] digestValue = referenceType.getDigestValue();

        assertEquals(EXPECTED_DIGEST, new String(digestValue, UTF8));
        assertTrue(Arrays.equals(EXPECTED_DIGEST.getBytes(), digestValue));
        assertEquals(CanonicalizationMethod.EXCLUSIVE_WITH_COMMENTS, signedInfo.getCanonicalizationMethod().getAlgorithm());
        assertEquals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", signedInfo.getSignatureMethod().getAlgorithm());
        assertEquals(DigestMethod.SHA256, referenceType.getDigestMethod().getAlgorithm());
        assertEquals(Transform.ENVELOPED, referenceType.getTransforms().getTransform().get(0).getAlgorithm());
    }

    @Test
    public void testBuildPartialSignatureCheckKeyInfo() throws IOException, JAXBException {
        InputStream xmlResourceInputStream = getXmlResource();
        SignatureType signatureType = testee.prepareSignature(IOUtils.toString(xmlResourceInputStream));
        JAXBContext jc = JAXBContext.newInstance(SignatureType.class, X509DataType.class);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.marshal(signatureType, System.out);
    }

    @Test
    public void testValidate() throws IOException, JAXBException {
        InputStream xmlResourceInputStream = getXmlResource();
        SignatureType signatureType = testee.prepareSignature(IOUtils.toString(xmlResourceInputStream));
        testee.validate(signatureType);
    }

    private InputStream getXmlResource() {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext()) {
            Resource resource = context.getResource(INTYG_XML_LOCATION);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
