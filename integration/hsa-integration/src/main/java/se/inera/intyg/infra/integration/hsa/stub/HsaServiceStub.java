/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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

import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.inera.intyg.infra.integration.hsa.model.Vardgivare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author johannesc
 */
public class HsaServiceStub {

    // Data cache
    private List<String> readOnlyVardgivare = new ArrayList<>();

    private List<Vardgivare> vardgivare = new ArrayList<>();
    private List<Medarbetaruppdrag> medarbetaruppdrag = new ArrayList<>();

    private Map<String, HsaPerson> personMap = new HashMap<>();

    public Vardgivare getVardgivare(String hsaIdentity) {
        for (Vardgivare vg : vardgivare) {
            if (vg.getId().equalsIgnoreCase(hsaIdentity)) {
                return vg;
            }
        }
        return null;
    }

    public Vardenhet getVardenhet(String hsaIdentity) {

        for (Vardgivare vg : vardgivare) {
            for (Vardenhet vardenhet : vg.getVardenheter()) {
                if (vardenhet.getId().equals(hsaIdentity)) {
                    vardenhet.setVardgivareHsaId(vg.getId());
                    return vardenhet;
                }
            }
        }
        return null;
    }

    public void deleteVardgivare(String id) {
        vardgivare.removeIf(next -> next.getId().equals(id));
    }

    public void deleteMedarbetareuppdrag(String hsaId) {
        medarbetaruppdrag.removeIf(next -> next.getHsaId().equals(hsaId));
    }

    public List<Vardgivare> getVardgivare() {
        return vardgivare;
    }

    public List<Medarbetaruppdrag> getMedarbetaruppdrag() {
        return medarbetaruppdrag;
    }

    public Mottagning getMottagning(String hsaIdentity) {
        for (Vardgivare vg : vardgivare) {
            for (Vardenhet vardenhet : vg.getVardenheter()) {
                for (Mottagning mottagning : vardenhet.getMottagningar()) {
                    if (mottagning.getId().equals(hsaIdentity)) {
                        mottagning.setParentHsaId(vardenhet.getId());
                        return mottagning;
                    }
                }
            }
        }
        return null;
    }

    public HsaPerson getHsaPerson(String hsaId) {
        if (personMap.containsKey(hsaId)) {
            return personMap.get(hsaId);
        } else if (personMap.containsKey(hsaId.toUpperCase())) {
            return personMap.get(hsaId.toUpperCase());
        } else if (personMap.containsKey(hsaId.toLowerCase())) {
            return personMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }

    }

    public void addHsaPerson(HsaPerson person) {
        personMap.put(person.getHsaId(), person);
    }

    public void deleteHsaPerson(String hsaId) {
        personMap.remove(hsaId);
    }

    public void updateHsaPersonName(String hsaId, String firstName, String lastName) {
        // Hitta och uppdatera hsaPerson
        if (personMap.containsKey(hsaId)) {
            HsaPerson storedHsPerson = personMap.get(hsaId);
            storedHsPerson.setForNamn(firstName);
            storedHsPerson.setEfterNamn(lastName);
        }
    }

    public boolean isVardgivareReadOnly(String hsaId) {
        return readOnlyVardgivare.contains(hsaId);
    }

    public void markAsReadOnly(String hsaId) {
        readOnlyVardgivare.add(hsaId);
    }
}
