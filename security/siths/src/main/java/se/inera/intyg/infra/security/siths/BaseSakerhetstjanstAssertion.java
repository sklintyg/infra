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

import java.util.ArrayList;
import java.util.List;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.impl.XSAnyImpl;
import org.springframework.security.saml.SAMLCredential;

/**
 * This SAML-assertion is adapted for "uppdragslös inloggning", specifying only employeeHsaId.
 */
@SuppressWarnings("FieldMayBeFinal")
public class BaseSakerhetstjanstAssertion {

    // Användarens HSA-ID.
    public static final String HSA_ID_ATTRIBUTE = "http://sambi.se/attributes/1/employeeHsaId";

    // Användarens HSA-ID, legacy.
    public static final String HSA_ID_ATTRIBUTE_LEGACY = "urn:sambi:names:attribute:employeeHsaId";


    private String hsaId;
    private String authenticationScheme;


    /* Constructor taking an Assertion object */
    public BaseSakerhetstjanstAssertion(Assertion assertion) {
        if (assertion.getAttributeStatements() != null) {
            for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
                extractAttributes(attributeStatement.getAttributes());
            }
        }

        if (!assertion.getAuthnStatements().isEmpty()) {
            authenticationScheme = assertion.getAuthnStatements().get(0).getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef();
        }
    }

    // - - - - -  Static - - - - -

    public static BaseSakerhetstjanstAssertion getAssertion(SAMLCredential credential) {
        return new BaseSakerhetstjanstAssertion(credential.getAuthenticationAssertion());
    }



    // - - - - -  Getters and setters - - - - -

    public String getHsaId() {
        return hsaId;
    }

   public String getAuthenticationScheme() {
        return authenticationScheme;
    }


    // - - - - - Private scope - - - - -

    private void extractAttributes(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            switch (attribute.getName()) {

                case HSA_ID_ATTRIBUTE:
                    hsaId = getValue(attribute);
                    break;
                case HSA_ID_ATTRIBUTE_LEGACY:
                    // Only set if other not already set.
                    String val = getValue(attribute);
                    if (val != null && hsaId == null) {
                        hsaId = val;
                    }
                    break;
                default:
                    // Ignore.
            }
        }
    }

    private String getValue(Attribute attribute) {
        List<String> values = getValues(attribute);
        return values.isEmpty() ? null : values.get(0);
    }

    private List<String> getValues(Attribute attribute) {
        List<String> values = new ArrayList<>();
        if (attribute.getAttributeValues() == null) {
            return values;
        }
        for (XMLObject xmlObject : attribute.getAttributeValues()) {
            if (xmlObject.getDOM() != null) {
                values.add(xmlObject.getDOM().getTextContent());
            } else if (xmlObject instanceof XSAnyImpl) {
                values.add(((XSAnyImpl) xmlObject).getTextContent());
            }
        }
        return values;
    }

}
