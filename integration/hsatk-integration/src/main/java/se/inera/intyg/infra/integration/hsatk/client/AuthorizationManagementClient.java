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
package se.inera.intyg.infra.integration.hsatk.client;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;

@Service
public class AuthorizationManagementClient {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationManagementClient.class);

    @Autowired
    private GetCredentialsForPersonIncludingProtectedPersonResponderInterface
        getCredentialsForPersonIncludingProtectedPersonResponderInterface;

    @Autowired
    private GetHospCredentialsForPersonResponderInterface getHospCredentialsForPersonResponderInterface;

    @Autowired
    private GetHospLastUpdateResponderInterface getHospLastUpdateResponderInterface;

    @Autowired
    private HandleHospCertificationPersonResponderInterface handleHospCertificationPersonResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    private static boolean includeFeignedObject = false;

    public List<CredentialInformationType> getCredentialInformationForPerson(String personalIdentityNumber,
        String personHsaId,
        String profile)
        throws HsaServiceCallException {

        GetCredentialsForPersonIncludingProtectedPersonType parameters = new GetCredentialsForPersonIncludingProtectedPersonType();
        parameters.setIncludeFeignedObject(includeFeignedObject);
        parameters.setPersonalIdentityNumber(personalIdentityNumber);
        parameters.setPersonHsaId(personHsaId);
        parameters.setProfile(profile);

        GetCredentialsForPersonIncludingProtectedPersonResponseType response =
            getCredentialsForPersonIncludingProtectedPersonResponderInterface
                .getCredentialsForPersonIncludingProtectedPerson(logicalAddress, parameters);
        if (response == null || response.getCredentialInformation().isEmpty()) {
            LOG.error("getCredentialsForPersonIncludingProtectedPerson response is null or empty");
            throw new HsaServiceCallException("No CredentialInformation found for personHsaId " + personHsaId);
        }

        return response.getCredentialInformation();
    }

    public GetHospCredentialsForPersonResponseType getHospCredentialsForPerson(String personalIdentityNumber)
        throws HsaServiceCallException {
        GetHospCredentialsForPersonType parameters = new GetHospCredentialsForPersonType();

        parameters.setPersonalIdentityNumber(personalIdentityNumber);

        GetHospCredentialsForPersonResponseType response = getHospCredentialsForPersonResponderInterface
            .getHospCredentialsForPerson(logicalAddress, parameters);

        if (response == null) {
            LOG.error("getHospCredentialsForPerson response is null");
            throw new HsaServiceCallException("No GetHospCredentialsForPerson found for personalIdentityNumber " + personalIdentityNumber);
        }

        return response;
    }

    public LocalDateTime getHospLastUpdate() {
        GetHospLastUpdateType parameters = new GetHospLastUpdateType();

        GetHospLastUpdateResponseType response = getHospLastUpdateResponderInterface.getHospLastUpdate(logicalAddress, parameters);

        return response.getLastUpdate();
    }

    public HandleHospCertificationPersonResponseType handleHospCertificationPerson(
        String certificationId, OperationEnum operation, String personalIdentityNumber, String reason) {

        HandleHospCertificationPersonType parameters = new HandleHospCertificationPersonType();
        parameters.setCertificationId(certificationId);
        parameters.setOperation(operation);
        parameters.setPersonalIdentityNumber(personalIdentityNumber);
        parameters.setReason(reason);

        HandleHospCertificationPersonResponseType response = handleHospCertificationPersonResponderInterface
            .handleHospCertificationPerson(logicalAddress, parameters);

        return response;
    }
}
