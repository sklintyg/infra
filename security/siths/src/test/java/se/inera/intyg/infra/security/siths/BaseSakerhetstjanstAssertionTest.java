/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.security.siths;

import org.apache.cxf.staxutils.StaxUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;

import javax.xml.transform.stream.StreamSource;

import static org.junit.Assert.assertEquals;

/**
 * @author erikl
 */
public class BaseSakerhetstjanstAssertionTest {

    private static Assertion assertionWithEnhet;

    @BeforeClass
    public static void readSamlAssertions() throws Exception {
        DefaultBootstrap.bootstrap();

        UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(Assertion.DEFAULT_ELEMENT_NAME);

        Document doc = StaxUtils.read(new StreamSource(new ClassPathResource(
                "UppdragslosIdpTest/assertion-1.xml").getInputStream()));
        assertionWithEnhet = (Assertion) unmarshaller.unmarshall(doc.getDocumentElement());

    }

    @Test
    public void testAssertionWithEnhetAndVardgivare() {

        BaseSakerhetstjanstAssertion assertion = new BaseSakerhetstjanstAssertion(assertionWithEnhet);
        assertEquals("TSTNMT2321000156-1024", assertion.getHsaId());
    }

}
