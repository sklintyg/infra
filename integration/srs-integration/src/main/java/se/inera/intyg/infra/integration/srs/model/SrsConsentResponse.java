package se.inera.intyg.infra.integration.srs.model;

import se.inera.intyg.clinicalprocess.healthcond.srs.getconsent.v1.Samtyckesstatus;

import java.time.LocalDateTime;

public class SrsConsentResponse {
    private Samtyckesstatus status;
    private boolean samtycke;
    private LocalDateTime sparatTidpunkt;

    public SrsConsentResponse(Samtyckesstatus status, boolean samtycke, LocalDateTime sparatTidpunkt) {
        this.samtycke = samtycke;
        this.status = status;
        this.sparatTidpunkt = sparatTidpunkt;
    }

    public Samtyckesstatus getStatus() {
        return status;
    }

    public boolean isSamtycke() {
        return samtycke;
    }

    public LocalDateTime getSparatTidpunkt() {
        return sparatTidpunkt;
    }
}
