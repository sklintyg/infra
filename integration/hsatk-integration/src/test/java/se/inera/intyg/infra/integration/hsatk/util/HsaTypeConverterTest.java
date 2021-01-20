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
package se.inera.intyg.infra.integration.hsatk.util;

import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProfessionalLicence;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProvider;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CommissionRightType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CommissionType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HCPSpecialityCodesType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HealthCareProfessionalLicenceType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HsaSystemRoleType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.IIType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.NursePrescriptionRightType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.RestrictionType;
import se.riv.infrastructure.directory.employee.v2.HealthCareProfessionalLicenceSpecialityType;
import se.riv.infrastructure.directory.employee.v2.PaTitleType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.HealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMemberType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.BusinessClassificationType;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.GeoCoordRt90Type;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.GeoCoordSWEREF99Type;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.UnitType;
import se.riv.infrastructure.directory.organization.v3.AddressType;

@RunWith(MockitoJUnitRunner.class)
public class HsaTypeConverterTest {

    private static final String HSA_ID = "HID";
    private static final String PERSONALID = "202001010101";
    private static final String GIVENNAME = "Namn";
    private static final String SURNAME = "Namnsson";
    private static final String LICENCE = "Läkare";
    private static final String LICENCE_CODE = "LK";
    private static final String LICENCE_NAME = "Läkare";
    private static final String LICENCE_IDENTITY_NUMBER = "HCPLIN";
    private static final String GPC = "GPC";
    private static final String OC = "OC";
    private static final String PTC = "PTC";
    private static final String PTN = "PTN";

    private static final String PRESCRIPTION_CODE = "PPC";
    private static final String ROLE = "ROLE";
    private static final String SID = "SID";
    private static final String ACTIVITY = "Activity";
    private static final String INFORMATION_CLASS = "IClass";
    private static final String SCOPE = "Scope";
    private static final String PURPOSE = "PURPOSE";
    private static final String NAME = "Name";
    private static final String CODE = "Code";
    private static final String SPECIALITY_NAME = "Name";
    private static final String SPECIALITY_CODE = "Code";
    private static final String ORGNO = "OrgNo";

    private static final String PUBLIC_NAME = "Public Name";
    private static final String POSTAL_CODE = "Postal_Code";
    private static final String ADDRESS_LINE = "Street 1";
    private static final String TELEPHONE_NUMBER = "Number";
    private static final String LOCATION = "Location";
    private static final String MAIL = "Mail@ma.il";

    private static final String BUSINESS_TYPE = "Business";
    private static final String BC_CODE = "BC_Code";
    private static final String BC_NAME = "BC-Name";
    private static final String CARE = "Care";
    private static final String MANAGEMENT = "Management";
    private static final String RC = "RC";
    private static final String RN = "RC";

    private static final String AGE = "50";
    private static final String GENDER = "Hen";
    private static final LocalDateTime END_DATE = LocalDateTime.of(2020, 1, 1, 2, 0);
    private static final LocalDateTime START_DATE = LocalDateTime.of(2020, 1, 1, 1, 0);
    private static final String TITLE = "Title";

    private static final String X = "X";
    private static final String Y = "Y";
    private static final String E = "E";
    private static final String N = "N";

    private final AddressType POSTAL_ADDRESS_v3 = new AddressType();
    private final se.riv.infrastructure.directory.organization.v2.AddressType POSTAL_ADDRESS_v2 = new se.riv.infrastructure.directory.organization.v2.AddressType();

    private HsaTypeConverter hsaTypeConverter = new HsaTypeConverter();

    @Before
    public void setup() {
        POSTAL_ADDRESS_v3.getAddressLine().add(ADDRESS_LINE);
        POSTAL_ADDRESS_v2.getAddressLine().add(ADDRESS_LINE);
    }

    @Test
    public void testHealthCareProfessionalLicenceOK() {
        HealthCareProfessionalLicenceType professionalLicenceType = new HealthCareProfessionalLicenceType();
        professionalLicenceType.setHealthCareProfessionalLicenceCode(LICENCE_CODE);
        professionalLicenceType.setHealthCareProfessionalLicenceName(LICENCE_NAME);
        HealthCareProfessionalLicence licence = hsaTypeConverter.toHealthCareProfessionalLicence(professionalLicenceType);
        Assert.assertNotNull(licence);
        Assert.assertEquals(LICENCE_CODE, licence.getHealthCareProfessionalLicenceCode());
        Assert.assertEquals(LICENCE_NAME, licence.getHealthCareProfessionalLicenceName());
    }

    @Test
    public void testCredentialInformationOK() {


        CommissionRightType commissionRightType = new CommissionRightType();
        commissionRightType.setActivity(ACTIVITY);
        commissionRightType.setInformationClass(INFORMATION_CLASS);
        commissionRightType.setScope(SCOPE);


        CommissionType commissionType = new CommissionType();
        commissionType.getCommissionRight().add(commissionRightType);
        commissionType.setCommissionPurpose(PURPOSE);
        commissionType.setHealthCareProviderHsaId(HSA_ID);
        commissionType.setHealthCareProviderName(NAME);
        commissionType.setHealthCareUnitHsaId(HSA_ID);
        commissionType.setHealthCareUnitName(NAME);
        commissionType.setHealthCareUnitStartDate(START_DATE);
        commissionType.setHealthCareUnitEndDate(END_DATE);

        HCPSpecialityCodesType hcpSpecialityCodesType = new HCPSpecialityCodesType();
        hcpSpecialityCodesType.setHealthCareProfessionalLicenceCode(LICENCE_CODE);
        hcpSpecialityCodesType.setSpecialityCode(SPECIALITY_CODE);
        hcpSpecialityCodesType.setSpecialityName(SPECIALITY_NAME);

        HsaSystemRoleType hsaSystemRoleType = new HsaSystemRoleType();
        hsaSystemRoleType.setSystemId(SID);
        hsaSystemRoleType.setRole(ROLE);

        IIType iiType = new IIType();
        iiType.setExtension(PERSONALID);
        NursePrescriptionRightType nursePrescriptionRightType = new NursePrescriptionRightType();


        CredentialInformationType credentialInformationType = new CredentialInformationType();
        credentialInformationType.setPersonalPrescriptionCode(PRESCRIPTION_CODE);
        credentialInformationType.setPersonalIdentity(iiType);
        credentialInformationType.setMiddleAndSurName(SURNAME);
        credentialInformationType.setGivenName(GIVENNAME);
        credentialInformationType.setHealthcareProfessionalLicenseIdentityNumber(LICENCE_IDENTITY_NUMBER);
        credentialInformationType.setPersonHsaId(HSA_ID);
        credentialInformationType.getNursePrescriptionRight().add(nursePrescriptionRightType);
        credentialInformationType.getHsaSystemRole().add(hsaSystemRoleType);
        credentialInformationType.getHealthCareProfessionalLicence().add(LICENCE);
        credentialInformationType.getHealthCareProfessionalLicenceSpeciality().add(hcpSpecialityCodesType);
        credentialInformationType.getCommission().add(commissionType);
        credentialInformationType.getGroupPrescriptionCode().add(GPC);
        credentialInformationType.getHealthCareProfessionalLicenceCode().add(LICENCE_CODE);
        credentialInformationType.getOccupationalCode().add(OC);
        credentialInformationType.getPaTitleCode().add(PTC);

        CredentialInformation credentialInformation = hsaTypeConverter.toCredentialInformation(credentialInformationType);
        Assert.assertNotNull(credentialInformation);
    }

    @Test
    public void testRestrictionOK() {

        RestrictionType restrictionType = new RestrictionType();
        restrictionType.setHealthCareProfessionalLicenceCode(LICENCE_CODE);
        restrictionType.setRestrictionCode(RC);
        restrictionType.setRestrictionName(RN);

        HospCredentialsForPerson.Restriction restriction = hsaTypeConverter.toRestriction(restrictionType);
        Assert.assertNotNull(restriction);
    }

    @Test
    public void testPersonInformationOK() {

        HealthCareProfessionalLicenceSpecialityType licenceSpecialityType = new HealthCareProfessionalLicenceSpecialityType();
        licenceSpecialityType.setHealthCareProfessionalLicence(LICENCE);
        licenceSpecialityType.setSpecialityCode(SPECIALITY_CODE);
        licenceSpecialityType.setSpecialityName(SPECIALITY_NAME);

        PaTitleType paTitleType = new PaTitleType();
        paTitleType.setPaTitleCode(PTC);
        paTitleType.setPaTitleName(PTN);

        PersonInformationType personInformationType = new PersonInformationType();
        personInformationType.setPersonHsaId(HSA_ID);
        personInformationType.setAge(AGE);
        personInformationType.setGender(GENDER);
        personInformationType.setGivenName(GIVENNAME);
        personInformationType.setMiddleAndSurName(SURNAME);
        personInformationType.getHealthCareProfessionalLicence().add(LICENCE);
        personInformationType.getHealthCareProfessionalLicenceSpeciality().add(licenceSpecialityType);
        personInformationType.getPaTitle().add(paTitleType);
        personInformationType.setPersonEndDate(END_DATE);
        personInformationType.setPersonStartDate(START_DATE);
        personInformationType.setPersonHsaId(HSA_ID);
        personInformationType.getSpecialityCode().add(SPECIALITY_CODE);
        personInformationType.getSpecialityName().add(SPECIALITY_NAME);
        personInformationType.setTitle(TITLE);

        PersonInformation personInformation = hsaTypeConverter.toPersonInformation(personInformationType);
        Assert.assertNotNull(personInformation);
    }

    @Test
    public void testHealthCareProviderV1OK() {
        HealthCareProviderType healthCareProviderType = new HealthCareProviderType();
        healthCareProviderType.setHealthCareProviderEndDate(END_DATE);
        healthCareProviderType.setHealthCareProviderStartDate(START_DATE);
        healthCareProviderType.setHealthCareProviderHsaId(HSA_ID);
        healthCareProviderType.setHealthCareProviderName(NAME);
        healthCareProviderType.setHealthCareProviderOrgNo(ORGNO);

        HealthCareProvider healthCareProvider = hsaTypeConverter.toHealthCareProvider(healthCareProviderType);
        Assert.assertNotNull(healthCareProvider);
    }

    @Test
    public void testHealthCareProviderV2OK() {
        se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareProviderType healthCareProviderType
                = new se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareProviderType();
        healthCareProviderType.setHealthCareProviderEndDate(END_DATE);
        healthCareProviderType.setHealthCareProviderStartDate(START_DATE);
        healthCareProviderType.setHealthCareProviderHsaId(HSA_ID);
        healthCareProviderType.setHealthCareProviderName(NAME);
        healthCareProviderType.setHealthCareProviderOrgNo(ORGNO);

        HealthCareProvider healthCareProvider = hsaTypeConverter.toHealthCareProvider(healthCareProviderType);
        Assert.assertNotNull(healthCareProvider);
    }

    @Test
    public void testHealthCareUnitOK() {
        HealthCareUnitType healthCareUnitType = new HealthCareUnitType();
        healthCareUnitType.setHealthCareProviderEndDate(END_DATE);
        healthCareUnitType.setHealthCareProviderHsaId(HSA_ID);
        healthCareUnitType.setHealthCareProviderName(NAME);
        healthCareUnitType.setHealthCareProviderOrgNo(ORGNO);
        healthCareUnitType.setHealthCareProviderPublicName(PUBLIC_NAME);
        healthCareUnitType.setHealthCareProviderStartDate(START_DATE);
        healthCareUnitType.setHealthCareUnitEndDate(END_DATE);
        healthCareUnitType.setHealthCareUnitHsaId(HSA_ID);
        healthCareUnitType.setHealthCareUnitMemberEndDate(END_DATE);
        healthCareUnitType.setHealthCareUnitMemberHsaId(HSA_ID);
        healthCareUnitType.setHealthCareUnitMemberName(NAME);
        healthCareUnitType.setHealthCareUnitMemberPublicName(PUBLIC_NAME);
        healthCareUnitType.setHealthCareUnitMemberStartDate(START_DATE);
        healthCareUnitType.setHealthCareUnitName(NAME);
        healthCareUnitType.setHealthCareUnitPublicName(PUBLIC_NAME);
        healthCareUnitType.setHealthCareUnitStartDate(START_DATE);

        HealthCareUnit healthCareUnit = hsaTypeConverter.toHealthCareUnit(healthCareUnitType);
        Assert.assertNotNull(healthCareUnit);
    }

    @Test
    public void testHealthCareUnitMembersOK() {
        HealthCareUnitMemberType healthCareUnitMemberType = new HealthCareUnitMemberType();
        healthCareUnitMemberType.setHealthCareUnitMemberEndDate(END_DATE);
        healthCareUnitMemberType.setHealthCareUnitMemberStartDate(START_DATE);
        healthCareUnitMemberType.setHealthCareUnitMemberHsaId(HSA_ID);
        healthCareUnitMemberType.setHealthCareUnitMemberName(NAME);
        healthCareUnitMemberType.setHealthCareUnitMemberpostalAddress(POSTAL_ADDRESS_v2);
        healthCareUnitMemberType.setHealthCareUnitMemberpostalCode(POSTAL_CODE);
        healthCareUnitMemberType.getHealthCareUnitMemberPrescriptionCode().add(PRESCRIPTION_CODE);
        healthCareUnitMemberType.setHealthCareUnitMemberPublicName(PUBLIC_NAME);
        healthCareUnitMemberType.getHealthCareUnitMemberTelephoneNumber().add(TELEPHONE_NUMBER);

        se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareProviderType healthCareProviderType
                = new se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareProviderType();
        healthCareProviderType.setHealthCareProviderEndDate(END_DATE);
        healthCareProviderType.setHealthCareProviderStartDate(START_DATE);
        healthCareProviderType.setHealthCareProviderHsaId(HSA_ID);
        healthCareProviderType.setHealthCareProviderName(NAME);
        healthCareProviderType.setHealthCareProviderOrgNo(ORGNO);

        HealthCareUnitMembersType healthCareUnitMembersType = new HealthCareUnitMembersType();
        healthCareUnitMembersType.getHealthCareUnitMember().add(healthCareUnitMemberType);
        healthCareUnitMembersType.setHealthCareUnitEndDate(END_DATE);
        healthCareUnitMembersType.setHealthCareUnitStartDate(START_DATE);
        healthCareUnitMembersType.setHealthCareUnitHsaId(HSA_ID);
        healthCareUnitMembersType.getHealthCareUnitPrescriptionCode().add(PRESCRIPTION_CODE);
        healthCareUnitMembersType.setHealthCareUnitPublicName(PUBLIC_NAME);
        healthCareUnitMembersType.setPostalAddress(POSTAL_ADDRESS_v2);
        healthCareUnitMembersType.setPostalCode(POSTAL_CODE);
        healthCareUnitMembersType.getTelephoneNumber().add(TELEPHONE_NUMBER);
        healthCareUnitMembersType.setHealthCareProvider(healthCareProviderType);

        HealthCareUnitMembers healthCareUnitMembers = hsaTypeConverter.toHealthCareUnitMembers(healthCareUnitMembersType);
        Assert.assertNotNull(healthCareUnitMembers);
    }


    @Test
    public void testUnitOK() {

        BusinessClassificationType businessClassificationType = new BusinessClassificationType();
        businessClassificationType.setBusinessClassificationCode(BC_CODE);
        businessClassificationType.setBusinessClassificationName(BC_NAME);

        GeoCoordSWEREF99Type geoCoordSWEREF99Type = new GeoCoordSWEREF99Type();
        geoCoordSWEREF99Type.setECoordinate(E);
        geoCoordSWEREF99Type.setNCoordinate(N);

        GeoCoordRt90Type geoCoordRt90Type = new GeoCoordRt90Type();
        geoCoordRt90Type.setXCoordinate(X);
        geoCoordRt90Type.setYCoordinate(Y);

        UnitType unitType = new UnitType();
        unitType.getBusinessClassification().add(businessClassificationType);
        unitType.getBusinessType().add(BUSINESS_TYPE);
        unitType.getCareType().add(CARE);
        unitType.setCountyCode(CODE);
        unitType.setCountyName(NAME);
        unitType.setGeographicalCoordinatesRt90(geoCoordRt90Type);
        unitType.setGeographicalCoordinatesSWEREF99(geoCoordSWEREF99Type);
        unitType.setLocation(LOCATION);
        unitType.setMail(MAIL);
        unitType.getManagement().add(MANAGEMENT);
        unitType.setMunicipalityCode(CODE);
        unitType.setMunicipalityName(NAME);
        unitType.setPostalAddress(POSTAL_ADDRESS_v3);
        unitType.setUnitEndDate(END_DATE);
        unitType.setUnitEndDate(START_DATE);
        unitType.setUnitHsaId(HSA_ID);
        unitType.setUnitName(NAME);


        Unit unit = hsaTypeConverter.toUnit(unitType);
        Assert.assertNotNull(unit);
    }
}
