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

import java.util.*;
import java.util.stream.Collectors;

import javax.xml.ws.WebServiceException;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.common.integration.hsa.client.AuthorizationManagementService;
import se.inera.intyg.common.integration.hsa.client.OrganizationUnitService;
import se.inera.intyg.common.integration.hsa.model.*;
import se.inera.intyg.common.integration.hsa.stub.Medarbetaruppdrag;
import se.inera.intyg.common.support.common.util.StringUtil;
import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMemberType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.UnitType;
import se.riv.infrastructure.directory.v1.*;

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
        try {
            HealthCareUnitType healthCareUnit = organizationUnitService.getHealthCareUnit(careUnitHsaId);
            return healthCareUnit.getHealthCareProviderHsaId();
        } catch (ExternalServiceCallException e) {
            LOG.error("Could not look up vardgivarId for vardEnhet {}. Does vardEnhet exist?", careUnitHsaId);
            return null;
        }
    }

    @Override
    public Vardenhet getVardenhet(String careUnitHsaId) {

        LOG.debug("Getting info on vardenhet '{}'", careUnitHsaId);

        UnitType unit;
        try {
            unit = getUnit(careUnitHsaId);
        } catch (ExternalServiceCallException e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        Vardenhet vardenhet = new Vardenhet(unit.getUnitHsaId(), unit.getUnitName(), unit.getUnitStartDate(), unit.getUnitEndDate());

        getHealthCareUnitMembers(vardenhet).ifPresent(response -> {
            attachMottagningar(vardenhet, response);
            setArbetsplatskod(vardenhet, response);
        });

        updateWithContactInformation(vardenhet, unit);

        return vardenhet;
    }

    @Override
    public List<String> getHsaIdForAktivaUnderenheter(String vardEnhetHsaId) {
        try {
            HealthCareUnitMembersType response = organizationUnitService.getHealthCareUnitMembers(vardEnhetHsaId);
            final LocalDateTime now = LocalDateTime.now();
            return response.getHealthCareUnitMember()
                    .stream()
                    .filter(member -> (member.getHealthCareUnitMemberStartDate() == null
                            || member.getHealthCareUnitMemberStartDate().compareTo(now) <= 0)
                            && (member.getHealthCareUnitMemberEndDate() == null || member.getHealthCareUnitMemberEndDate().compareTo(now) >= 0))
                    .map(HealthCareUnitMemberType::getHealthCareUnitMemberHsaId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (ExternalServiceCallException e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
    }

    @Override
    public UserAuthorizationInfo getAuthorizedEnheterForHosPerson(String hosPersonHsaId) {
        List<Vardgivare> vardgivareList = new ArrayList<>();
        UserCredentials userCredentials = new UserCredentials();

        try {
            List<CredentialInformationType> credentialInformationList = authorizationManagementService
                    .getAuthorizationsForPerson(hosPersonHsaId, null, null);

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
                                    .filter(Objects::nonNull)
                                    .distinct()
                                    .sorted(Comparator.comparing(Vardenhet::getNamn))
                                    .collect(Collectors.toList()));

                            return vg;
                        }).collect(Collectors.toList()));

                // Add relevant credentialInfo to the userCredz
                userCredentials.getGroupPrescriptionCode().addAll(credentialInformation.getGroupPrescriptionCode());
                userCredentials.getPaTitleCode().addAll(credentialInformation.getPaTitleCode());
                userCredentials.setPersonalPrescriptionCode(credentialInformation.getPersonalPrescriptionCode());
                userCredentials.getHsaSystemRole().addAll(credentialInformation.getHsaSystemRole());
            }

            vardgivareList.sort(Comparator.nullsLast(Comparator.comparing(Vardgivare::getNamn)));
            return new UserAuthorizationInfo(userCredentials, vardgivareList);
        } catch (ExternalServiceCallException e) {
            LOG.warn("Returning empty vardgivareList, cause: {}", e.getMessage());
        }
        return new UserAuthorizationInfo(userCredentials, vardgivareList); // Empty
    }

    private Vardenhet createVardenhet(CredentialInformationType credentialInformation, CommissionType ct) {
        Vardenhet vardenhet = new Vardenhet(ct.getHealthCareUnitHsaId(), ct.getHealthCareUnitName());
        vardenhet.setStart(ct.getHealthCareUnitStartDate());
        vardenhet.setEnd(ct.getHealthCareUnitEndDate());
        AgandeForm agandeForm = getAgandeForm(ct.getHealthCareProviderOrgNo());
        vardenhet.setAgandeForm(agandeForm);

        // I don't like this, but we need to do an extra call to
        // infrastructure:directory:organization:getUnit for address related stuff.
        try {
            updateWithContactInformation(vardenhet, getUnit(vardenhet.getId()));
        } catch (ExternalServiceCallException e) {
            LOG.error(e.getMessage());
            return null;
        }

        getHealthCareUnitMembers(vardenhet).ifPresent(response -> {
            attachMottagningar(vardenhet, response, agandeForm);
            setArbetsplatskod(vardenhet, response);
        });

        return vardenhet;
    }

    private void setArbetsplatskod(Vardenhet vardenhet, final HealthCareUnitMembersType healthCareUnitMembers) {
        vardenhet.setArbetsplatskod(
                healthCareUnitMembers.getHealthCareUnitPrescriptionCode().size() > 0
                        && healthCareUnitMembers.getHealthCareUnitPrescriptionCode().get(0) != null
                                ? healthCareUnitMembers.getHealthCareUnitPrescriptionCode().get(0) : DEFAULT_ARBETSPLATSKOD);
    }

    private Optional<HealthCareUnitMembersType> getHealthCareUnitMembers(final Vardenhet vardenhet) {
        try {
            HealthCareUnitMembersType response = organizationUnitService.getHealthCareUnitMembers(vardenhet.getId());
            return Optional.ofNullable(response);
        } catch (ExternalServiceCallException e) {
            return Optional.empty();
        }
    }

    private boolean isActive(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime now = new LocalDateTime();

        if (fromDate != null && now.isBefore(fromDate)) {
            return false;
        }
        return !(toDate != null && now.isAfter(toDate));
    }

    private AgandeForm getAgandeForm(String orgNo) {
        if (StringUtil.isNullOrEmpty(orgNo)) {
            LOG.error("orgNo is null or empty, this make us unable to determine if the unit is private or not");
            return AgandeForm.OKAND;
        }
        return orgNo.startsWith("2") ? AgandeForm.OFFENTLIG : AgandeForm.PRIVAT;
    }

    /**
     * Used when ownership is not used.
     */
    private void attachMottagningar(Vardenhet vardenhet, HealthCareUnitMembersType healthCareUnitMembers) {
        attachMottagningar(vardenhet, healthCareUnitMembers, AgandeForm.OKAND);
    }

    private void attachMottagningar(Vardenhet vardenhet, final HealthCareUnitMembersType healthCareUnitMembers, AgandeForm agandeForm) {
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
            mottagning.setAgandeForm(agandeForm);

            vardenhet.getMottagningar().add(mottagning);
            LOG.debug("Attached mottagning '{}' to vardenhet '{}'", mottagning.getId(), vardenhet.getId());
        }
        vardenhet.setMottagningar(vardenhet.getMottagningar().stream().sorted().collect(Collectors.toList()));
    }

    private UnitType getUnit(String careUnitHsaId) throws ExternalServiceCallException {
        return organizationUnitService.getUnit(careUnitHsaId);
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

        List<String> lines = address.getAddressLine();

        if (!lines.isEmpty()) {
            vardenhet.setPostadress(lines.subList(0, address.getAddressLine().size() - 1).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" ")));
        } else {
            vardenhet.setPostadress("");
        }

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
