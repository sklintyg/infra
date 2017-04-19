/**
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
 * <p>
 * This file is part of sklintyg (https://github.com/sklintyg).
 * <p>
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.integration.hsa.stub;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Magnus Ekstrand on 2017-04-12.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FakeProperties {

    private String displayOrder;
    private String env;
    private boolean readOnly = false;

    private List<FakeLogins> logins = new ArrayList<FakeLogins>();

    public FakeProperties() {
        // Needed for deserialization
    }

    public String getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(String displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @JsonIgnore
    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public List<FakeLogins> getLogins() {
        return logins;
    }

    public void setLogins(List<FakeLogins> logins) {
        this.logins = logins;
    }
}
