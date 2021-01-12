/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.stub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author andreaskaltenbach
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CredentialInformation {

    public static final String VARD_OCH_BEHANDLING = "Vård och behandling";
    public static final String STATISTIK = "Statistik";

    private String hsaId;
    private String givenName;
    private List<Commission> commissionList = new ArrayList<>();

    public CredentialInformation() {
        // Needed for deserialization
    }

    public CredentialInformation(String hsaId, List<Commission> commissionList) {
        this.hsaId = hsaId;
        this.commissionList = commissionList;
    }

    @Data
    public static class Commission {

        private String healthCareProviderHsaId;
        private String healthCareUnitHsaId;
        private List<String> commissionPurpose;

        /**
         * Medarbetaruppdragets namn, motsv. CommissionType#commissionName
         */

        public Commission() {
            healthCareUnitHsaId = "";
            commissionPurpose = asList(VARD_OCH_BEHANDLING);
        }

        public Commission(String healthCareUnitHsaId, String commissionPurpose) {
            this.healthCareUnitHsaId = healthCareUnitHsaId;
            this.commissionPurpose = asList(commissionPurpose);
        }

    }
}
