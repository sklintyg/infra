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

package se.inera.intyg.common.integration.hsa.services;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.common.integration.hsa.client.EmployeeService;
import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.v1.PersonInformationType;

/**
 * Created by Magnus Ekstrand on 28/05/15.
 */
@Service
public class HsaEmployeeServiceImpl implements HsaEmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(HsaEmployeeServiceImpl.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    public List<PersonInformationType> getEmployee(String personHsaId, String personalIdentityNumber) throws WebServiceException {
        try {
            return employeeService.getEmployee(personHsaId, personalIdentityNumber, null);
        } catch (ExternalServiceCallException e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
    }

    @Override
    public List<PersonInformationType> getEmployee(String personHsaId, String personalIdentityNumber, String searchBase) throws WebServiceException {
        try {
            return employeeService.getEmployee(personHsaId, personalIdentityNumber, searchBase);
        } catch (ExternalServiceCallException e) {
            LOG.error(e.getMessage());
            throw new WebServiceException(e.getMessage());
        }
    }

}
