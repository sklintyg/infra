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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.model.Commission;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.intygproxyservice.services.organization.converter.CareProviderConverter;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetCareProviderListService {

    private final CareProviderConverter careProviderConverter;
    private final GetCareUnitListService getCareUnitListService;

    public List<Vardgivare> get(List<Commission> commissions) {
        return commissions.stream()
            .map(
                commission -> careProviderConverter.convert(
                    commission,
                    getCareUnitListService.get(
                        commissionsForCareProvider(commissions, commission.getHealthCareProviderHsaId())
                    )
                )
            )
            .distinct()
            .filter(GetCareProviderListService::hasUnits)
            .sorted(Comparator.nullsLast(Comparator.comparing(Vardgivare::getNamn)))
            .collect(Collectors.toList());
    }

    private List<Commission> commissionsForCareProvider(List<Commission> commissions, String id) {
        return commissions.stream()
            .filter(commission -> commission.getHealthCareProviderHsaId().equals(id))
            .collect(Collectors.toList());
    }

    private static boolean hasUnits(Vardgivare careProvider) {
        return !careProvider.getVardenheter().isEmpty();
    }
}