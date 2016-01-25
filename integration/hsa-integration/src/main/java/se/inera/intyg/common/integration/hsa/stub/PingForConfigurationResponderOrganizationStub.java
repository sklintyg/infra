/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.integration.hsa.stub;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;


/**
 * Created by eriklupander on 2015-12-09.
 */
@Component(value = "pingForConfigurationResponderInterfaceOrganization")
public class PingForConfigurationResponderOrganizationStub implements PingForConfigurationResponderInterface {

    @Override
    public PingForConfigurationResponseType pingForConfiguration(String logicalAddress, PingForConfigurationType parameters) {
        PingForConfigurationResponseType response = new PingForConfigurationResponseType();
        response.setPingDateTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        response.setVersion("1.1");

        return response;
    }
}
