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
