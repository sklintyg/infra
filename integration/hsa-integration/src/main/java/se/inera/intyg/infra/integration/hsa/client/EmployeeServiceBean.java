/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.client;

//CHECKSTYLE:OFF LineLength

import java.util.List;
import javax.xml.ws.soap.SOAPFaultException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v2.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;

//CHECKSTYLE:ON LineLength

/**
 * Created by eriklupander on 2015-12-03.
 */
@Service
public class EmployeeServiceBean implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceBean.class);

    @Autowired
    private GetEmployeeIncludingProtectedPersonResponderInterface getEmployeeIncludingProtectedPersonResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    @Override
    @Cacheable(cacheResolver = "hsaCacheResolver", key = "#personHsaId + #searchBase", unless = "#result == null")
    public List<PersonInformationType> getEmployee(String personHsaId, String searchBase)
        throws HsaServiceCallException {

        LOG.debug("Getting info from HSA for person '{}'", personHsaId);

        if (StringUtils.isEmpty(personHsaId)) {
            throw new IllegalArgumentException(
                "personHsaId must be specified");
        }

        GetEmployeeIncludingProtectedPersonType employeeType = createEmployeeType(personHsaId, searchBase);
        return getEmployee(logicalAddress, employeeType);
    }

    private List<PersonInformationType> getEmployee(String logicalAddress, GetEmployeeIncludingProtectedPersonType employeeType)
        throws HsaServiceCallException {

        try {
            GetEmployeeIncludingProtectedPersonResponseType response = getEmployeeIncludingProtectedPersonResponderInterface
                .getEmployeeIncludingProtectedPerson(logicalAddress,
                    employeeType);

            if (response.getPersonInformation() == null || response.getPersonInformation().isEmpty()) {
                throw new HsaServiceCallException(
                    "Empty response returned from HSA GetEmployeeIncludingProtectedPerson; personHsaId = '" + employeeType.getPersonHsaId()
                        + "'");
            }
            return response.getPersonInformation();
        } catch (SOAPFaultException soapFaultException) {
            throw new HsaServiceCallException(soapFaultException);
        }

    }

    private GetEmployeeIncludingProtectedPersonType createEmployeeType(String personHsaId, String searchBase) {
        GetEmployeeIncludingProtectedPersonType employeeType = new GetEmployeeIncludingProtectedPersonType();
        employeeType.setPersonHsaId(personHsaId);
        employeeType.setSearchBase(searchBase);

        return employeeType;
    }
}
