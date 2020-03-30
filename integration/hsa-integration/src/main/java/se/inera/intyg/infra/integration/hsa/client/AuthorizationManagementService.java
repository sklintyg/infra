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
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.authorizationmanagement.gethospcredentialsforpersonresponder.v1.GetHospCredentialsForPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.gethosplastupdateresponder.v1.GetHospLastUpdateResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.HandleHospCertificationPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.handlehospcertificationpersonresponder.v1.OperationEnum;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;

/**
 * Exposes the HSA interface for GetCredentialsForPersonIncludingProtectedPerson.
 *
 * Note: Avoid using this class directly from external applications. Use
 * {@link se.inera.intyg.infra.integration.hsa.services.HsaPersonService}
 * instead.
 *
 * Created by eriklupander on 2015-12-04.
 */
public interface AuthorizationManagementService {

    List<CredentialInformationType> getAuthorizationsForPerson(String personHsaId, String searchBase) throws HsaServiceCallException;

    GetHospCredentialsForPersonResponseType getHospCredentialsForPerson(String personHsaId) throws HsaServiceCallException;

    GetHospLastUpdateResponseType getHospLastUpdate() throws HsaServiceCallException;

    HandleHospCertificationPersonResponseType handleHospCertificationPerson(String personId, OperationEnum operation,
        String certificationId,
        String reason) throws HsaServiceCallException;

}
