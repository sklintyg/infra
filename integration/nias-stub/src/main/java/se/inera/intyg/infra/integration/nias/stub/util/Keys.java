/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.nias.stub.util;

import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;

public class Keys {

    private RSAPrivateKey privateKey;
    private X509Certificate x509Certificate;

    public Keys(RSAPrivateKey privateKey, X509Certificate x509Certificate) {
        this.privateKey = privateKey;
        this.x509Certificate = x509Certificate;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public X509Certificate getX509Certificate() {
        return x509Certificate;
    }
}
