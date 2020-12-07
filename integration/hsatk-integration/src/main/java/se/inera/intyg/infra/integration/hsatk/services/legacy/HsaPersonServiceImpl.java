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
package se.inera.intyg.infra.integration.hsatk.services.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.client.AuthorizationManagementClient;
import se.inera.intyg.infra.integration.hsatk.client.EmployeeClient;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.stub.model.CredentialInformation;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CommissionType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;

import javax.xml.ws.WebServiceException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides person related services using TJK over NTjP.
 * <p>
 * infrastructure:directory:employee and
 * infrastructure:directory:authorizationmanagement
 *
 * @author eriklupander
 */
@Service
public class HsaPersonServiceImpl implements HsaPersonService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaPersonServiceImpl.class);

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private AuthorizationManagementClient authorizationManagementClient;

    /*
     * (non-Javadoc)
     *
     * @see HsaPersonService#getHsaPersonInfo(java.lang.String)
     */
    @Override
    public List<PersonInformationType> getHsaPersonInfo(final String personHsaId) {

        LOG.debug("Getting info from HSA for person '{}'", personHsaId);

        try {
            return employeeClient.getEmployee(null, personHsaId, null);
        } catch (HsaServiceCallException e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
    }

    @Override
    public List<CommissionType> checkIfPersonHasMIUsOnUnit(String hosPersonHsaId, String unitHsaId) throws HsaServiceCallException {

        LOG.debug("Checking if person with HSA id '{}' has MIUs on unit '{}'", hosPersonHsaId, unitHsaId);

        List<CredentialInformationType> response = authorizationManagementClient.getCredentialInformationForPerson(
                null, hosPersonHsaId, null);
        List<CommissionType> commissions = response.stream()
                .flatMap(ci -> ci.getCommission().stream())
                .collect(Collectors.toList());

        List<CommissionType> filteredMuisOnUnit = commissions.stream()
                .filter(ct -> ct.getHealthCareUnitHsaId() != null && ct.getHealthCareUnitHsaId().equals(unitHsaId))
                .filter(ct -> ct.getHealthCareUnitEndDate() == null || ct.getHealthCareUnitEndDate().isAfter(LocalDateTime.now()))
                .filter(ct -> ct.getCommissionPurpose() != null
                        && CredentialInformation.VARD_OCH_BEHANDLING.equalsIgnoreCase(ct.getCommissionPurpose()))
                .collect(Collectors.toList());

        LOG.debug("Person has {} MIUs on unit '{}'", filteredMuisOnUnit.size(), hosPersonHsaId);

        return filteredMuisOnUnit;
    }
}
