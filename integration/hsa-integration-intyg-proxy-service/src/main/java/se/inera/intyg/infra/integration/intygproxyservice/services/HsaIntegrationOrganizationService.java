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

package se.inera.intyg.infra.integration.intygproxyservice.services;

import java.util.List;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareProvider;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.services.HsatkOrganizationService;

@Service
public class HsaIntegrationOrganizationService implements HsatkOrganizationService {

    @Override
    public List<HealthCareProvider> getHealthCareProvider(String healthCareProviderHsaId, String healthCareProviderOrgNo) {
        return null;
    }

    @Override
    public HealthCareUnit getHealthCareUnit(String healthCareUnitMemberHsaId) {
        return null;
    }

    @Override
    public HealthCareUnitMembers getHealthCareUnitMembers(String healtCareUnitHsaId) {
        return null;
    }

    @Override
    public Unit getUnit(String unitHsaId, String profile) {
        return null;
    }
}
