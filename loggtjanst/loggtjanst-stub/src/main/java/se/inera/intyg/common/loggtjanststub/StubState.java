package se.inera.intyg.common.loggtjanststub;

/**
 * Created by eriklupander on 2016-03-22.
 */
public class StubState {

    private boolean active = true;
    private boolean fakeError = false;
    private long artificialLatency = 0L;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isFakeError() {
        return fakeError;
    }

    public void setFakeError(boolean fakeError) {
        this.fakeError = fakeError;
    }

    public long getArtificialLatency() {
        return artificialLatency;
    }

    public void setArtificialLatency(long artificialLatency) {
        this.artificialLatency = artificialLatency;
    }
}
