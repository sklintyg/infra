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
package se.inera.intyg.infra.integration.hsatk.util;

import java.util.stream.Collectors;
import se.inera.intyg.infra.integration.hsatk.model.Commission;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.model.HCPSpecialityCodes;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProfessionalLicence;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProvider;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMember;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.hsatk.model.HsaSystemRole;
import se.inera.intyg.infra.integration.hsatk.model.NursePrescriptionRight;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CommissionType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HCPSpecialityCodesType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HealthCareProfessionalLicenceType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HsaSystemRoleType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.NursePrescriptionRightType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.RestrictionType;
import se.riv.infrastructure.directory.employee.v2.HealthCareProfessionalLicenceSpecialityType;
import se.riv.infrastructure.directory.employee.v2.PaTitleType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMemberType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.BusinessClassificationType;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.GeoCoordRt90Type;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.GeoCoordSWEREF99Type;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.UnitType;

public class HsaTypeConverter {

    public HealthCareProfessionalLicence toHealthCareProfessionalLicence(HealthCareProfessionalLicenceType
                                                                                 healthCareProfessionalLicenceType) {
        HealthCareProfessionalLicence healthCareProfessionalLicence = new HealthCareProfessionalLicence();
        healthCareProfessionalLicence
                .setHealthCareProfessionalLicenceCode(healthCareProfessionalLicenceType.getHealthCareProfessionalLicenceCode());
        healthCareProfessionalLicence
                .setHealthCareProfessionalLicenceName(healthCareProfessionalLicenceType.getHealthCareProfessionalLicenceName());

        return healthCareProfessionalLicence;
    }

    public CredentialInformation toCredentialInformation(CredentialInformationType credentialInformationType) {
        CredentialInformation credentialInformation = new CredentialInformation();

        credentialInformation.setCommission(credentialInformationType.getCommission()
            .stream().map(this::toCommission).collect(Collectors.toList()));
        credentialInformation.setFeignedPerson(credentialInformationType.isFeignedPerson());
        credentialInformation.setGivenName(credentialInformationType.getGivenName());
        credentialInformation.setGroupPrescriptionCode(credentialInformationType.getGroupPrescriptionCode());
        credentialInformation.setHealthCareProfessionalLicence(credentialInformationType.getHealthCareProfessionalLicence());
        credentialInformation.setHealthCareProfessionalLicenceCode(credentialInformationType.getHealthCareProfessionalLicenceCode());
        if (!credentialInformationType.getHealthCareProfessionalLicenceSpeciality().isEmpty()) {
            credentialInformation.setHealthCareProfessionalLicenceSpeciality(
                    credentialInformationType.getHealthCareProfessionalLicenceSpeciality()
                            .stream().map(this::toHCPSpecialityCodes).collect(Collectors.toList()));
        }
        credentialInformation.setHealthcareProfessionalLicenseIdentityNumber(
            credentialInformationType.getHealthcareProfessionalLicenseIdentityNumber());
        if (!credentialInformationType.getHsaSystemRole().isEmpty()) {
            credentialInformation.setHsaSystemRole(credentialInformationType.getHsaSystemRole()
                .stream().map(this::toHsaSystemRole).collect(Collectors.toList()));
        }
        credentialInformation.setMiddleAndSurName(credentialInformationType.getMiddleAndSurName());
        if (!credentialInformationType.getNursePrescriptionRight().isEmpty()) {
            credentialInformation.setNursePrescriptionRight(
                credentialInformationType.getNursePrescriptionRight()
                    .stream().map(this::toNursePrescriptionRight).collect(Collectors.toList()));
        }
        credentialInformation.setOccupationalCode(credentialInformationType.getOccupationalCode());
        credentialInformation.setPaTitleCode(credentialInformationType.getPaTitleCode());
        if (credentialInformation.getPersonalIdentity() != null) {
            credentialInformation.setPersonalIdentity(credentialInformationType.getPersonalIdentity().getExtension());
        }
        credentialInformation.setPersonalPrescriptionCode(credentialInformationType.getPersonalPrescriptionCode());
        credentialInformation.setPersonHsaId(credentialInformationType.getPersonHsaId());
        credentialInformation.setProtectedPerson(credentialInformationType.isProtectedPerson());

        return credentialInformation;
    }

    public Commission toCommission(CommissionType commissionType) {
        Commission commission = new Commission();

        commission.setCommissionPurpose(commissionType.getCommissionPurpose());
        commission.setHealthCareProviderHsaId(commissionType.getHealthCareProviderHsaId());
        commission.setHealthCareProviderName(commissionType.getHealthCareProviderName());
        commission.setHealthCareUnitHsaId(commissionType.getHealthCareUnitHsaId());
        commission.setHealthCareUnitName(commissionType.getHealthCareUnitName());
        commission.setHealthCareUnitStartDate(commissionType.getHealthCareUnitStartDate());
        commission.setHealthCareUnitEndDate(commissionType.getHealthCareUnitEndDate());

        return commission;
    }

    public HCPSpecialityCodes toHCPSpecialityCodes(HCPSpecialityCodesType hcpSpecialityCodesType) {
        HCPSpecialityCodes hcpSpecialityCodes = new HCPSpecialityCodes();

        hcpSpecialityCodes.setSpecialityName(hcpSpecialityCodesType.getSpecialityName());
        hcpSpecialityCodes.setSpecialityCode(hcpSpecialityCodesType.getSpecialityCode());
        hcpSpecialityCodes.setHealthCareProfessionalLicenceCode(hcpSpecialityCodesType.getHealthCareProfessionalLicenceCode());

        return hcpSpecialityCodes;
    }

    public HsaSystemRole toHsaSystemRole(HsaSystemRoleType hsaSystemRoleType) {
        HsaSystemRole hsaSystemRole = new HsaSystemRole();

        hsaSystemRole.setRole(hsaSystemRoleType.getRole());
        hsaSystemRole.setSystemId(hsaSystemRoleType.getSystemId());

        return hsaSystemRole;
    }

    public NursePrescriptionRight toNursePrescriptionRight(NursePrescriptionRightType nursePrescriptionRightType) {
        NursePrescriptionRight nursePrescriptionRight = new NursePrescriptionRight();

        nursePrescriptionRight.setPrescriptionRight(nursePrescriptionRightType.isPrescriptionRight());
        nursePrescriptionRight.setHealthCareProfessionalLicence(nursePrescriptionRightType.getHealthCareProfessionalLicence());

        return nursePrescriptionRight;
    }

    public HospCredentialsForPerson.Restriction toRestriction(RestrictionType restrictionType) {
        HospCredentialsForPerson.Restriction restriction = new HospCredentialsForPerson.Restriction();

        restriction.setHealthCareProfessionalLicenceCode(restrictionType.getHealthCareProfessionalLicenceCode());
        restriction.setRestrictionCode(restrictionType.getRestrictionCode());
        restriction.setRestrictionName(restrictionType.getRestrictionName());

        return restriction;
    }

    public PersonInformation toPersonInformation(PersonInformationType personInformationType) {
        PersonInformation personInformation = new PersonInformation();
        personInformation.setAge(personInformationType.getAge());
        personInformation.setFeignedPerson(personInformationType.isFeignedPerson());
        personInformation.setGender(personInformationType.getGender());
        personInformation.setGivenName(personInformationType.getGivenName());
        personInformation.setHealthCareProfessionalLicence(personInformationType.getHealthCareProfessionalLicence());
        personInformation.setHealthCareProfessionalLicenceSpeciality(personInformationType.getHealthCareProfessionalLicenceSpeciality()
            .stream().map(this::toHCPSpecialityCodes).collect(Collectors.toList()));
        personInformation.setMiddleAndSurName(personInformationType.getMiddleAndSurName());
        personInformation.setPaTitle(personInformationType.getPaTitle().stream().map(this::toPaTitleType).collect(Collectors.toList()));
        personInformation.setPersonEndDate(personInformationType.getPersonEndDate());
        personInformation.setPersonHsaId(personInformationType.getPersonHsaId());
        personInformation.setPersonStartDate(personInformationType.getPersonStartDate());
        personInformation.setProtectedPerson(personInformationType.isProtectedPerson());
        personInformation.setSpecialityCode(personInformationType.getSpecialityCode());
        personInformation.setSpecialityName(personInformationType.getSpecialityName());
        personInformation.setTitle(personInformationType.getTitle());
        return personInformation;
    }

    public HCPSpecialityCodes toHCPSpecialityCodes(
        HealthCareProfessionalLicenceSpecialityType healthCareProfessionalLicenceSpecialityType) {

        HCPSpecialityCodes hcpSpecialityCodes = new HCPSpecialityCodes();

        hcpSpecialityCodes.setHealthCareProfessionalLicenceCode(
            healthCareProfessionalLicenceSpecialityType.getHealthCareProfessionalLicence());
        hcpSpecialityCodes.setSpecialityCode(healthCareProfessionalLicenceSpecialityType.getSpecialityCode());
        hcpSpecialityCodes.setSpecialityName(healthCareProfessionalLicenceSpecialityType.getSpecialityName());

        return hcpSpecialityCodes;
    }

    public PersonInformation.PaTitle toPaTitleType(PaTitleType paTitleType) {

        PersonInformation.PaTitle paTitle = new PersonInformation.PaTitle();

        paTitle.setPaTitleCode(paTitleType.getPaTitleCode());
        paTitle.setPaTitleName(paTitleType.getPaTitleName());
        return paTitle;
    }

    public HealthCareProvider toHealthCareProvider(
        se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.HealthCareProviderType healthCareProviderType) {
        HealthCareProvider healthCareProvider = new HealthCareProvider();

        healthCareProvider.setArchivedHealthCareProvider(healthCareProviderType.isArchivedHealthCareProvider());
        healthCareProvider.setFeignedHealthCareProvider(healthCareProviderType.isFeignedHealthCareProvider());
        healthCareProvider.setHealthCareProviderEndDate(healthCareProviderType.getHealthCareProviderEndDate());
        healthCareProvider.setHealthCareProviderHsaId(healthCareProviderType.getHealthCareProviderHsaId());
        healthCareProvider.setHealthCareProviderName(healthCareProviderType.getHealthCareProviderName());
        healthCareProvider.setHealthCareProviderOrgNo(healthCareProviderType.getHealthCareProviderOrgNo());
        healthCareProvider.setHealthCareProviderStartDate(healthCareProviderType.getHealthCareProviderStartDate());

        return healthCareProvider;
    }

    public HealthCareUnit toHealthCareUnit(HealthCareUnitType healthCareUnitType) {
        HealthCareUnit healthCareUnit = new HealthCareUnit();

        healthCareUnit.setArchivedHealthCareProvider(healthCareUnitType.isArchivedHealthCareProvider());
        healthCareUnit.setArchivedHealthCareUnit(healthCareUnitType.isArchivedHealthCareUnit());
        healthCareUnit.setArchivedHealthCareUnitMember(healthCareUnitType.isArchivedHealthCareUnitMember());
        healthCareUnit.setFeignedHealthCareProvider(healthCareUnitType.isFeignedHealthCareProvider());
        healthCareUnit.setFeignedHealthCareUnit(healthCareUnitType.isFeignedHealthCareUnit());
        healthCareUnit.setFeignedHealthCareUnitMember(healthCareUnitType.isFeignedHealthCareUnitMember());
        healthCareUnit.setHealthCareProviderEndDate(healthCareUnitType.getHealthCareProviderEndDate());
        healthCareUnit.setHealthCareProviderHsaId(healthCareUnitType.getHealthCareProviderHsaId());
        healthCareUnit.setHealthCareProviderName(healthCareUnitType.getHealthCareProviderName());
        healthCareUnit.setHealthCareProviderOrgNo(healthCareUnitType.getHealthCareProviderOrgNo());
        healthCareUnit.setHealthCareProviderPublicName(healthCareUnitType.getHealthCareProviderPublicName());
        healthCareUnit.setHealthCareProviderStartDate(healthCareUnitType.getHealthCareProviderStartDate());
        healthCareUnit.setHealthCareUnitEndDate(healthCareUnitType.getHealthCareUnitEndDate());
        healthCareUnit.setHealthCareUnitHsaId(healthCareUnitType.getHealthCareUnitHsaId());
        healthCareUnit.setHealthCareUnitMemberEndDate(healthCareUnitType.getHealthCareUnitMemberEndDate());
        healthCareUnit.setHealthCareUnitMemberHsaId(healthCareUnitType.getHealthCareUnitMemberHsaId());
        healthCareUnit.setHealthCareUnitMemberName(healthCareUnitType.getHealthCareUnitMemberName());
        healthCareUnit.setHealthCareUnitMemberPublicName(healthCareUnitType.getHealthCareUnitMemberPublicName());
        healthCareUnit.setHealthCareUnitMemberStartDate(healthCareUnitType.getHealthCareUnitMemberStartDate());
        healthCareUnit.setHealthCareUnitName(healthCareUnitType.getHealthCareUnitName());
        healthCareUnit.setHealthCareUnitPublicName(healthCareUnitType.getHealthCareUnitPublicName());
        healthCareUnit.setHealthCareUnitStartDate(healthCareUnitType.getHealthCareUnitStartDate());
        healthCareUnit.setUnitIsHealthCareUnit(healthCareUnitType.isUnitIsHealthCareUnit());

        return healthCareUnit;
    }

    public HealthCareUnitMembers toHealthCareUnitMembers(HealthCareUnitMembersType healthCareUnitMembersType) {
        HealthCareUnitMembers healthCareUnitMembers = new HealthCareUnitMembers();

        healthCareUnitMembers.setArchivedHealthCareUnit(healthCareUnitMembersType.isArchivedHealthCareUnit());
        healthCareUnitMembers.setFeignedHealthCareUnit(healthCareUnitMembersType.isFeignedHealthCareUnit());
        if (!healthCareUnitMembersType.getHealthCareUnitMember().isEmpty()) {
            healthCareUnitMembers.setHealthCareUnitMember(healthCareUnitMembersType.getHealthCareUnitMember()
                    .stream().map(this::toHealthCareUnitMember).collect(Collectors.toList()));
        }
        healthCareUnitMembers.setHealthCareUnitHsaId(healthCareUnitMembersType.getHealthCareUnitHsaId());
        healthCareUnitMembers.setHealthCareUnitEndDate(healthCareUnitMembersType.getHealthCareUnitEndDate());
        if (healthCareUnitMembers.getHealthCareProvider() != null) {
            healthCareUnitMembers.setHealthCareProvider(toHealthCareProvider(healthCareUnitMembersType.getHealthCareProvider()));
        }
        healthCareUnitMembers.setHealthCareUnitName(healthCareUnitMembersType.getHealthCareUnitName());
        healthCareUnitMembers.setHealthCareUnitPrescriptionCode(healthCareUnitMembersType.getHealthCareUnitPrescriptionCode());
        healthCareUnitMembers.setHealthCareUnitPublicName(healthCareUnitMembersType.getHealthCareUnitPublicName());
        healthCareUnitMembers.setHealthCareUnitStartDate(healthCareUnitMembersType.getHealthCareUnitStartDate());
        if (healthCareUnitMembersType.getPostalAddress() != null) {
            healthCareUnitMembers.setPostalAddress(healthCareUnitMembersType.getPostalAddress().getAddressLine());
        }
        healthCareUnitMembers.setPostalCode(healthCareUnitMembersType.getPostalCode());
        healthCareUnitMembers.setTelephoneNumber(healthCareUnitMembersType.getTelephoneNumber());

        return healthCareUnitMembers;
    }

    public HealthCareUnitMember toHealthCareUnitMember(HealthCareUnitMemberType healthCareUnitMemberType) {
        HealthCareUnitMember healthCareUnitMember = new HealthCareUnitMember();

        healthCareUnitMember.setArchivedHealthCareUnitMember(healthCareUnitMemberType.isArchivedHealthCareUnitMember());
        healthCareUnitMember.setFeignedHealthCareUnitMember(healthCareUnitMemberType.isFeignedHealthCareUnitMember());
        healthCareUnitMember.setHealthCareUnitMemberEndDate(healthCareUnitMemberType.getHealthCareUnitMemberEndDate());
        healthCareUnitMember.setHealthCareUnitMemberHsaId(healthCareUnitMemberType.getHealthCareUnitMemberHsaId());
        healthCareUnitMember.setHealthCareUnitMemberName(healthCareUnitMemberType.getHealthCareUnitMemberName());
        if (healthCareUnitMemberType.getHealthCareUnitMemberpostalAddress() != null) {
            healthCareUnitMember.setHealthCareUnitMemberpostalAddress(healthCareUnitMemberType
                .getHealthCareUnitMemberpostalAddress().getAddressLine());
        }
        healthCareUnitMember.setHealthCareUnitMemberpostalCode(healthCareUnitMemberType.getHealthCareUnitMemberpostalCode());
        healthCareUnitMember.setHealthCareUnitMemberPrescriptionCode(healthCareUnitMemberType.getHealthCareUnitMemberPrescriptionCode());
        healthCareUnitMember.setHealthCareUnitMemberPublicName(healthCareUnitMemberType.getHealthCareUnitMemberPublicName());
        healthCareUnitMember.setHealthCareUnitMemberStartDate(healthCareUnitMemberType.getHealthCareUnitMemberStartDate());
        healthCareUnitMember.setHealthCareUnitMemberTelephoneNumber(healthCareUnitMemberType.getHealthCareUnitMemberTelephoneNumber());

        return healthCareUnitMember;
    }

    public HealthCareProvider toHealthCareProvider(HealthCareProviderType healthCareProviderType) {
        HealthCareProvider healthCareProvider = new HealthCareProvider();

        healthCareProvider.setArchivedHealthCareProvider(healthCareProviderType.isArchivedHealthCareProvider());
        healthCareProvider.setFeignedHealthCareProvider(healthCareProviderType.isFeignedHealthCareProvider());
        healthCareProvider.setHealthCareProviderEndDate(healthCareProviderType.getHealthCareProviderEndDate());
        healthCareProvider.setHealthCareProviderHsaId(healthCareProviderType.getHealthCareProviderHsaId());
        healthCareProvider.setHealthCareProviderName(healthCareProviderType.getHealthCareProviderName());
        healthCareProvider.setHealthCareProviderOrgNo(healthCareProviderType.getHealthCareProviderOrgNo());
        healthCareProvider.setHealthCareProviderStartDate(healthCareProviderType.getHealthCareProviderStartDate());

        return healthCareProvider;
    }

    public Unit toUnit(UnitType unitType) {
        Unit unit = new Unit();

        if (!unitType.getBusinessClassification().isEmpty()) {
            unit.setBusinessClassification(unitType.getBusinessClassification()
                .stream().map(this::toBusinessClassification).collect(Collectors.toList()));
        }
        unit.setBusinessType(unitType.getBusinessType());
        unit.setCareType(unitType.getCareType());
        unit.setCountyCode(unitType.getCountyCode());
        unit.setCountyName(unitType.getCountyName());
        unit.setFeignedUnit(unitType.isFeignedUnit());
        if (unitType.getGeographicalCoordinatesRt90() != null) {
            unit.setGeographicalCoordinatesRt90(toRt90(unitType.getGeographicalCoordinatesRt90()));
        }
        if (unitType.getGeographicalCoordinatesSWEREF99() != null) {
            unit.setGeographicalCoordinatesSweref99(toSweref99(unitType.getGeographicalCoordinatesSWEREF99()));
        }
        unit.setLocation(unitType.getLocation());
        unit.setMail(unitType.getMail());
        unit.setManagement(unitType.getManagement());
        unit.setMunicipalityCode(unitType.getMunicipalityCode());
        unit.setMunicipalityName(unitType.getMunicipalityName());
        if (unitType.getPostalAddress() != null) {
            unit.setPostalAddress(unitType.getPostalAddress().getAddressLine());
        }
        unit.setPostalCode(unitType.getPostalCode());
        unit.setUnitEndDate(unitType.getUnitEndDate());
        unit.setUnitHsaId(unitType.getUnitHsaId());
        unit.setUnitName(unitType.getUnitName());
        unit.setUnitEndDate(unitType.getUnitEndDate());

        return unit;
    }

    public Unit.GeoCoordRt90 toRt90(GeoCoordRt90Type geoCoordRt90Type) {
        Unit.GeoCoordRt90 geoCoordRt90 = new Unit.GeoCoordRt90();

        geoCoordRt90.setXCoordinate(geoCoordRt90Type.getXCoordinate());
        geoCoordRt90.setYCoordinate(geoCoordRt90Type.getYCoordinate());

        return geoCoordRt90;
    }

    public Unit.GeoCoordSweref99 toSweref99(GeoCoordSWEREF99Type geoCoordSweref99Type) {
        Unit.GeoCoordSweref99 geoCoordSweref99 = new Unit.GeoCoordSweref99();

        geoCoordSweref99.setECoordinate(geoCoordSweref99Type.getECoordinate());
        geoCoordSweref99.setNCoordinate(geoCoordSweref99Type.getNCoordinate());

        return geoCoordSweref99;
    }

    public Unit.BusinessClassification toBusinessClassification(BusinessClassificationType businessClassificationType) {
        Unit.BusinessClassification businessClassification = new Unit.BusinessClassification();

        businessClassification.setBusinessClassificationCode(businessClassificationType.getBusinessClassificationCode());
        businessClassification.setBusinessClassificationName(businessClassificationType.getBusinessClassificationName());

        return businessClassification;
    }

}
