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
package se.inera.intyg.infra.integration.hsatk.client;

import java.util.List;
import jakarta.xml.ws.soap.SOAPFaultException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedperson.v2.rivtabp21.GetEmployeeIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.employee.getemployeeincludingprotectedpersonresponder.v2.GetEmployeeIncludingProtectedPersonType;
import se.riv.infrastructure.directory.employee.v2.PersonInformationType;
import se.riv.infrastructure.directory.employee.v2.ProfileEnum;

@Service
public class EmployeeClient {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeClient.class);

    @Autowired
    private GetEmployeeIncludingProtectedPersonResponderInterface getEmployeeIncludingProtectedPersonResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    private static boolean includeFeignedObject = false;

    public List<PersonInformationType> getEmployee(String personalIdentityNumber, String personHsaId, ProfileEnum profile)
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

        GetEmployeeIncludingProtectedPersonType parameters = new GetEmployeeIncludingProtectedPersonType();

        parameters.setIncludeFeignedObject(includeFeignedObject);
        parameters.setPersonalIdentityNumber(personalIdentityNumber);
        parameters.setPersonHsaId(personHsaId);
        parameters.setProfile(profile);
        GetEmployeeIncludingProtectedPersonResponseType response;

        try {
            response = getEmployeeIncludingProtectedPersonResponderInterface
                    .getEmployeeIncludingProtectedPerson(logicalAddress, parameters);
        } catch (SOAPFaultException e) {
            LOG.error("GetEmployee call returned with error: {}", e.getLocalizedMessage());
            throw new HsaServiceCallException(e);
        }
        if (response == null || response.getPersonInformation().isEmpty()) {
            String logMessage;
            if (!StringUtils.isEmpty(personalIdentityNumber)) {
                logMessage = String.format("Response null or empty for personalIdentityNumber: %s", personalIdentityNumber);

            } else {
                logMessage = String.format("Response null or empty for personHsaId: %s", personHsaId);
            }
            LOG.warn(logMessage);
            throw new HsaServiceCallException(logMessage);
        }

        return response.getPersonInformation();
    }

}
