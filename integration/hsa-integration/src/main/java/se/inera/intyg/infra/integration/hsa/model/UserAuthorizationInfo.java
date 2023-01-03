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
package se.inera.intyg.infra.integration.hsa.model;

import java.util.List;
import java.util.Map;

/**
 * Composite class encapsulating response data from GetAuthorizationsForPerson.
 *
 * Created by eriklupander on 2016-05-20.
 */
public class UserAuthorizationInfo {

    private UserCredentials userCredentials;
    private List<Vardgivare> vardgivare;

    /**
     * Maps a careUnitId to the name of the actual commission the user has on that care unit. Used for PDL-logging.
     *
     * See {@link se.riv.infrastructure.directory.v1.CommissionType#commissionName}
     */
    private Map<String, String> commissionNamePerCareUnit;

    public UserAuthorizationInfo(UserCredentials userCredentials, List<Vardgivare> vardgivare,
        Map<String, String> commissionNamePerCareUnit) {
        this.userCredentials = userCredentials;
        this.vardgivare = vardgivare;
        this.commissionNamePerCareUnit = commissionNamePerCareUnit;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public List<Vardgivare> getVardgivare() {
        return vardgivare;
    }

    public Map<String, String> getCommissionNamePerCareUnit() {
        return commissionNamePerCareUnit;
    }
}
