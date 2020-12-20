/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.stub;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareUnitStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.inera.intyg.infra.integration.hsatk.stub.model.SubUnitStub;

@Service
public class HsaServiceStub {


    private List<String> readOnlyCareProvider = new ArrayList<>();

    private Map<String, HsaPerson> hsaPersonMap = new HashMap<>();
    private Map<String, CredentialInformation> credentialInformationMap = new HashMap<>();
    private Map<String, CareProviderStub> careProviderMap = new HashMap<>();
    private Map<String, CareUnitStub> careUnitMap = new HashMap<>();
    private Map<String, SubUnitStub> subUnitMap = new HashMap<>();

    private LocalDateTime lastHospUpdate = LocalDateTime.now(ZoneId.systemDefault());

    public void addCredentialInformation(CredentialInformation credentialInformation) {
        credentialInformationMap.put(credentialInformation.getHsaId(), credentialInformation);
    }

    public void deleteCredentialInformation(String hsaId) {
        credentialInformationMap.remove(hsaId);
    }

    public Collection<CredentialInformation> getCredentialInformation() {
        return credentialInformationMap.values();
    }

    public CredentialInformation getCredentialInformation(String hsaId) {
        if (isNullOrShouldNotExistInHsa(hsaId)) {
            return null;
        } else if (credentialInformationMap.containsKey(hsaId)) {
            return credentialInformationMap.get(hsaId);
        } else if (credentialInformationMap.containsKey(hsaId.toUpperCase())) {
            return credentialInformationMap.get(hsaId.toUpperCase());
        } else if (credentialInformationMap.containsKey(hsaId.toLowerCase())) {
            return credentialInformationMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public void addHsaPerson(HsaPerson hsaPerson) {
        if (hsaPerson.getHsaId() != null) {
            hsaPersonMap.put(hsaPerson.getHsaId(), hsaPerson);
        }
        if (hsaPerson.getPersonalIdentityNumber() != null) {
            hsaPersonMap.put(hsaPerson.getPersonalIdentityNumber(), hsaPerson);
        }
    }

    public void deleteHsaPerson(String id) {
        hsaPersonMap.remove(id);
    }

    public Collection<HsaPerson> getHsaPerson() {
        return hsaPersonMap.values();
    }

    public HsaPerson getHsaPerson(String id) {
        if (isNullOrShouldNotExistInHsa(id)) {
            return null;
        } else if (hsaPersonMap.containsKey(id)) {
            return hsaPersonMap.get(id);
        } else if (hsaPersonMap.containsKey(id.toUpperCase())) {
            return hsaPersonMap.get(id.toUpperCase());
        } else if (hsaPersonMap.containsKey(id.toLowerCase())) {
            return hsaPersonMap.get(id.toLowerCase());
        } else {
            return null;
        }
    }

    public void addCareProvider(CareProviderStub careProviderStub) {
        if (careProviderStub != null && careProviderStub.getId() != null) {
            careProviderMap.put(careProviderStub.getId(), careProviderStub);
            if (careProviderStub.getCareUnits() != null && careProviderStub.getCareUnits().size() > 0) {
                for (CareUnitStub careUnitStub : careProviderStub.getCareUnits()) {
                    careUnitStub.setCareProviderHsaId(careProviderStub.getId());
                    careUnitMap.put(careUnitStub.getId(), careUnitStub);
                    if (careUnitStub.getSubUnits() != null && careUnitStub.getSubUnits().size() > 0) {
                        for (SubUnitStub subUnit : careUnitStub.getSubUnits()) {
                            subUnit.setParentHsaId(careUnitStub.getId());
                            subUnitMap.put(subUnit.getId(), subUnit);
                        }
                    }
                }
            }
        }
    }

    public void addCareUnit(CareUnitStub careUnitStub) {
        if (careUnitStub != null && careUnitStub.getId() != null) {
            careUnitMap.put(careUnitStub.getId(), careUnitStub);
        }
    }

    public void addSubUnit(SubUnitStub subUnitStub) {
        if (subUnitStub != null && subUnitStub.getId() != null) {
            subUnitMap.put(subUnitStub.getId(), subUnitStub);
        }
    }

    public void deleteCareProvider(String hsaId) {
        for (CareUnitStub careUnitStub : careProviderMap.get(hsaId).getCareUnits()) {
            for (SubUnitStub subUnit : careUnitStub.getSubUnits()) {
                subUnitMap.remove(subUnit.getId());
            }
            careUnitMap.remove(careUnitStub.getId());
        }
        careProviderMap.remove(hsaId);
    }

    public Collection<CareProviderStub> getCareProvider() {
        return careProviderMap.values();
    }

    public CareProviderStub getCareProvider(String hsaId) {
        if (isNullOrShouldNotExistInHsa(hsaId)) {
            return null;
        } else if (careProviderMap.containsKey(hsaId)) {
            return careProviderMap.get(hsaId);
        } else if (careProviderMap.containsKey(hsaId.toUpperCase())) {
            return careProviderMap.get(hsaId.toUpperCase());
        } else if (careProviderMap.containsKey(hsaId.toLowerCase())) {
            return careProviderMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public CareUnitStub getCareUnit(String hsaId) {
        if (isNullOrShouldNotExistInHsa(hsaId)) {
            return null;
        } else if (careUnitMap.containsKey(hsaId)) {
            return careUnitMap.get(hsaId);
        } else if (careUnitMap.containsKey(hsaId.toUpperCase())) {
            return careUnitMap.get(hsaId.toUpperCase());
        } else if (careUnitMap.containsKey(hsaId.toLowerCase())) {
            return careUnitMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public SubUnitStub getSubUnit(String hsaId) {
        if (isNullOrShouldNotExistInHsa(hsaId)) {
            return null;
        } else if (subUnitMap.containsKey(hsaId)) {
            return subUnitMap.get(hsaId);
        } else if (subUnitMap.containsKey(hsaId.toUpperCase())) {
            return subUnitMap.get(hsaId.toUpperCase());
        } else if (subUnitMap.containsKey(hsaId.toLowerCase())) {
            return subUnitMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public static boolean isNullOrShouldNotExistInHsa(String hsaId) {
        return hsaId == null || hsaId.startsWith("EJHSA") || "UTANENHETSID".equals(hsaId) || hsaId.endsWith("-finns-ej");
    }

    public void markAsReadOnly(String hsaId) {
        readOnlyCareProvider.add(hsaId);
    }

    public boolean isCareProviderReadOnly(String hsaId) {
        return readOnlyCareProvider.contains(hsaId);
    }

    public LocalDateTime getHospLastUpdate() {
        return lastHospUpdate;
    }

    public void resetHospLastUpdate() {
        lastHospUpdate = LocalDateTime.now(ZoneId.systemDefault());
    }

}
