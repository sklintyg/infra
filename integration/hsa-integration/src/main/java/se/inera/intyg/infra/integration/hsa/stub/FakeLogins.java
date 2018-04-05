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
package se.inera.intyg.infra.integration.hsa.stub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Magnus Ekstrand on 2017-04-12.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FakeLogins {

    private String forvaldEnhet;
    private String beskrivning;

    public FakeLogins() {
        // Needed for deserialization
    }

    public String getForvaldEnhet() {
        return forvaldEnhet;
    }

    public void setForvaldEnhet(String forvaldEnhet) {
        this.forvaldEnhet = forvaldEnhet;
    }

    public String getBeskrivning() {
        return beskrivning;
    }

    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }
}
