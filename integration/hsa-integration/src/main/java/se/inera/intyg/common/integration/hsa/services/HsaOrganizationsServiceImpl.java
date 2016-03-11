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

package se.inera.intyg.common.integration.hsa.services;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.common.integration.hsa.model.AbstractVardenhet;
import se.inera.intyg.common.integration.hsa.client.AuthorizationManagementService;
import se.inera.intyg.common.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.common.integration.hsa.model.Mottagning;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.integration.hsa.stub.Medarbetaruppdrag;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMemberType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.UnitType;
import se.riv.infrastructure.directory.v1.AddressType;
import se.riv.infrastructure.directory.v1.CommissionType;
import se.riv.infrastructure.directory.v1.CredentialInformationType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

import javax.xml.ws.WebServiceException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides HSA organization services through TJK over NTjP.
 *
 * infrastructure:directory:organization and
 * infrastructure:directory:authorizationmanagement
 *
 * @author andreaskaltenbach, eriklupander
 */
@Service
public class HsaOrganizationsServiceImpl implements HsaOrganizationsService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaOrganizationsServiceImpl.class);

    private static final String DEFAULT_ARBETSPLATSKOD = "0000000";

    private static final String DEFAULT_POSTNR = "XXXXX";

    @Autowired
    private AuthorizationManagementService authorizationManagementService;

    @Autowired
    private OrganizationUnitService organizationUnitService;

    @Override
    public String getVardgivareOfVardenhet(String careUnitHsaId) {
        GetHealthCareUnitResponseType healthCareUnit = organizationUnitService.getHealthCareUnit(careUnitHsaId);
        if (healthCareUnit == null || healthCareUnit.getHealthCareUnit() == null
                || healthCareUnit.getHealthCareUnit().getHealthCareProviderHsaId() == null) {
            LOG.error("Could not look up vardgivarId for vardEnhet {0}. Does vardEnhet exist?", careUnitHsaId);
            return null;
        }
        return healthCareUnit.getHealthCareUnit().getHealthCareProviderHsaId();
    }

    @Override
    public Vardenhet getVardenhet(String careUnitHsaId) {

        LOG.debug("Getting info on vardenhet '{}'", careUnitHsaId);

        UnitType unit = getUnit(careUnitHsaId);

        Vardenhet vardenhet = new Vardenhet(unit.getUnitHsaId(), unit.getUnitName(), unit.getUnitStartDate(), unit.getUnitEndDate());

        getHealthCareUnitMembers(vardenhet).ifPresent(response -> {
            attachMottagningar(vardenhet, response.getHealthCareUnitMembers());
            setArbetsplatskod(vardenhet, response.getHealthCareUnitMembers());
        });

        updateWithContactInformation(vardenhet, unit);

        return vardenhet;
    }

    @Override
    public List<String> getHsaIdForAktivaUnderenheter(String vardEnhetHsaId) {
        GetHealthCareUnitMembersResponseType response = organizationUnitService.getHealthCareUnitMembers(vardEnhetHsaId);
        final LocalDateTime now = LocalDateTime.now();
        if (response.getResultCode() == ResultCodeEnum.OK) {
            return response.getHealthCareUnitMembers().getHealthCareUnitMember()
                    .stream()
                    .filter(member -> (member.getHealthCareUnitMemberStartDate() == null
                            || member.getHealthCareUnitMemberStartDate().compareTo(now) <= 0)
                            && (member.getHealthCareUnitMemberEndDate() == null || member.getHealthCareUnitMemberEndDate().compareTo(now) >= 0))
                    .map(HealthCareUnitMemberType::getHealthCareUnitMemberHsaId)
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            LOG.error("getHealthCareUnitMembers failed with code '{}' and message '{}'", response.getResultCode().value(), response.getResultText());
            throw new WebServiceException(response.getResultText());
        }
    }

    @Override
    public List<Vardgivare> getAuthorizedEnheterForHosPerson(String hosPersonHsaId) {
        List<Vardgivare> vardgivareList = new ArrayList<>();

        GetCredentialsForPersonIncludingProtectedPersonResponseType response = authorizationManagementService
                .getAuthorizationsForPerson(hosPersonHsaId, null, null);

        if (response.getResultCode() == ResultCodeEnum.OK) {
            List<CredentialInformationType> credentialInformationList = response.getCredentialInformation();
            for (CredentialInformationType credentialInformation : credentialInformationList) {
                List<CommissionType> commissions = credentialInformation.getCommission()
                        .stream()
                        .filter(commissionType -> Medarbetaruppdrag.VARD_OCH_BEHANDLING.equalsIgnoreCase(commissionType.getCommissionPurpose()))
                        .collect(Collectors.toList());

                LOG.debug("User '{}' has a total of {} medarbetaruppdrag", hosPersonHsaId, commissions.size());

                vardgivareList.addAll(commissions.stream()
                        .filter(ct -> isActive(ct.getHealthCareProviderStartDate(), ct.getHealthCareProviderEndDate()))
                        .map(ct -> new Vardgivare(ct.getHealthCareProviderHsaId(), ct.getHealthCareProviderName()))
                        .distinct()
                        .map(vg -> {
                            vg.setVardenheter(commissions.stream()
                                    .filter(ct -> ct.getHealthCareProviderHsaId().equals(vg.getId())
                                            && isActive(ct.getHealthCareUnitStartDate(), ct.getHealthCareUnitEndDate()))
                                    .map(ct -> createVardenhet(credentialInformation, ct))
                                    .distinct()
                                    .sorted(Comparator.comparing(Vardenhet::getNamn))
                                    .collect(Collectors.toList()));

                            return vg;
                        }).collect(Collectors.toList()));
            }

            vardgivareList.sort(Comparator.nullsLast(Comparator.comparing(Vardgivare::getNamn)));
            return vardgivareList;
        } else {
            LOG.error("getAuthorizationsForPerson failed with code '{}' and message '{}'", response.getResultCode().value(),
                    response.getResultText());
            return vardgivareList; // Empty
        }
    }

    private Vardenhet createVardenhet(CredentialInformationType credentialInformation, CommissionType ct) {
        Vardenhet vardenhet = new Vardenhet(ct.getHealthCareUnitHsaId(), ct.getHealthCareUnitName());
        vardenhet.setStart(ct.getHealthCareUnitStartDate());
        vardenhet.setEnd(ct.getHealthCareUnitEndDate());

        // I don't like this, but we need to do an extra call to
        // infrastructure:directory:organization:getUnit for address related stuff.
        updateWithContactInformation(vardenhet, getUnit(vardenhet.getId()));

        getHealthCareUnitMembers(vardenhet).ifPresent(response -> {
            attachMottagningar(vardenhet, response.getHealthCareUnitMembers());
            setArbetsplatskod(vardenhet, response.getHealthCareUnitMembers());
        });

        return vardenhet;
    }

    private void setArbetsplatskod(Vardenhet vardenhet, final HealthCareUnitMembersType healthCareUnitMembers) {
        vardenhet.setArbetsplatskod(
                healthCareUnitMembers.getHealthCareUnitPrescriptionCode().size() > 0
                        && healthCareUnitMembers.getHealthCareUnitPrescriptionCode().get(0) != null
                                ? healthCareUnitMembers.getHealthCareUnitPrescriptionCode().get(0) : DEFAULT_ARBETSPLATSKOD);
    }

    private Optional<GetHealthCareUnitMembersResponseType> getHealthCareUnitMembers(final Vardenhet vardenhet) {
        GetHealthCareUnitMembersResponseType response = organizationUnitService.getHealthCareUnitMembers(vardenhet.getId());
        if (response == null || response.getResultCode() == ResultCodeEnum.ERROR) {
            LOG.error("Could not fetch mottagningar for unit {}, null or error response: ", vardenhet.getId(),
                    response != null ? response.getResultText() : "Response was null");
        }
        return Optional.ofNullable(response);
    }

    private boolean isActive(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime now = new LocalDateTime();

        if (fromDate != null && now.isBefore(fromDate)) {
            return false;
        }
        return !(toDate != null && now.isAfter(toDate));
    }

    private void attachMottagningar(Vardenhet vardenhet, final HealthCareUnitMembersType healthCareUnitMembers) {
        for (HealthCareUnitMemberType member : healthCareUnitMembers.getHealthCareUnitMember()) {

            if (!isActive(member.getHealthCareUnitMemberStartDate(), member.getHealthCareUnitMemberEndDate())) {
                LOG.debug("Mottagning '{}' is not active right now", member.getHealthCareUnitMemberHsaId());
                continue;
            }

            Mottagning mottagning = new Mottagning(member.getHealthCareUnitMemberHsaId(), member.getHealthCareUnitMemberName(),
                    member.getHealthCareUnitMemberStartDate(), member.getHealthCareUnitMemberEndDate());
            if (member.getHealthCareUnitMemberpostalAddress() != null && member.getHealthCareUnitMemberpostalAddress().getAddressLine() != null) {
                mottagning.setPostadress(member.getHealthCareUnitMemberpostalAddress().getAddressLine()
                        .stream()
                        .collect(Collectors.joining(" ")));
                mottagning.setParentHsaId(vardenhet.getId());
            }

            mottagning.setPostnummer(member.getHealthCareUnitMemberpostalCode());
            mottagning.setTelefonnummer(member.getHealthCareUnitMemberTelephoneNumber().stream().collect(Collectors.joining(", ")));
            mottagning.setArbetsplatskod(member.getHealthCareUnitMemberPrescriptionCode().size() > 0
                    ? member.getHealthCareUnitMemberPrescriptionCode().get(0) : DEFAULT_ARBETSPLATSKOD);

            vardenhet.getMottagningar().add(mottagning);
            LOG.debug("Attached mottagning '{}' to vardenhet '{}'", mottagning.getId(), vardenhet.getId());
        }
        vardenhet.setMottagningar(vardenhet.getMottagningar().stream().sorted().collect(Collectors.toList()));
    }

    private UnitType getUnit(String careUnitHsaId) {
        GetUnitResponseType unitResponse = organizationUnitService.getUnit(careUnitHsaId);
        return unitResponse.getUnit();
    }

    private void updateWithContactInformation(AbstractVardenhet vardenhet, UnitType response) {
        vardenhet.setEpost(response.getMail());
        if (response.getTelephoneNumber() != null && !response.getTelephoneNumber().isEmpty()) {
            vardenhet.setTelefonnummer(response.getTelephoneNumber().get(0));
        }
        AddressType address = response.getPostalAddress();
        if (address == null) {
            return;
        }

        // There exists a postal code field, HSA doesn't seem to use it though (they use adressline2 for zip and city)
        if (response.getPostalCode() != null && response.getPostalCode().trim().length() > 0) {
            vardenhet.setPostnummer(response.getPostalCode());
        }

        StringBuilder postaAddress = new StringBuilder();
        List<String> lines = address.getAddressLine();
        for (int i = 0; i < lines.size() - 1; i++) {
            if (lines.get(i) != null) {
                postaAddress.append(lines.get(i).trim());
            }
        }
        vardenhet.setPostadress(postaAddress.toString());

        String lastLine = lines != null && lines.size() > 0 ? lines.get(lines.size() - 1) : null;
        final int shortestLengthToIncludeBothPnrAndPostort = 7;
        if (lastLine != null && lastLine.length() > shortestLengthToIncludeBothPnrAndPostort && Character.isDigit(lastLine.charAt(0))) {
            final int startPostort = 6;
            vardenhet.setPostort(lastLine.substring(startPostort).trim());
            if (vardenhet.getPostnummer() == null) {
                vardenhet.setPostnummer(lastLine.substring(0, startPostort).trim());
            }
        } else {
            if (vardenhet.getPostnummer() == null) {
                vardenhet.setPostnummer(DEFAULT_POSTNR);
            }
            vardenhet.setPostort(lastLine != null ? lastLine.trim() : "");
        }
    }
}
