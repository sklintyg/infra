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

class AnonymiseraHsaIdTest {

    AnonymiseraHsaId anonymiseraHsaId = new AnonymiseraHsaId()

    @Test
    void anonymiseringGerInteSammaId() {
        String hsaId = "123456789"
        String anonymiseradHsaId = anonymiseraHsaId.anonymisera(hsaId)
        assert hsaId != anonymiseradHsaId
    }

    @Test
    void anonymiseringGerSammaResultatFleraGånger() {
        String hsaId = "123456789"
        String anonymiseradHsaId1 = anonymiseraHsaId.anonymisera(hsaId)
        String anonymiseradHsaId2 = anonymiseraHsaId.anonymisera(hsaId)
        assert anonymiseradHsaId1 == anonymiseradHsaId2
    }

    @Test
    void anonymiseringGerOlikaResultatFörOlikaId() {
        String hsaId1 = "123456789"
        String hsaId2 = "987654321"
        String anonymiseradHsaId1 = anonymiseraHsaId.anonymisera(hsaId1)
        String anonymiseradHsaId2 = anonymiseraHsaId.anonymisera(hsaId2)
        assert anonymiseradHsaId1 != anonymiseradHsaId2
    }

}
