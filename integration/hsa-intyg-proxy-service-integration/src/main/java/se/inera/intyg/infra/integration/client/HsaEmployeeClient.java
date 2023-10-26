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

package se.inera.intyg.infra.integration.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.integration.dto.GetEmployeeRequestDTO;
import se.inera.intyg.infra.integration.dto.GetEmployeeResponseDTO;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;

@Service
public class HsaEmployeeClient {

    private final RestTemplate restTemplate;

    public HsaEmployeeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public GetEmployeeResponseDTO getEmployee(GetEmployeeRequestDTO getEmployeeRequestDTO)
        throws HsaServiceCallException {

        validateRequestParameters(getEmployeeRequestDTO);
        // How do we implement so that this is configureable?
        final var url = "http://localhost:18020/api/v1/person";
        return restTemplate.postForObject(url, getEmployeeRequestDTO, GetEmployeeResponseDTO.class);
    }

    private void validateRequestParameters(GetEmployeeRequestDTO getEmployeeRequestDTO) {
        if (isNullOrEmpty(getEmployeeRequestDTO.getPersonHsaId()) && isNullOrEmpty(getEmployeeRequestDTO.getPersonalIdentityNumber())) {
            throw new IllegalArgumentException(
                "Missing required parameters. Must provide either personalIdentityNumber or personHsaId");
        }
        if (!isNullOrEmpty(getEmployeeRequestDTO.getPersonHsaId()) && !isNullOrEmpty(getEmployeeRequestDTO.getPersonalIdentityNumber())) {
            throw new IllegalArgumentException("Only provide either personalIdentityNumber or personHsaId. ");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
