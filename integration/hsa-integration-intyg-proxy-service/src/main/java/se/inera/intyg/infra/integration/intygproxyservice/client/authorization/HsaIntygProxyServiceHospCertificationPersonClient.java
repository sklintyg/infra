/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.intygproxyservice.client.authorization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.inera.intyg.infra.integration.intygproxyservice.dto.authorization.GetHospCertificationPersonRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.authorization.GetHospCertificationPersonResponseDTO;

@Service
public class HsaIntygProxyServiceHospCertificationPersonClient {

    @Autowired
    @Qualifier("hsaIntygProxyServiceRestTemplate")
    private RestTemplate restTemplate;
    @Value("${integration.intygproxyservice.certificationperson.endpoint}")
    private String certificationPersonEndpoint;
    @Value("${integration.intygproxyservice.baseurl}")
    private String intygProxyServiceBaseUrl;

    public GetHospCertificationPersonResponseDTO get(GetHospCertificationPersonRequestDTO getHospCertificationPersonRequestDTO) {
        try {
            final var url = intygProxyServiceBaseUrl + certificationPersonEndpoint;
            return restTemplate.postForObject(url, getHospCertificationPersonRequestDTO, GetHospCertificationPersonResponseDTO.class);
        } catch (Exception exception) {
            throw new IllegalStateException("Error occured when trying to communicate with intyg-proxy-service", exception);
        }
    }
}
