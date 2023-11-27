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

package se.inera.intyg.infra.integration.intygproxyservice.services.organization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;

@ExtendWith(MockitoExtension.class)
class HsaIntegrationOrganizationServiceTest {

    private static final String UNIT_HSA_ID = "unitHsaId";
    @Mock
    private GetUnitService getUnitService;
    @InjectMocks
    private HsaIntegrationOrganizationService hsaIntegrationOrganizationService;

    @Nested
    class GetUnit {

        @Test
        void shouldReturnUnit() {
            final var expectedUnit = new Unit();

            when(getUnitService.get(any(GetUnitRequestDTO.class))).thenReturn(expectedUnit);

            final var result = hsaIntegrationOrganizationService.getUnit(UNIT_HSA_ID, null);

            assertEquals(expectedUnit, result);
        }
    }
}
