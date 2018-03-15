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
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class XMLDSigServiceImplTest {

    private XMLDSigServiceImpl testee = new XMLDSigServiceImpl();

    @Before
    public void init() {
        testee.init();
    }


    // Use this test to manually test signed documents.
    // @Test
    public void testValidateSignature() throws IOException, JAXBException {

        InputStream xmlResourceInputStream = getXmlResource("classpath:/netid-test/simple_after_netid_sign.xml");
        String xml = IOUtils.toString(xmlResourceInputStream);
        String canonXml = testee.canonicalizeXml(xml);

        boolean result = testee.validateSignatureValidity(canonXml);
        assertTrue(result);
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