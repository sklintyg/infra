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
package se.inera.intyg.infra.integration.api.model;

import java.io.Serializable;
import lombok.Getter;
import se.inera.intyg.schemas.contract.Personnummer;

@Getter
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Personnummer personnummer;
    private final boolean sekretessmarkering;
    private final boolean avliden;
    private final String fornamn;
    private final String mellannamn;
    private final String efternamn;
    private final String postadress;
    private final String postnummer;
    private final String postort;
    private final boolean testIndicator;

    // CHECKSTYLE:OFF ParameterNumber
    public Person(Personnummer personnummer, boolean sekretessmarkering, boolean avliden,
        String fornamn, String mellannamn,
        String efternamn, String postadress, String postnummer, String postort) {
        this.personnummer = personnummer;
        this.sekretessmarkering = sekretessmarkering;
        this.avliden = avliden;
        this.fornamn = fornamn;
        this.mellannamn = mellannamn;
        this.efternamn = efternamn;
        this.postadress = postadress;
        this.postnummer = postnummer;
        this.postort = postort;
        this.testIndicator = false;
    }

    // For backward compatibility a new constructor is added with the testIndicator argument.
    public Person(Personnummer personnummer, boolean sekretessmarkering, boolean avliden,
        String fornamn, String mellannamn,
        String efternamn, String postadress, String postnummer, String postort,
        boolean testIndicator) {
        this.personnummer = personnummer;
        this.sekretessmarkering = sekretessmarkering;
        this.avliden = avliden;
        this.fornamn = fornamn;
        this.mellannamn = mellannamn;
        this.efternamn = efternamn;
        this.postadress = postadress;
        this.postnummer = postnummer;
        this.postort = postort;
        this.testIndicator = testIndicator;
    }
    // CHECKSTYLE:ON ParameterNumber

}