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

package se.inera.intyg.infra.integration.intygproxyservice.services.authorization;

import static se.inera.intyg.infra.integration.hsatk.constants.HsaIntegrationApiConstants.HSA_INTEGRATION_INTYG_PROXY_SERVICE_NOT_ACTIVATED_PROFILE;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.model.HospCredentialsForPerson;
import se.inera.intyg.infra.integration.hsatk.model.Result;
import se.inera.intyg.infra.integration.hsatk.services.HsatkAuthorizationManagementService;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetCredentialInformationRequestDTO;

@Service
@RequiredArgsConstructor
@Profile(HSA_INTEGRATION_INTYG_PROXY_SERVICE_NOT_ACTIVATED_PROFILE)
public class HsaIntegrationAuthorizationManagementService implements HsatkAuthorizationManagementService {

    private final GetCredentialInformationForPersonService getCredentialInformationForPersonService;

    @Override
    public List<CredentialInformation> getCredentialInformationForPerson(String personalIdentityNumber, String personHsaId,
        String profile) {
        return getCredentialInformationForPersonService.get(
            GetCredentialInformationRequestDTO.builder()
                .personHsaId(personHsaId)
                .build()
        );
    }

    @Override
    public HospCredentialsForPerson getHospCredentialsForPersonResponseType(String personalIdentityNumber) {
        return null;
    }

    @Override
    public LocalDateTime getHospLastUpdate() {
        return null;
    }

    @Override
    public Result handleHospCertificationPersonResponseType(String certificationId, String operation, String personalIdentityNumber,
        String reason) {
        return null;
    }
}
