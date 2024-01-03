/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.xml.transform.stream.StreamSource;
import org.apache.cxf.staxutils.StaxUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * @author erikl
 */
public class BaseSakerhetstjanstAssertionTest {

    public static final String HSA_ID = "TSTNMT2321000156-1024";
    private static final String IDENTITY_PROVIDER_FOR_SIGN = "https://idp.ineratest.org:443/saml/sign/sithseid-other-device";
    private Assertion assertionWithEnhet;

    @Nested
    class AssertionWithoutIdpForSigning {

        @BeforeEach
        void readSamlAssertions() throws Exception {
            assertionWithEnhet = getAssertionWithEnhet("UppdragslosIdpTest/assertion-1.xml");
        }

        @Test
        void testAssertionWithEnhetAndVardgivare() {

            BaseSakerhetstjanstAssertion assertion = new BaseSakerhetstjanstAssertion(assertionWithEnhet);
            assertEquals(HSA_ID, assertion.getHsaId());
        }

        @Test
        void shallExcludeIdpForSigning() {
            final var assertion = new BaseSakerhetstjanstAssertion(assertionWithEnhet);
            assertNull(assertion.getIdentityProviderForSign());
        }
    }

    @Nested
    class AssertionWithIdpForSigning {

        @BeforeEach
        void readSamlAssertions() throws Exception {
            assertionWithEnhet = getAssertionWithEnhet("UppdragslosIdpTest/assertion-2.xml");
        }

        @Test
        void shallincludeHsaId() {
            BaseSakerhetstjanstAssertion assertion = new BaseSakerhetstjanstAssertion(assertionWithEnhet);
            assertEquals(HSA_ID, assertion.getHsaId());
        }

        @Test
        void shallIncludeIdpForSigning() {
            final var assertion = new BaseSakerhetstjanstAssertion(assertionWithEnhet);
            assertEquals(IDENTITY_PROVIDER_FOR_SIGN, assertion.getIdentityProviderForSign());
        }
    }

    private Assertion getAssertionWithEnhet(String assertionFile) throws Exception {
        DefaultBootstrap.bootstrap();

        final var unmarshallerFactory = Configuration.getUnmarshallerFactory();
        final var unmarshaller = unmarshallerFactory.getUnmarshaller(Assertion.DEFAULT_ELEMENT_NAME);

        final var doc = StaxUtils.read(new StreamSource(new ClassPathResource(assertionFile).getInputStream()));
        return (Assertion) unmarshaller.unmarshall(doc.getDocumentElement());
    }
}
