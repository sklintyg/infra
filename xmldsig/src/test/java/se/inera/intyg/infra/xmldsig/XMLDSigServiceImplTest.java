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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import se.inera.intyg.infra.xmldsig.model.CertificateInfo;
import se.inera.intyg.infra.xmldsig.model.ValidationResponse;
import se.inera.intyg.infra.xmldsig.service.XMLDSigServiceImpl;

public class XMLDSigServiceImplTest {

    private XMLDSigServiceImpl testee = new XMLDSigServiceImpl();

    @Before
    public void init() {
        testee.init();
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
    }

    // Use this test to manually test signed documents.
    @Test
    public void testValidateSignature() throws IOException {

        InputStream xmlResourceInputStream = getXmlResource("classpath:/signed/signed-lisjp-i18n.xml");
        String xml = IOUtils.toString(xmlResourceInputStream, Charset.forName("UTF-8"));

        ValidationResponse response = testee.validateSignatureValidity(xml, true);
        assertTrue(response.isValid());
    }

    @Test
    public void testValidateSignatureAfterStoreInIT() throws IOException {

        InputStream xmlResourceInputStream = getXmlResource("classpath:/signed/signed-after-store-in-intygstjansten.xml");
        String xml = IOUtils.toString(xmlResourceInputStream, Charset.forName("UTF-8"));

        ValidationResponse response = testee.validateSignatureValidity(xml, true);
        assertTrue(response.isValid());
    }

    @Test
    public void testValidateSignatureAfterStoreInITI18n() throws IOException {

        InputStream xmlResourceInputStream = getXmlResource("classpath:/signed/signed-after-store-i18n.xml");
        String xml = IOUtils.toString(xmlResourceInputStream, Charset.forName("UTF-8"));

        ValidationResponse response = testee.validateSignatureValidity(xml, true);
        assertTrue(response.isValid());
    }


    @Test
    public void testValidateSignatureInListCertificatesForCare() throws IOException {

        InputStream xmlResourceInputStream = getXmlResource("classpath:/signed/list-certificates-for-care-response.xml");
        String xml = IOUtils.toString(xmlResourceInputStream, Charset.forName("UTF-8"));

        ValidationResponse response = testee.validateSignatureValidity(xml, true);
        assertTrue(response.isValid());
    }

    @Test
    public void testFromIntygstjanstenNewSchema() throws IOException {

        InputStream xmlResourceInputStream = getXmlResource("classpath:/signed/signed-after-new-schema.xml");
        String xml = IOUtils.toString(xmlResourceInputStream, Charset.forName("UTF-8"));

        ValidationResponse response = testee.validateSignatureValidity(xml, true);
        assertTrue(response.isValid());
    }

    @Test
    public void testExtractCertificateInfo() throws IOException {
        InputStream xmlResourceInputStream = getXmlResource("classpath:/signed/signed-after-store-i18n.xml");
        String xml = IOUtils.toString(xmlResourceInputStream, Charset.forName("UTF-8"));
        Map<String, CertificateInfo> map = testee.extractCertificateInfo(xml);
        assertNotNull(map);
    }

    private InputStream getXmlResource(String location) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext()) {
            Resource resource = context.getResource(location);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
