/*
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.tools.anonymisering

import org.junit.Test

class AnonymiseraPersonIdTest {

    AnonymiseraPersonId anonymiseraPersonId = new AnonymiseraPersonId()

    @Test
    void anonymiseringGerInteSammaId() {
        String personId = "19121212-1212"
        String anonymiseradPersonId = anonymiseraPersonId.anonymisera(personId)
        assert personId != anonymiseradPersonId
    }

    @Test
    void anonymiseringGerEttSlumpmässigtResultat() {
        anonymiseraPersonId.random = [nextInt: {range->501}] as Random
        String personId = "19121212-1212"
        String förväntatAnonymiseratPersonId = "19121213-5014"
        String anonymiseradPersonId = anonymiseraPersonId.anonymisera(personId)
        assert förväntatAnonymiseratPersonId == anonymiseradPersonId
    }

    @Test
    void anonymiseringGerSammaResultatFleraGånger() {
        String personId = "19121212-1212"
        String anonymiseradPersonId1 = anonymiseraPersonId.anonymisera(personId)
        String anonymiseradPersonId2 = anonymiseraPersonId.anonymisera(personId)
        assert anonymiseradPersonId1 == anonymiseradPersonId2
    }

    @Test
    void anonymiseringGerOlikaResultatFörOlikaId() {
        String personId1 = "19121212-1212"
        String personId2 = "20101010-2010"
        String anonymiseradPersonId1 = anonymiseraPersonId.anonymisera(personId1)
        String anonymiseradPersonId2 = anonymiseraPersonId.anonymisera(personId2)
        assert anonymiseradPersonId1 != anonymiseradPersonId2
    }

    @Test
    void anonymiseringAvFelaktigtPersonnr() {
        String personId = "20110043-6904"
        String anonymiseradPersonId1 = anonymiseraPersonId.anonymisera(personId)
        String anonymiseradPersonId2 = anonymiseraPersonId.anonymisera(personId)
        assert personId == anonymiseradPersonId1
        assert anonymiseradPersonId1 == anonymiseradPersonId2
    }

    @Test
    void normaliseraLäggerTilBindestreck() {
        String personId1 = "191212121212"
        String personId2 = "20101010-2010"
        String normaliseradPersonId1 = anonymiseraPersonId.normalisera(personId1)
        String normaliseradPersonId2 = anonymiseraPersonId.normalisera(personId2)
        assert normaliseradPersonId1 == "19121212-1212"
        assert normaliseradPersonId2 == personId2
    }


    @Test
    void kontrollSiffra() {
        String personId1 = "101010201"
        String personId2 = "121212121"
        String personId3 = "101010281"
        int kontrollSiffraPersonId1 = anonymiseraPersonId.kontrollSiffra(personId1)
        int kontrollSiffraPersonId2 = anonymiseraPersonId.kontrollSiffra(personId2)
        int kontrollSiffraPersonId3 = anonymiseraPersonId.kontrollSiffra(personId3)
        assert kontrollSiffraPersonId1 == 8
        assert kontrollSiffraPersonId2 == 2
        assert kontrollSiffraPersonId3 == 0
    }

    @Test
    void behallKon() {
        String templatePersonId = "19980112-"
        for (int i = 1000; i < 1100;i++) {
            String personId = templatePersonId + i;
            String anonymiseratId = anonymiseraPersonId.anonymisera(personId);
            assert ((int)personId.charAt(personId.length() - 2)) % 2 == ((int)anonymiseratId.charAt(anonymiseratId.length() - 2)) % 2
        }
    }

    @Test
    void behallKonMedNormalisering() {
        String templatePersonId = "19980112"
        for (int i = 1000; i < 1100;i++) {
            String personId = templatePersonId + i;
            String anonymiseratId = anonymiseraPersonId.anonymisera(personId);
            assert ((int)personId.charAt(personId.length() - 2)) % 2 == ((int)anonymiseratId.charAt(anonymiseratId.length() - 2)) % 2
        }
    }
}
