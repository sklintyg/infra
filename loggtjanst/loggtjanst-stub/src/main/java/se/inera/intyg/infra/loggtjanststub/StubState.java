/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.loggtjanststub;

/**
 * Created by eriklupander on 2016-03-22.
 */
public class StubState {

    private boolean active = true;
    private boolean fakeError = false;
    private long artificialLatency = 0L;
    private ErrorState errorState = ErrorState.NONE;

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

    public ErrorState getErrorState() {
        return errorState;
    }

    public void setErrorState(ErrorState errorState) {
        this.errorState = errorState;
    }
}
