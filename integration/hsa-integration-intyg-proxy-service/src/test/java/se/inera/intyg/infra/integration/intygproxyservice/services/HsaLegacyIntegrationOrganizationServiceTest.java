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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnit;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetHealthCareUnitMembersRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.GetHealthCareUnitRequestDTO;

@ExtendWith(MockitoExtension.class)
class HsaLegacyIntegrationOrganizationServiceTest {

    public static final String CARE_PROVIDER_HSA_ID = "careProviderHsaId";
    @InjectMocks
    private HsaLegacyIntegrationOrganizationService hsaLegacyIntegrationOrganizationService;

    @Mock
    private GetHealthCareUnitService getHealthCareUnitService;

    @Mock
    private GetActiveHealthCareUnitMemberHsaIdService getHealthCareUnitMemberHsaIdService;

    private static final String CARE_UNIT_HSA_ID = "careUnitHsaId";

    @Nested
    class VardgivareOfvardenhet {

        @Test
        void shouldReturnHealthCareProviderHsaIdWhenCareUnitHsaIdIsProvided() throws HsaServiceCallException {
            final var healthCareUnit = new HealthCareUnit();
            healthCareUnit.setHealthCareProviderHsaId(CARE_PROVIDER_HSA_ID);

            when(getHealthCareUnitService.get(GetHealthCareUnitRequestDTO.builder()
                .hsaId(CARE_UNIT_HSA_ID)
                .build())).thenReturn(healthCareUnit);

            final var actualResult = hsaLegacyIntegrationOrganizationService.getVardgivareOfVardenhet(CARE_UNIT_HSA_ID);
            assertEquals(CARE_PROVIDER_HSA_ID, actualResult);
        }

        @Test
        void shouldReturnNullWhenExceptionIsThrown() throws HsaServiceCallException {
            when(getHealthCareUnitService.get(GetHealthCareUnitRequestDTO.builder()
                .hsaId(CARE_UNIT_HSA_ID)
                .build())).thenThrow(HsaServiceCallException.class);

            final var actualResult = hsaLegacyIntegrationOrganizationService.getVardgivareOfVardenhet(CARE_UNIT_HSA_ID);
            Assertions.assertNull(actualResult);
        }
    }

    @Nested
    class GetHsaIdForAktivaUnderenheter {

        private static final String CARE_UNIT_ID = "careUnitId";
        private static final String ACTIVE_CARE_UNIT_HSA_ID_1 = "careUnitId1";
        private static final String ACTIVE_CARE_UNIT_HSA_ID_2 = "careUnitId2";

        @Test
        void shouldReturnListOfHsaIdsForActiveSubUnits() {
            final var expectedResult = List.of(ACTIVE_CARE_UNIT_HSA_ID_1, ACTIVE_CARE_UNIT_HSA_ID_2);
            when(getHealthCareUnitMemberHsaIdService.get(
                    GetHealthCareUnitMembersRequestDTO.builder()
                        .hsaId(CARE_UNIT_ID)
                        .build()
                )
            ).thenReturn(expectedResult);
            final var result = hsaLegacyIntegrationOrganizationService.getHsaIdForAktivaUnderenheter(CARE_UNIT_ID);
            assertEquals(expectedResult, result);
        }
    }
}
