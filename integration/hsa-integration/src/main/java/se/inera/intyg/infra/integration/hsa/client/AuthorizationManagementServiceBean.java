/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.client;

import java.util.List;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedperson.v2.rivtabp21.GetCredentialsForPersonIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedpersonresponder.v2.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedpersonresponder.v2.GetCredentialsForPersonIncludingProtectedPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforperson.v1.rivtabp21.GetHospCredentialsForPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdate.v1.rivtabp21.GetHospLastUpdateResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdateresponder.v1.GetHospLastUpdateResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationperson.v1.rivtabp21.HandleHospCertificationPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.OperationEnum;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;

/**
 * Created by eriklupander on 2015-12-04.
 */
@Service
public class AuthorizationManagementServiceBean implements AuthorizationManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationManagementServiceBean.class);

    // CHECKSTYLE:OFF LineLength

    @Autowired
    private GetCredentialsForPersonIncludingProtectedPersonResponderInterface getCredentialsForPersonIncludingProtectedPersonResponderInterface;

    @Autowired
    private GetHospCredentialsForPersonResponderInterface getHospCredentialsForPersonResponderInterface;

    @Autowired
    private GetHospLastUpdateResponderInterface getHospLastUpdateResponderInterface;

    @Autowired
    private HandleHospCertificationPersonResponderInterface handleHospCertificationPersonResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    @Override
    public List<CredentialInformationType> getAuthorizationsForPerson(String personHsaId, String searchBase)
        throws HsaServiceCallException {
        GetCredentialsForPersonIncludingProtectedPersonType parameters = new GetCredentialsForPersonIncludingProtectedPersonType();
        parameters.setPersonHsaId(personHsaId);
        parameters.setSearchBase(searchBase);
        try {
            GetCredentialsForPersonIncludingProtectedPersonResponseType response = getCredentialsForPersonIncludingProtectedPersonResponderInterface
                .getCredentialsForPersonIncludingProtectedPerson(logicalAddress, parameters);

            if (response.getCredentialInformation().isEmpty()) {
                throw new HsaServiceCallException(
                    "Empty response returned from HSA GetCredentialsForPersonIncludingProtectedPerson; personHsaId = '" + parameters
                        .getPersonHsaId()
                        + "'");
            }
            return response.getCredentialInformation();
        } catch (SOAPFaultException soapFaultException) {
            throw new HsaServiceCallException(soapFaultException);
        }
    }

    @Override
    public GetHospCredentialsForPersonResponseType getHospCredentialsForPerson(String personHsaId) throws HsaServiceCallException {
        return null;
    }

    @Override
    public GetHospLastUpdateResponseType getHospLastUpdate() throws HsaServiceCallException {
        return null;
    }

    @Override
    public HandleHospCertificationPersonResponseType handleHospCertificationPerson(String personId, OperationEnum operation,
        String certificationId, String reason) throws HsaServiceCallException {
        return null;
    }
}
