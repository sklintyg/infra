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
package se.inera.intyg.infra.integration.hsa.stub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Magnus Ekstrand on 2017-04-12.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FakeProperties {

    private String displayOrder;
    private String env;
    private boolean readOnly = false;

    private List<String> allowedInApplications = new ArrayList<>();
    private List<FakeLogins> logins = new ArrayList<FakeLogins>();

    private Map<String, String> extraContextProperties = new HashMap<>();

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

    public List<String> getAllowedInApplications() {
        return allowedInApplications;
    }

    public void setAllowedInApplications(List<String> allowedInApplications) {
        this.allowedInApplications = allowedInApplications;
    }

    public Map<String, String> getExtraContextProperties() {
        return extraContextProperties;
    }

    public void setExtraContextProperties(Map<String, String> extraContextProperties) {
        this.extraContextProperties = extraContextProperties;
    }
}
