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

import static se.inera.intyg.infra.integration.hsatk.constants.HsaIntegrationApiConstants.HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.ws.WebServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.client.AuthorizationManagementClient;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.hsatk.model.Result;
import se.inera.intyg.infra.integration.hsatk.util.HsaTypeConverter;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.OperationEnum;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;

@Service
@Profile("!" + HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE)
public class HsatkAuthorizationManagementServiceImpl implements HsatkAuthorizationManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(HsatkAuthorizationManagementServiceImpl.class);

    HsaTypeConverter hsaTypeConverter = new HsaTypeConverter();

    @Autowired
    AuthorizationManagementClient authorizationManagementClient;

    @Override
    public List<CredentialInformation> getCredentialInformationForPerson(String personalIdentityNumber,
        String personHsaId, String profile) {

        List<CredentialInformationType> credentialInformationTypeList;

        try {
            credentialInformationTypeList = authorizationManagementClient.getCredentialInformationForPerson(
                personalIdentityNumber, personHsaId, profile);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        return credentialInformationTypeList.stream().map(hsaTypeConverter::toCredentialInformation).collect(Collectors.toList());
    }

    @Override
    public HospCredentialsForPerson getHospCredentialsForPersonResponseType(String personalIdentityNumber) {

        GetHospCredentialsForPersonResponseType responseType = null;
        HospCredentialsForPerson hospCredentialsForPerson = new HospCredentialsForPerson();
        try {
            responseType = authorizationManagementClient.getHospCredentialsForPerson(personalIdentityNumber);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        hospCredentialsForPerson.setEducationCode(responseType.getEducationCode());
        hospCredentialsForPerson.setPersonalPrescriptionCode(responseType.getPersonalPrescriptionCode());
        if (responseType.getPersonalIdentityNumber() != null) {
            hospCredentialsForPerson.setPersonalIdentityNumber(responseType.getPersonalIdentityNumber().getExtension());
        }
        hospCredentialsForPerson.setHealthCareProfessionalLicence(
            responseType.getHealthCareProfessionalLicence()
                .stream()
                .map(hsaTypeConverter::toHealthCareProfessionalLicence)
                .collect(Collectors.toList()));
        hospCredentialsForPerson.setRestrictions(responseType.getRestrictions()
            .stream()
            .map(hsaTypeConverter::toRestriction)
            .collect(Collectors.toList()));
        hospCredentialsForPerson.setHealthCareProfessionalLicenceSpeciality(
            responseType.getHealthCareProfessionalLicenceSpeciality()
                .stream()
                .map(hsaTypeConverter::toHCPSpecialityCodes)
                .collect(Collectors.toList()));

        hospCredentialsForPerson.setHealthcareProfessionalLicenseIdentityNumber(responseType
            .getHealthcareProfessionalLicenseIdentityNumber());
        hospCredentialsForPerson.setNursePrescriptionRight(
            responseType.getNursePrescriptionRight()
                .stream()
                .map(hsaTypeConverter::toNursePrescriptionRight)
                .collect(Collectors.toList()));

        return hospCredentialsForPerson;
    }

    @Override
    public LocalDateTime getHospLastUpdate() {
        LocalDateTime hospLastUpdate;

        try {
            hospLastUpdate = authorizationManagementClient.getHospLastUpdate();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
        return hospLastUpdate;
    }

    @Override
    public Result handleHospCertificationPersonResponseType(String certificationId, String operation,
        String personalIdentityNumber, String reason) {
        HandleHospCertificationPersonResponseType responseType;
        Result result = new Result();

        try {
            responseType = authorizationManagementClient.handleHospCertificationPerson(
                certificationId, OperationEnum.fromValue(operation), personalIdentityNumber, reason);

            result.setResultText(responseType.getResultText());
            result.setResultCode(responseType.getResultCode().value());

        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }

        return result;
    }

}
