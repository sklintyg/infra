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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.Commission;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.intygproxyservice.services.organization.converter.CareProviderConverter;

@ExtendWith(MockitoExtension.class)
class GetCareProviderListServiceTest {

    private static final List<Vardenhet> CARE_UNITS = List.of(new Vardenhet());
    private static final String HSA_ID = "HSA_ID";

    private Vardgivare provider;
    private Commission c1;

    @Mock
    GetCareUnitListService getCareUnitListService;
    @Mock
    CareProviderConverter careProviderConverter;

    @InjectMocks
    GetCareProviderListService getCareProviderListService;

    @BeforeEach
    void setup() {
        provider = new Vardgivare();
        provider.setId(HSA_ID);

        c1 = new Commission();
        c1.setHealthCareProviderHsaId(HSA_ID);

        when(getCareUnitListService.get(any(List.class)))
            .thenReturn(CARE_UNITS);

        when(careProviderConverter.convert(any(Commission.class), any(List.class)))
            .thenReturn(provider);
    }

    @Test
    void shouldReturnCareProviderWithUnits() {
        provider.setVardenheter(CARE_UNITS);

        final var response = getCareProviderListService.get(List.of(c1));

        assertEquals(1, response.size());
        assertEquals(provider, response.get(0));
    }

    @Test
    void shouldFilterCareProviderWithoutUnits() {
        final var response = getCareProviderListService.get(List.of(c1));

        assertTrue(response.isEmpty());
    }

    @Test
    void shouldFilterDuplicates() {
        final var response = getCareProviderListService.get(List.of(c1, c1));

        assertTrue(response.isEmpty());
    }

    @Test
    void shouldSendCommissionToConverter() {
        final var captor = ArgumentCaptor.forClass(Commission.class);

        getCareProviderListService.get(List.of(c1));

        verify(careProviderConverter).convert(captor.capture(), anyList());
        assertEquals(c1, captor.getValue());
    }

    @Test
    void shouldSendUnitListToConverter() {
        final var captor = ArgumentCaptor.forClass(List.class);

        getCareProviderListService.get(List.of(c1));

        verify(careProviderConverter).convert(any(Commission.class), captor.capture());
        assertEquals(CARE_UNITS, captor.getValue());
    }

    @Test
    void shouldSendCommissionsToGetCareUnitListService() {
        final var c2 = new Commission();
        final var c3 = new Commission();
        c2.setHealthCareProviderHsaId(HSA_ID);
        c3.setHealthCareProviderHsaId("NOT_IT");
        final var captor = ArgumentCaptor.forClass(List.class);

        getCareProviderListService.get(List.of(c1, c2, c3));

        verify(getCareUnitListService, times(3)).get(captor.capture());
        assertTrue(captor.getAllValues().contains(List.of(c1, c2)));
    }
}