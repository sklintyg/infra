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

package se.inera.intyg.infra.integration.intygproxyservice.services.employee;

import static se.inera.intyg.infra.integration.hsatk.constants.HsaIntegrationApiConstants.HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE;

import java.util.List;
import javax.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaEmployeeService;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetEmployeeRequestDTO;

@Slf4j
@Service
@Profile(HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE)
@RequiredArgsConstructor
public class HsaLegacyIntegrationEmployeeService implements HsaEmployeeService {

    private final GetEmployeeService getEmployeeService;

    @Override
    public List<PersonInformation> getEmployee(String personHsaId, String personalIdentityNumber) throws WebServiceException {
        return getEmployee(personHsaId, personalIdentityNumber, null);
    }

    @Override
    public List<PersonInformation> getEmployee(String personHsaId, String personalIdentityNumber, String searchBase)
        throws WebServiceException {
        try {
            return getEmployeeService.get(
                GetEmployeeRequestDTO.builder()
                    .hsaId(personHsaId)
                    .personId(personalIdentityNumber)
                    .build()
            );
        } catch (HsaServiceCallException hsaServiceCallException) {
            log.error(hsaServiceCallException.getMessage());
            throw new WebServiceException(hsaServiceCallException);
        }
    }
}
