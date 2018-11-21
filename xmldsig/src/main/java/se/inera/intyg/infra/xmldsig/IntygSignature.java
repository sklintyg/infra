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

import se.inera.intyg.infra.xmldsig.model.SignatureType;

public class IntygSignature {

    private SignatureType signatureType;
    private String digestedXml;
    private String signedInfoForSigning;

    public IntygSignature(SignatureType signatureType, String digestedXml, String signedInfoForSigning) {
        this.signatureType = signatureType;
        this.digestedXml = digestedXml;
        this.signedInfoForSigning = signedInfoForSigning;
    }

    public SignatureType getSignatureType() {
        return signatureType;
    }

    public String getDigestedXml() {
        return digestedXml;
    }

    public String getSignedInfoForSigning() {
        return signedInfoForSigning;
    }
}
