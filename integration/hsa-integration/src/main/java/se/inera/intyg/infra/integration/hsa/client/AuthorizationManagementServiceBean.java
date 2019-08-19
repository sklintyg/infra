/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonType;
import se.riv.infrastructure.directory.v1.CredentialInformationType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

/**
 * Created by eriklupander on 2015-12-04.
 */
@Service
public class AuthorizationManagementServiceBean implements AuthorizationManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationManagementServiceBean.class);

    // CHECKSTYLE:OFF LineLength

    @Autowired
    private GetCredentialsForPersonIncludingProtectedPersonResponderInterface getCredentialsForPersonIncludingProtectedPersonResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    @Override
    public List<CredentialInformationType> getAuthorizationsForPerson(String personHsaId, String personalIdentityNumber,
        String searchBase) throws HsaServiceCallException {
        GetCredentialsForPersonIncludingProtectedPersonType parameters = new GetCredentialsForPersonIncludingProtectedPersonType();
        parameters.setPersonalIdentityNumber(personalIdentityNumber);
        parameters.setPersonHsaId(personHsaId);
        parameters.setSearchBase(searchBase);
        GetCredentialsForPersonIncludingProtectedPersonResponseType response = getCredentialsForPersonIncludingProtectedPersonResponderInterface
            .getCredentialsForPersonIncludingProtectedPerson(logicalAddress, parameters);

        if (response.getResultCode() == ResultCodeEnum.ERROR) {
            // Absolute minimum required response
            String errorText = "GetCredentialsForPersonIncludingProtectedPerson returned ERROR with result text '{}'";
            if (response.getCredentialInformation() == null || response.getCredentialInformation().isEmpty()) {
                LOG.error(errorText, response.getResultText());
                throw new HsaServiceCallException("Could not call GetCredentialsForPersonIncludingProtectedPerson");
            } else {
                LOG.warn(errorText, response.getResultText());
                LOG.warn("Continuing anyway because information was delivered with the ERROR code.");
            }
        }
        return response.getCredentialInformation();
    }
}
