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

package se.inera.intyg.infra.integration.intygproxyservice.services.organization;

import static se.inera.intyg.infra.integration.hsatk.constants.HsaIntegrationApiConstants.HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMember;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.model.legacy.AgandeForm;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitMembersRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE)
public class HsaLegacyGetCareUnitService {

    private final GetHealthCareUnitMembersService getHealthCareUnitMembersService;
    private final GetUnitService getUnitService;

    private static final int POSTORT_START_POS = 6;
    private static final int MIN_LENGTH_POSTNR_POSTORT = 7;
    private static final String DEFAULT_ARBETSPLATSKOD = "0000000";

    public Vardenhet get(String hsaId) {
        try {
            final var unit = getUnit(hsaId).orElseThrow();
            final var optionalUnitMembers = getUnitMembers(hsaId);
            final var postalAddress = unit.getPostalAddress();
            final var telephoneNumber = unit.getTelephoneNumber();
            final var careUnit = new Vardenhet(unit.getUnitHsaId(), unit.getUnitName(), unit.getUnitStartDate(), unit.getUnitEndDate());

            optionalUnitMembers.ifPresent(members -> {
                careUnit.setMottagningar(getUnits(members, careUnit));
                careUnit.setArbetsplatskod(getPrescriptionCode(members.getHealthCareUnitPrescriptionCode()));
            });

            if (!telephoneNumber.isEmpty()) {
                careUnit.setTelefonnummer(telephoneNumber.get(0));
            }

            if (postalAddress != null) {
                careUnit.setPostadress(extractPostalAddress(postalAddress));
                careUnit.setPostnummer(extractPostalCode(postalAddress, unit.getPostalCode()));
                careUnit.setPostort(extractPostTown(postalAddress));
            }

            careUnit.setEpost(unit.getMail());
            return careUnit;

        } catch (Exception e) {
            log.error("Failure getting Vardenhet {}", hsaId, e);
            throw new WebServiceException(e.getMessage());
        }
    }

    private Optional<Unit> getUnit(String hsaId) {
        final var unitRequest = GetUnitRequestDTO.builder().hsaId(hsaId).build();
        return Optional.ofNullable(getUnitService.get(unitRequest));
    }

    private Optional<HealthCareUnitMembers> getUnitMembers(String hsaId) {
        try {
            final var unitMemberRequest = GetHealthCareUnitMembersRequestDTO.builder().hsaId(hsaId).build();
            return Optional.ofNullable(getHealthCareUnitMembersService.get(unitMemberRequest));
        } catch (Exception e) {
            log.error("Failure fetching HealthCareUnitMembers for unit with id '{}'.", hsaId, e);
            return Optional.empty();
        }
    }

    private List<Mottagning> getUnits(HealthCareUnitMembers members, Vardenhet careUnit) {
        return members.getHealthCareUnitMember().stream()
            .filter(member -> isActive(member.getHealthCareUnitMemberStartDate(), member.getHealthCareUnitMemberEndDate()))
            .map(member -> createUnit(member, careUnit))
            .collect(Collectors.toList());
    }

    private boolean isActive(LocalDateTime start, LocalDateTime end) {
        final var now = LocalDateTime.now(ZoneId.systemDefault());
        return start.isBefore(now) && end.isAfter(now);
    }

    private Mottagning createUnit(HealthCareUnitMember member, Vardenhet careUnit) {
        final var unit = newUnit(member);
        final var postalAddress = member.getHealthCareUnitMemberpostalAddress();

        if (postalAddress != null) {
            final var postalCode = member.getHealthCareUnitMemberpostalCode();
            unit.setPostadress(extractPostalAddress(postalAddress));
            unit.setPostnummer(extractPostalCode(postalAddress, postalCode));
            unit.setPostort(extractPostTown(postalAddress));
        }
        unit.setParentHsaId(careUnit.getId());
        unit.setTelefonnummer(String.join(", ", member.getHealthCareUnitMemberTelephoneNumber()));
        unit.setArbetsplatskod(getPrescriptionCode(member.getHealthCareUnitMemberPrescriptionCode()));
        unit.setAgandeForm(AgandeForm.OKAND);
        return unit;
    }

    private Mottagning newUnit(HealthCareUnitMember member) {
        return new Mottagning(member.getHealthCareUnitMemberHsaId(), member.getHealthCareUnitMemberName(),
            member.getHealthCareUnitMemberStartDate(), member.getHealthCareUnitMemberEndDate());
    }

    private String extractPostalAddress(List<String> postalAddress) {
        if (postalAddress.isEmpty()) {
            return "";
        }

        return postalAddress.subList(0, postalAddress.size() - 1).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" "));
    }

    private String extractPostalCode(List<String> postalAddress, String postalCode) {
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            return postalCode;
        }

        if (postalAddress.isEmpty()) {
            return "";
        }

        final var lastAddressLine = postalAddress.get(postalAddress.size() - 1);
        return startsWithPostalCode(lastAddressLine) ? lastAddressLine.substring(0, POSTORT_START_POS).trim() : "";
    }

    private String extractPostTown(List<String> postalAddress) {
        final var lastAddressLine = !postalAddress.isEmpty() ? postalAddress.get(postalAddress.size() - 1) : null;

        if (startsWithPostalCode(lastAddressLine)) {
            return lastAddressLine.substring(POSTORT_START_POS).trim();
        }
        return lastAddressLine != null ? lastAddressLine.trim() : "";
    }

    private boolean startsWithPostalCode(String lastAddressLine) {
        return lastAddressLine != null && lastAddressLine.length() > MIN_LENGTH_POSTNR_POSTORT
            && Character.isDigit(lastAddressLine.charAt(0));
    }

    private String getPrescriptionCode(List<String> prescriptionCodes) {
        return prescriptionCodes.isEmpty() || prescriptionCodes.get(0) == null ? DEFAULT_ARBETSPLATSKOD : prescriptionCodes.get(0);
    }
}
