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
package se.inera.intyg.infra.integration.hsatk.stub;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
        add(credentialInformation.getHsaId(), credentialInformation, credentialInformationMap);
    }

    public void deleteCredentialInformation(String hsaId) {
        remove(hsaId, credentialInformationMap);
    }

    public Collection<CredentialInformation> getCredentialInformation() {
        return credentialInformationMap.values();
    }

    public CredentialInformation getCredentialInformation(String hsaId) {
        return get(hsaId, credentialInformationMap);
    }

    public void addHsaPerson(HsaPerson hsaPerson) {
        add(hsaPerson.getHsaId(), hsaPerson, hsaPersonMap);
        add(hsaPerson.getPersonalIdentityNumber(), hsaPerson, hsaPersonMap);
    }

    public void deleteHsaPerson(String id) {
        remove(id, hsaPersonMap);
    }

    public Collection<HsaPerson> getHsaPerson() {
        return hsaPersonMap.values();
    }

    public HsaPerson getHsaPerson(String id) {
        return get(id, hsaPersonMap);
    }

    public void addCareProvider(CareProviderStub careProviderStub) {
        if (careProviderStub != null) {
            add(careProviderStub.getId(), careProviderStub, careProviderMap);

            if (careProviderStub.getCareUnits() != null && !careProviderStub.getCareUnits().isEmpty()) {
                for (CareUnitStub careUnitStub : careProviderStub.getCareUnits()) {
                    careUnitStub.setCareProviderHsaId(careProviderStub.getId());
                    add(careUnitStub.getId(), careUnitStub, careUnitMap);

                    if (careUnitStub.getSubUnits() != null && !careUnitStub.getSubUnits().isEmpty()) {
                        for (SubUnitStub subUnit : careUnitStub.getSubUnits()) {
                            subUnit.setParentHsaId(careUnitStub.getId());
                            add(subUnit.getId(), subUnit, subUnitMap);
                        }
                    }
                }
            }
        }
    }

    public void addCareUnit(CareUnitStub careUnitStub) {
        add(careUnitStub.getId(), careUnitStub, careUnitMap);
    }

    public void addSubUnit(SubUnitStub subUnitStub) {
        add(subUnitStub.getId(), subUnitStub, subUnitMap);
    }

    public void deleteCareProvider(String hsaId) {
        var careProvider = get(hsaId, careProviderMap);

        if (careProvider != null) {
            var careUnits = careProvider.getCareUnits();

            if (careUnits != null) {
                for (CareUnitStub careUnitStub : careUnits) {
                    var subUnits = careUnitStub.getSubUnits();

                    if (subUnits != null) {
                        for (SubUnitStub subUnit : subUnits) {
                            remove(subUnit.getId(), subUnitMap);
                        }
                    }
                    remove(careUnitStub.getId(), careUnitMap);
                }
            }
            remove(hsaId, careProviderMap);
        }
    }

    public Collection<CareProviderStub> getCareProvider() {
        return careProviderMap.values();
    }

    public CareProviderStub getCareProvider(String hsaId) {
        return get(hsaId, careProviderMap);

    }

    public CareUnitStub getCareUnit(String hsaId) {
        return get(hsaId, careUnitMap);

    }

    public SubUnitStub getSubUnit(String hsaId) {
        return get(hsaId, subUnitMap);

    }

    public void markAsReadOnly(String hsaId) {
        if (hsaId != null) {
            readOnlyCareProvider.add(hsaId.toUpperCase());
        }
    }

    public boolean isCareProviderReadOnly(String hsaId) {
        return hsaId != null && readOnlyCareProvider.contains(hsaId.toUpperCase());
    }

    public LocalDateTime getHospLastUpdate() {
        return lastHospUpdate;
    }

    public void resetHospLastUpdate() {
        lastHospUpdate = LocalDateTime.now(ZoneId.systemDefault());
    }

    private static <T> void add(String id, T value, Map<String, T> map) {
        if (id != null && value != null && map != null) {
            map.put(formatId(id), value);
        }
    }

    private static <T> void remove(String id, Map<String, T> map) {
        if (id != null && map != null) {
            map.remove(formatId(id));
        }
    }

    private static <T> T get(String id, Map<String, T> map) {
        if (id != null && map != null) {
            var formatId = formatId(id);
            return isNullOrShouldNotExistInHsa(formatId) ? null : map.get(formatId);
        }
        return null;

    }

    private static String formatId(String id) {
        return StringUtils.trimAllWhitespace(id.toUpperCase());
    }

    private static boolean isNullOrShouldNotExistInHsa(String hsaId) {
        return hsaId == null || hsaId.startsWith("EJHSA") || "UTANENHETSID".equals(hsaId) || hsaId.endsWith("-FINNS-EJ");
    }

}
