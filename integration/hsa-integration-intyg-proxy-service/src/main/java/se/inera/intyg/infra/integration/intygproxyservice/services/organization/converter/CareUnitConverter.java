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

package se.inera.intyg.infra.integration.intygproxyservice.services.organization.converter;

import static se.inera.intyg.infra.integration.intygproxyservice.services.organization.OrganizationUtil.getWorkplaceCode;
import static se.inera.intyg.infra.integration.intygproxyservice.services.organization.OrganizationUtil.isActive;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.Commission;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.model.legacy.AgandeForm;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;

@Service
@Slf4j
@RequiredArgsConstructor
public class CareUnitConverter {

    private static final String PUBLIC_PERFIX = "2";

    private final UnitAddressConverter unitAddressConverter;
    private final CareUnitMemberConverter careUnitMemberConverter;

    public Vardenhet convert(Commission commission, Unit hsaUnit, HealthCareUnitMembers members) {
        final var unit = new Vardenhet(commission.getHealthCareUnitHsaId(), commission.getHealthCareUnitName());
        unit.setStart(commission.getHealthCareUnitStartDate());
        unit.setEnd(commission.getHealthCareUnitEndDate());
        unit.setVardgivareHsaId(commission.getHealthCareProviderHsaId());
        unit.setVardgivareOrgnr(commission.getHealthCareProviderOrgNo());
        unit.setAgandeForm(getAgandeForm(commission.getHealthCareProviderOrgNo()));
        unit.setArbetsplatskod(getWorkplaceCode(members.getHealthCareUnitPrescriptionCode()));
        unit.setEpost(hsaUnit.getMail());
        unit.setTelefonnummer(hsaUnit.getTelephoneNumber().isEmpty() ? null : hsaUnit.getTelephoneNumber().get(0));
        unit.setMottagningar(getCareUnitMembers(unit, members));
        updateAddress(unit, hsaUnit.getPostalAddress(), hsaUnit.getPostalCode());

        return unit;
    }

    private AgandeForm getAgandeForm(String orgNo) {
        if (orgNo == null || orgNo.isEmpty()) {
            log.error("orgNo is null or empty, this make us unable to determine if the unit is private or not");
            return AgandeForm.OKAND;
        }
        return orgNo.startsWith(PUBLIC_PERFIX) ? AgandeForm.OFFENTLIG : AgandeForm.PRIVAT;
    }

    private List<Mottagning> getCareUnitMembers(Vardenhet vardenhet, HealthCareUnitMembers healthCareUnitMembers) {
        if (healthCareUnitMembers.getHealthCareUnitMember() == null) {
            return Collections.emptyList();
        }

        return healthCareUnitMembers.getHealthCareUnitMember().stream()
            .filter(member -> isActive(member.getHealthCareUnitMemberStartDate(), member.getHealthCareUnitMemberEndDate()))
            .map(member -> careUnitMemberConverter.convert(member, vardenhet.getId(), vardenhet.getAgandeForm()))
            .sorted()
            .collect(Collectors.toList());
    }

    private void updateAddress(Vardenhet unit, List<String> address, String postalCode) {
        unit.setPostadress(unitAddressConverter.convertAddress(address));
        unit.setPostnummer(unitAddressConverter.convertZipCode(address, postalCode));
        unit.setPostort(unitAddressConverter.convertCity(address));
    }

}
