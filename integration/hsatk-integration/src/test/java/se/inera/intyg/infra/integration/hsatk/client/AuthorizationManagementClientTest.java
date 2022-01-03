/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedperson.v2.rivtabp21.GetCredentialsForPersonIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedpersonresponder.v2.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedpersonresponder.v2.GetCredentialsForPersonIncludingProtectedPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforperson.v1.rivtabp21.GetHospCredentialsForPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdate.v1.rivtabp21.GetHospLastUpdateResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdateresponder.v1.GetHospLastUpdateResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdateresponder.v1.GetHospLastUpdateType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationperson.v1.rivtabp21.HandleHospCertificationPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.OperationEnum;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.ResultCodeEnum;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HCPSpecialityCodesType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HealthCareProfessionalLicenceType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.IIType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.RestrictionType;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationManagementClientTest {

    private static final String HSA_ID = "hsa-id";
    private static final String PERSONAL_PRESCRIPTION_CODE = "ppc";
    private static final String PERSONAL_IDENTITY_NUMBER = "pin";
    private static final String HCPLICENCE_IDENTITY_NUMBER = "hcplin";

    @Mock
    GetCredentialsForPersonIncludingProtectedPersonResponderInterface getCredentialsForPersonIncludingProtectedPersonResponderInterface;

    @Mock
    GetHospCredentialsForPersonResponderInterface getHospCredentialsForPersonResponderInterface;

    @Mock
    HandleHospCertificationPersonResponderInterface handleHospCertificationPersonResponderInterface;

    @Mock
    GetHospLastUpdateResponderInterface getHospLastUpdateResponderInterface;

    @InjectMocks
    private AuthorizationManagementClient authorizationManagementClient;

    @Test
    public void testGetCredentialInformationOk() throws HsaServiceCallException {
        when(getCredentialsForPersonIncludingProtectedPersonResponderInterface.getCredentialsForPersonIncludingProtectedPerson(
                or(isNull(), anyString()),
                any(GetCredentialsForPersonIncludingProtectedPersonType.class))
        ).thenReturn(buildCredentialInformationResponse());

        List<CredentialInformationType> authorizationsForPerson = authorizationManagementClient.getCredentialInformationForPerson(null, HSA_ID, null);
        assertNotNull(authorizationsForPerson);
    }

    @Test
    public void testGetHospCredentialOk() throws HsaServiceCallException {
        when(getHospCredentialsForPersonResponderInterface.getHospCredentialsForPerson(
                or(isNull(), anyString()),
                any(GetHospCredentialsForPersonType.class))
        ).thenReturn(buildHospCredentialResponse());

        GetHospCredentialsForPersonResponseType responseType = authorizationManagementClient.getHospCredentialsForPerson(null);
        assertNotNull(responseType);
    }

    @Test
    public void testHandleHospCertificationOk() throws HsaServiceCallException {
        when(handleHospCertificationPersonResponderInterface.handleHospCertificationPerson(
                or(isNull(), anyString()),
                any(HandleHospCertificationPersonType.class))
        ).thenReturn(buildHandleHospCertificationResponse());

        HandleHospCertificationPersonResponseType responseType = authorizationManagementClient.handleHospCertificationPerson(
                null, OperationEnum.ADD, HSA_ID, "Add");
        assertEquals(responseType.getResultCode(), ResultCodeEnum.OK);
    }

    @Test
    public void testGetHospLastUpdateOk() throws HsaServiceCallException {
        when(getHospLastUpdateResponderInterface.getHospLastUpdate(
                or(isNull(), anyString()),
                any(GetHospLastUpdateType.class))
        ).thenReturn(buildHospLastUpdateResponse());

        LocalDateTime hospLastUpdate = authorizationManagementClient.getHospLastUpdate();
        assertNotNull(hospLastUpdate);
    }

    private GetCredentialsForPersonIncludingProtectedPersonResponseType buildCredentialInformationResponse() {
        GetCredentialsForPersonIncludingProtectedPersonResponseType resp = new GetCredentialsForPersonIncludingProtectedPersonResponseType();

        CredentialInformationType credentialInformationType = new CredentialInformationType();

        resp.getCredentialInformation().add(credentialInformationType);
        return resp;
    }

    private GetHospCredentialsForPersonResponseType buildHospCredentialResponse() {
        GetHospCredentialsForPersonResponseType resp = new GetHospCredentialsForPersonResponseType();
        resp.setPersonalPrescriptionCode(PERSONAL_PRESCRIPTION_CODE);

        IIType pin = new IIType();
        pin.setExtension(PERSONAL_IDENTITY_NUMBER);

        resp.setPersonalIdentityNumber(pin);

        resp.setHealthcareProfessionalLicenseIdentityNumber(HCPLICENCE_IDENTITY_NUMBER);
        HealthCareProfessionalLicenceType healthCareProfessionalLicence = new HealthCareProfessionalLicenceType();
        resp.getHealthCareProfessionalLicence().add(healthCareProfessionalLicence);
        RestrictionType restrictionType = new RestrictionType();
        resp.getRestrictions().add(restrictionType);
        HCPSpecialityCodesType hcpSpecialityCodesType = new HCPSpecialityCodesType();
        resp.getHealthCareProfessionalLicenceSpeciality().add(hcpSpecialityCodesType);
        resp.getNursePrescriptionRight();
        return resp;
    }

    private HandleHospCertificationPersonResponseType buildHandleHospCertificationResponse() {
        HandleHospCertificationPersonResponseType resp = new HandleHospCertificationPersonResponseType();
        resp.setResultCode(ResultCodeEnum.OK);
        resp.setResultText("OK");
        return resp;
    }

    private GetHospLastUpdateResponseType buildHospLastUpdateResponse() {
        GetHospLastUpdateResponseType responseType = new GetHospLastUpdateResponseType();
        responseType.setLastUpdate(LocalDateTime.of(1, 2, 3, 4, 5));
        return responseType;
    }
}
