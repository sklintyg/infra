/*
 * Copyright (C) 2021 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.client.EmployeeClient;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.inera.intyg.infra.integration.hsatk.model.PersonInformation;
import se.inera.intyg.infra.integration.hsatk.util.HsaTypeConverter;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;
import se.riv.infrastructure.directory.employee.v2.ProfileEnum;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HsatkEmployeeServiceImpl implements HsatkEmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(HsatkEmployeeServiceImpl.class);

    HsaTypeConverter hsaTypeConverter = new HsaTypeConverter();

    @Autowired
    private EmployeeClient employeeClient;

    @Override
    public List<PersonInformation> getEmployee(String personalIdentityNumber, String personHsaId) {
        return getEmployee(personalIdentityNumber, personHsaId, null);
    }

    @Override
    public List<PersonInformation> getEmployee(String personalIdentityNumber, String personHsaId, String profile) {
        ProfileEnum profileEnum = ProfileEnum.EXTENDED_1;
        List<PersonInformationType> personInformationTypeList;
        if (StringUtils.isNotEmpty(profile)) {
            profileEnum = ProfileEnum.fromValue(profile);
        }

        try {
            personInformationTypeList = employeeClient.getEmployee(personalIdentityNumber, personHsaId, profileEnum);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        } catch (HsaServiceCallException exception) {
            LOG.warn("HsaServiceCallException thrown: {}", exception);
            personInformationTypeList = new ArrayList<>();
        }

        return personInformationTypeList.stream().map(hsaTypeConverter::toPersonInformation).collect(Collectors.toList());
    }

}
