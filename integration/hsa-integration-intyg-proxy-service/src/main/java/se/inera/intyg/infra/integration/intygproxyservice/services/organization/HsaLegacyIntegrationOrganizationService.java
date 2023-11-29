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

import static se.inera.intyg.infra.integration.hsatk.constants.HsaIntegrationApiConstants.HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE;

import java.util.List;
import javax.xml.ws.WebServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.legacy.UserAuthorizationInfo;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardenhet;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Vardgivare;
import se.inera.intyg.infra.integration.hsatk.services.legacy.HsaOrganizationsService;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitMembersRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;

@Slf4j
@Service
@RequiredArgsConstructor
@Profile(HSA_INTEGRATION_INTYG_PROXY_SERVICE_PROFILE)
public class HsaLegacyIntegrationOrganizationService implements HsaOrganizationsService {

    private final GetActiveHealthCareUnitMemberHsaIdService getActiveHealthCareUnitMemberHsaIdService;
    private final GetHealthCareUnitService getHealthCareUnitService;
    private final GetUnitService getUnitService;

    @Override
    public UserAuthorizationInfo getAuthorizedEnheterForHosPerson(String hosPersonHsaId) {
        log.info("work in progress");
        return null;
    }

    @Override
    public String getVardgivareOfVardenhet(String vardenhetHsaId) {
        try {
            final var healthCareUnit = getHealthCareUnitService.get(
                GetHealthCareUnitRequestDTO.builder()
                    .hsaId(vardenhetHsaId)
                    .build()
            );
            return healthCareUnit.getHealthCareProviderHsaId();

        } catch (HsaServiceCallException hsaServiceCallException) {
            log.warn(String.format("Could not fetch health care unit: '%s'", vardenhetHsaId), hsaServiceCallException);
            return null;
        }
    }

    @Override
    public Vardenhet getVardenhet(String vardenhetHsaId) {
        return null;
    }

    @Override
    public Vardgivare getVardgivareInfo(String vardgivareHsaId) {
        final var unit = getUnitService.get(
            GetUnitRequestDTO.builder()
                .hsaId(vardgivareHsaId)
                .build()
        );

        if (unit == null) {
            throw new WebServiceException("Could not get unit for unitHsaId " + vardgivareHsaId);
        }

        return new Vardgivare(unit.getUnitHsaId(), unit.getUnitName());
    }

    @Override
    public List<String> getHsaIdForAktivaUnderenheter(String vardEnhetHsaId) {
        return getActiveHealthCareUnitMemberHsaIdService.get(
            GetHealthCareUnitMembersRequestDTO.builder()
                .hsaId(vardEnhetHsaId)
                .build()
        );
    }

    @Override
    public String getParentUnit(String hsaId) throws HsaServiceCallException {
        final var unit = getHealthCareUnitService.get(
            GetHealthCareUnitRequestDTO.builder()
                .hsaId(hsaId)
                .build()
        );
        return unit.getHealthCareUnitHsaId();
    }
}
