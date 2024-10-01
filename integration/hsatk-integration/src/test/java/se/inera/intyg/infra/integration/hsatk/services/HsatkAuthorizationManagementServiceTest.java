/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.services;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.client.AuthorizationManagementClient;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.hsatk.model.Result;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.OperationEnum;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.ResultCodeEnum;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HCPSpecialityCodesType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HealthCareProfessionalLicenceType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.IIType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.NursePrescriptionRightType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.RestrictionType;

import jakarta.xml.ws.WebServiceException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HsatkAuthorizationManagementServiceTest {

    private static final String HSA_ID = "HSA_ID";
    private static final String PIN = "PIN";
    private static final String CERTID = "CERTID";

    @Mock
    AuthorizationManagementClient authorizationManagementClient;

    @InjectMocks
    HsatkAuthorizationManagementServiceImpl authorizationManagementService;

    @Test
    public void testGetCredentialInformationOK() throws HsaServiceCallException {
        when(authorizationManagementClient.getCredentialInformationForPerson(any(), eq(HSA_ID), any())).thenReturn(buildCredentialReply());

        List<CredentialInformation> informationList = authorizationManagementService.getCredentialInformationForPerson(null, HSA_ID, null);

        Assert.assertNotNull(informationList);
    }

    @Test(expected = WebServiceException.class)
    public void testGetCredentialInformationException() throws HsaServiceCallException {

        when(authorizationManagementClient.getCredentialInformationForPerson(any(), eq(HSA_ID), any())).thenThrow(
            new HsaServiceCallException());

        List<CredentialInformation> informationList = authorizationManagementService.getCredentialInformationForPerson(null, HSA_ID, null);
    }

    @Test
    public void testGetHospCredentialsOK() throws HsaServiceCallException {
        when(authorizationManagementClient.getHospCredentialsForPerson(eq(PIN))).thenReturn(buildHospCredentialReply());
        HospCredentialsForPerson hospCredentialsForPerson = authorizationManagementService.getHospCredentialsForPersonResponseType(PIN);

        Assert.assertNotNull(hospCredentialsForPerson);
    }

    @Test
    public void testGetHospLastUpdateOK() {
        when(authorizationManagementClient.getHospLastUpdate()).thenReturn(LocalDateTime.of(2021, 1, 1, 8, 20));
        LocalDateTime localDateTime = authorizationManagementService.getHospLastUpdate();

        Assert.assertEquals(LocalDateTime.of(2021, 1, 1, 8, 20), localDateTime);
    }

    @Test
    public void testHandleHospCertificationAddOK() {
        when(authorizationManagementClient.handleHospCertificationPerson(eq(CERTID), eq(OperationEnum.ADD), eq(PIN), any())).thenReturn(
            buildHospCertificationReply(ResultCodeEnum.OK));
        Result result = authorizationManagementService.handleHospCertificationPersonResponseType(CERTID, OperationEnum.ADD.value(), PIN,
            null);

        Assert.assertNotNull(result);

        Assert.assertEquals(ResultCodeEnum.OK.value(), result.getResultCode());
    }

    @Test
    public void testHandleHospCertificationRemoveOK() {
        when(authorizationManagementClient.handleHospCertificationPerson(eq(CERTID), eq(OperationEnum.REMOVE), eq(PIN), any())).thenReturn(
            buildHospCertificationReply(ResultCodeEnum.OK));
        Result result = authorizationManagementService.handleHospCertificationPersonResponseType(CERTID, OperationEnum.REMOVE.value(), PIN,
            null);

        Assert.assertNotNull(result);

        Assert.assertEquals(ResultCodeEnum.OK.value(), result.getResultCode());
    }

    private List<CredentialInformationType> buildCredentialReply() {
        List<CredentialInformationType> credentialInformations = new ArrayList<>();
        CredentialInformationType informationType = new CredentialInformationType();

        informationType.setPersonHsaId(HSA_ID);
        IIType iiType = new IIType();
        iiType.setExtension(PIN);
        informationType.setPersonalIdentity(iiType);

        credentialInformations.add(informationType);
        return credentialInformations;
    }

    private GetHospCredentialsForPersonResponseType buildHospCredentialReply() {
        GetHospCredentialsForPersonResponseType responseType = new GetHospCredentialsForPersonResponseType();

        responseType.setHealthcareProfessionalLicenseIdentityNumber("HCPLIN");

        IIType iiType = new IIType();
        iiType.setExtension(PIN);
        responseType.setPersonalIdentityNumber(iiType);

        responseType.setPersonalPrescriptionCode("PPC");
        responseType.getEducationCode().add("EC");

        HealthCareProfessionalLicenceType licenceType = new HealthCareProfessionalLicenceType();
        licenceType.setHealthCareProfessionalLicenceName("HCPLN");
        licenceType.setHealthCareProfessionalLicenceCode("HCPLC");
        responseType.getHealthCareProfessionalLicence().add(licenceType);

        HCPSpecialityCodesType specialityType = new HCPSpecialityCodesType();
        specialityType.setSpecialityName("SN");
        specialityType.setSpecialityCode("SC");
        specialityType.setHealthCareProfessionalLicenceCode("HCPLC");
        responseType.getHealthCareProfessionalLicenceSpeciality().add(specialityType);

        NursePrescriptionRightType rightType = new NursePrescriptionRightType();
        rightType.setHealthCareProfessionalLicence("HCPL");
        rightType.setPrescriptionRight(true);
        responseType.getNursePrescriptionRight().add(rightType);

        RestrictionType restrictionType = new RestrictionType();
        restrictionType.setRestrictionName("RN");
        restrictionType.setRestrictionCode("RC");
        restrictionType.setHealthCareProfessionalLicenceCode("HCPLC");
        responseType.getRestrictions().add(restrictionType);
        return responseType;
    }

    private HandleHospCertificationPersonResponseType buildHospCertificationReply(ResultCodeEnum type) {
        HandleHospCertificationPersonResponseType responseType = new HandleHospCertificationPersonResponseType();

        responseType.setResultText(type.name());
        responseType.setResultCode(type);

        return responseType;
    }

}
