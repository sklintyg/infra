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
package se.inera.intyg.infra.xmldsig.model;


import org.w3._2000._09.xmldsig_.SignatureType;

public class IntygXMLDSignature implements IntygSignature {

    private SignatureType signatureType;
    private String digestedXml;
    private String signedInfoForSigning;

    public IntygXMLDSignature(SignatureType signatureType, String digestedXml, String signedInfoForSigning) {
        this.signatureType = signatureType;
        this.digestedXml = digestedXml;
        this.signedInfoForSigning = signedInfoForSigning;
    }

    public SignatureType getSignatureType() {
        return signatureType;
    }

    @Override
    public String getOriginalXml() {
        return digestedXml;
    }

    @Override
    public String getSigningData() {
        return signedInfoForSigning;
    }

}
