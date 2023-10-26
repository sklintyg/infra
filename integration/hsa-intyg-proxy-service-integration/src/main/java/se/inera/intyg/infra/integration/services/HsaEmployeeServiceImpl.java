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

package se.inera.intyg.infra.integration.services;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.client.HsaEmployeeClient;
import se.inera.intyg.infra.integration.dto.GetEmployeeRequestDTO;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.services.HsatkEmployeeService;

@Service
public class HsaEmployeeServiceImpl implements HsatkEmployeeService {

    private final HsaEmployeeClient hsaEmployeeClient;
    private static final Logger LOG = LoggerFactory.getLogger(HsaEmployeeServiceImpl.class);

    public HsaEmployeeServiceImpl(HsaEmployeeClient hsaEmployeeClient) {
        this.hsaEmployeeClient = hsaEmployeeClient;
    }

    @Override
    public List<PersonInformation> getEmployee(String personalIdentityNumber, String personHsaId) {
        return getEmployee(personalIdentityNumber, personHsaId, null);
    }

    @Override
    public List<PersonInformation> getEmployee(String personalIdentityNumber, String personHsaId, String profile) {
        try {
            final var employee = hsaEmployeeClient.getEmployee(
                GetEmployeeRequestDTO.builder()
                    .personalIdentityNumber(personalIdentityNumber)
                    .personHsaId(personHsaId)
                    .profile(profile)
                    .build()
            );
            return employee.getPersonInformationList();
        } catch (HsaServiceCallException exception) {
            LOG.warn("HsaServiceCallException thrown: {}", exception);
            return new ArrayList<>();
        }
    }
}
