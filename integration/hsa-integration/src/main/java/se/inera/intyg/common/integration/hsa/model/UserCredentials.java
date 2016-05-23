/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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
package se.inera.intyg.common.integration.hsa.model;

import se.riv.infrastructure.directory.v1.HsaSystemRoleType;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage for user-related info from GetCredentials HSA response.
 *
 * A number of data fields from HSA that are related to the persons "medarbetaruppdrag" is returned on the
 * {@link se.riv.infrastructure.directory.v1.CredentialInformationType}, which we need to provide to callers of
 * the HSA services.
 *
 * Created by eriklupander on 2016-05-20.
 */
public class UserCredentials {

    private String personalPrescriptionCode;
    private List<String> groupPrescriptionCode;
    private List<HsaSystemRoleType> hsaSystemRole;
    private List<String> paTitleCode;

    public String getPersonalPrescriptionCode() {
        return personalPrescriptionCode;
    }

    public void setPersonalPrescriptionCode(String personalPrescriptionCode) {
        this.personalPrescriptionCode = personalPrescriptionCode;
    }

    public List<String> getGroupPrescriptionCode() {
        if (groupPrescriptionCode == null) {
            groupPrescriptionCode = new ArrayList<>();
        }
        return groupPrescriptionCode;
    }

    public List<HsaSystemRoleType> getHsaSystemRole() {
        if (hsaSystemRole == null) {
            hsaSystemRole = new ArrayList<>();
        }
        return hsaSystemRole;
    }

    public List<String> getPaTitleCode() {
        if (paTitleCode == null) {
            paTitleCode = new ArrayList<>();
        }
        return paTitleCode;
    }
}
