/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
import org.springframework.stereotype.Service;

import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v1.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v1.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.v1.PersonInformationType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

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
    public List<PersonInformationType> getEmployee(String personHsaId, String personalIdentityNumber, String searchBase)
            throws HsaServiceCallException {

        LOG.debug("Getting info from HSA for person '{}'", personHsaId);

        // Exakt ett av fälten personHsaId och personalIdentityNumber ska anges.
        if (StringUtils.isEmpty(personHsaId) && StringUtils.isEmpty(personalIdentityNumber)) {
            throw new IllegalArgumentException(
                    "Inget av argumenten personHsaId och personalIdentityNumber är satt. Ett av dem måste ha ett värde.");
        }

        if (!StringUtils.isEmpty(personHsaId) && !StringUtils.isEmpty(personalIdentityNumber)) {
            throw new IllegalArgumentException("Endast ett av argumenten personHsaId och personalIdentityNumber får vara satt.");
        }

        GetEmployeeIncludingProtectedPersonType employeeType = createEmployeeType(personHsaId, personalIdentityNumber, searchBase);
        return getEmployee(logicalAddress, employeeType);
    }

    private List<PersonInformationType> getEmployee(String logicalAddress, GetEmployeeIncludingProtectedPersonType employeeType)
            throws HsaServiceCallException {

        GetEmployeeIncludingProtectedPersonResponseType response;

        try {
            response = getEmployeeIncludingProtectedPersonResponderInterface.getEmployeeIncludingProtectedPerson(logicalAddress,
                    employeeType);

            // check whether call was successful or not
            if (response.getResultCode() == ResultCodeEnum.ERROR) {
                if (response.getPersonInformation() == null || response.getPersonInformation().isEmpty()) {
                    LOG.error("Failed getting employee information from HSA; personHsaId = '{}'. Result text: {}",
                            employeeType.getPersonHsaId(),
                            response.getResultText());
                    throw new HsaServiceCallException(response.getResultText());
                } else {
                    LOG.warn("Failed getting employee information from HSA; personHsaId = '{}'. Result text: {}",
                            employeeType.getPersonHsaId(),
                            response.getResultText());
                    LOG.warn("Continuing anyway because information was delivered with the ERROR code.");
                }
            }
        } catch (SOAPFaultException e) {
            throw new HsaServiceCallException(e);
        }

        return response.getPersonInformation();
    }

    private GetEmployeeIncludingProtectedPersonType createEmployeeType(String personHsaId, String personalIdentityNumber,
            String searchBase) {
        GetEmployeeIncludingProtectedPersonType employeeType = new GetEmployeeIncludingProtectedPersonType();
        employeeType.setPersonHsaId(personHsaId);
        employeeType.setPersonalIdentityNumber(personalIdentityNumber);
        employeeType.setSearchBase(searchBase);

        return employeeType;
    }
}
