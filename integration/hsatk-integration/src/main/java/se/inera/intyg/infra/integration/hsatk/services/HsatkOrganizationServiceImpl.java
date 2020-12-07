package se.inera.intyg.infra.integration.hsatk.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.client.OrganizationClient;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.*;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMemberType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HsatkOrganizationServiceImpl implements HsatkOrganizationService {

    private static final Logger LOG = LoggerFactory.getLogger(HsatkAuthorizationManagementServiceImpl.class);

    @Autowired
    OrganizationClient organizationClient;

    @Override
    public List<HealthCareProvider> getHealthCareProvider(String healthCareProviderHsaId, String healthCareProviderOrgNo) {
        List<HealthCareProvider> healthCareProviderList = new ArrayList<>();

        try {
            healthCareProviderList = organizationClient.getHealthCareProvider(healthCareProviderHsaId, healthCareProviderOrgNo)
                    .stream().map(this::toHealthCareProvider).collect(Collectors.toList());
        } catch (HsaServiceCallException e) {
            LOG.error("");
        } catch (Exception e) {
            LOG.error("");
        }
        return healthCareProviderList;
    }

    @Override
    public HealthCareUnit getHealthCareUnit(String healthCareUnitMemberHsaId) {
        HealthCareUnit healthCareUnit = new HealthCareUnit();

        try {
            healthCareUnit = toHealthCareUnit(organizationClient.getHealthCareUnit(healthCareUnitMemberHsaId));
        } catch (HsaServiceCallException e) {
            LOG.error("");
        } catch (Exception e) {
            LOG.error("");
        }
        return healthCareUnit;
    }

    @Override
    public HealthCareUnitMembers getHealthCareUnitMembers(String healtCareUnitHsaId) {
        HealthCareUnitMembers healthCareUnitMembers = new HealthCareUnitMembers();
        try {
            healthCareUnitMembers = toHealthCareUnitMembers(organizationClient.getHealthCareUnitMembers(healtCareUnitHsaId));
        } catch (HsaServiceCallException e) {
            LOG.error("");
        } catch (Exception e) {
            LOG.error("Unexpected error occurred while getting HealthCareUnitMembers : {}", e.getLocalizedMessage());
        }
        return healthCareUnitMembers;
    }

    @Override
    public Unit getUnit(String unitHsaId, String profile) {
        ProfileEnum profileEnum = ProfileEnum.BASIC;
        GetUnitType getUnitType = new GetUnitType();
        getUnitType.setUnitHsaId(unitHsaId);

        Unit unit = new Unit();

        if (StringUtils.isNotEmpty(profile)) {
            profileEnum = ProfileEnum.fromValue(profile);
        }
        try {
             unit = toUnit(organizationClient.getUnit(unitHsaId, profileEnum));

        } catch (HsaServiceCallException e) {
            LOG.error("Failed to get Unit from HSA: {}", e.getLocalizedMessage());

        } catch (Exception e) {
            LOG.error("Unexpected error occurred during getUnit method: {}", e.getLocalizedMessage());
        }
        return unit;
    }

    private HealthCareProvider toHealthCareProvider(se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.HealthCareProviderType healthCareProviderType) {
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

    private HealthCareUnit toHealthCareUnit(HealthCareUnitType healthCareUnitType) {
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

    private HealthCareUnitMembers toHealthCareUnitMembers(HealthCareUnitMembersType healthCareUnitMembersType) {
        HealthCareUnitMembers healthCareUnitMembers = new HealthCareUnitMembers();

        healthCareUnitMembers.setArchivedHealthCareUnit(healthCareUnitMembersType.isArchivedHealthCareUnit());
        healthCareUnitMembers.setFeignedHealthCareUnit(healthCareUnitMembersType.isFeignedHealthCareUnit());
        if (healthCareUnitMembersType.getHealthCareUnitMember() != null) {
            healthCareUnitMembers.setHealthCareUnitMember(healthCareUnitMembersType.getHealthCareUnitMember()
                    .stream().map(this::toHealthCareUnitMember).collect(Collectors.toList()));
        }
        healthCareUnitMembers.setHealthCareUnitHsaId(healthCareUnitMembersType.getHealthCareUnitHsaId());
        healthCareUnitMembers.setHealthCareUnitEndDate(healthCareUnitMembersType.getHealthCareUnitEndDate());
        healthCareUnitMembers.setHealthCareProvider(toHealthCareProvider(healthCareUnitMembersType.getHealthCareProvider()));
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

    private HealthCareUnitMember toHealthCareUnitMember(HealthCareUnitMemberType healthCareUnitMemberType) {
        HealthCareUnitMember healthCareUnitMember = new HealthCareUnitMember();

        healthCareUnitMember.setArchivedHealthCareUnitMember(healthCareUnitMemberType.isArchivedHealthCareUnitMember());
        healthCareUnitMember.setFeignedHealthCareUnitMember(healthCareUnitMemberType.isFeignedHealthCareUnitMember());
        healthCareUnitMember.setHealthCareUnitMemberEndDate(healthCareUnitMemberType.getHealthCareUnitMemberEndDate());
        healthCareUnitMember.setHealthCareUnitMemberHsaId(healthCareUnitMemberType.getHealthCareUnitMemberHsaId());
        healthCareUnitMember.setHealthCareUnitMemberName(healthCareUnitMemberType.getHealthCareUnitMemberName());
        if (healthCareUnitMemberType.getHealthCareUnitMemberpostalAddress() != null) {
            healthCareUnitMember.setHealthCareUnitMemberpostalAddress(healthCareUnitMemberType.getHealthCareUnitMemberpostalAddress().getAddressLine());
        }
        healthCareUnitMember.setHealthCareUnitMemberpostalCode(healthCareUnitMemberType.getHealthCareUnitMemberpostalCode());
        healthCareUnitMember.setHealthCareUnitMemberPrescriptionCode(healthCareUnitMemberType.getHealthCareUnitMemberPrescriptionCode());
        healthCareUnitMember.setHealthCareUnitMemberPublicName(healthCareUnitMemberType.getHealthCareUnitMemberPublicName());
        healthCareUnitMember.setHealthCareUnitMemberStartDate(healthCareUnitMemberType.getHealthCareUnitMemberStartDate());
        healthCareUnitMember.setHealthCareUnitMemberTelephoneNumber(healthCareUnitMemberType.getHealthCareUnitMemberTelephoneNumber());

        return healthCareUnitMember;
    }

    private HealthCareProvider toHealthCareProvider(HealthCareProviderType healthCareProviderType) {
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

    private Unit toUnit(UnitType unitType) {
        Unit unit = new Unit();

        if (unitType.getBusinessClassification() != null) {
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
            unit.setGeographicalCoordinatesSWEREF99(toSWEREF99(unitType.getGeographicalCoordinatesSWEREF99()));
        }
        unit.setLocation(unitType.getLocation());
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

    private Unit.GeoCoordRt90 toRt90(GeoCoordRt90Type geoCoordRt90Type) {
        Unit.GeoCoordRt90 geoCoordRt90 = new Unit.GeoCoordRt90();

        geoCoordRt90.setXCoordinate(geoCoordRt90Type.getXCoordinate());
        geoCoordRt90.setYCoordinate(geoCoordRt90Type.getYCoordinate());

        return geoCoordRt90;
    }

    private Unit.GeoCoordSWEREF99 toSWEREF99(GeoCoordSWEREF99Type geoCoordSWEREF99Type) {
        Unit.GeoCoordSWEREF99 geoCoordSWEREF99 = new Unit.GeoCoordSWEREF99();

        geoCoordSWEREF99.setECoordinate(geoCoordSWEREF99Type.getECoordinate());
        geoCoordSWEREF99.setNCoordinate(geoCoordSWEREF99Type.getNCoordinate());

        return geoCoordSWEREF99;
    }

    private Unit.BusinessClassification toBusinessClassification(BusinessClassificationType businessClassificationType) {
        Unit.BusinessClassification businessClassification = new Unit.BusinessClassification();

        businessClassification.setBusinessClassificationCode(businessClassificationType.getBusinessClassificationCode());
        businessClassification.setBusinessClassificationName(businessClassificationType.getBusinessClassificationName());

        return businessClassification;
    }
}
