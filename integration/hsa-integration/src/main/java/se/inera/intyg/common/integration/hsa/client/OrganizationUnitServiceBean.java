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

package se.inera.intyg.common.integration.hsa.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v1.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v1.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v1.*;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.*;
import se.riv.infrastructure.directory.organization.getunit.v1.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v1.*;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

/**
 * Provides a common interface to the {@link GetUnitResponderInterface}, {@link GetHealthCareUnitResponderInterface} and
 * {@link GetHealthCareUnitMembersResponderInterface} HSA services.
 *
 * Created by eriklupander on 2015-12-03.
 */
@Service
public class OrganizationUnitServiceBean implements OrganizationUnitService {

    private static final Logger LOG = LoggerFactory.getLogger(OrganizationUnitServiceBean.class);

    @Autowired
    private GetUnitResponderInterface getUnitResponderInterface;

    @Autowired
    private GetHealthCareUnitResponderInterface getHealthCareUnitResponderInterface;

    @Autowired
    private GetHealthCareUnitMembersResponderInterface getHealthCareUnitMembersResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    @Override
    public UnitType getUnit(String unitHsaId) throws ExternalServiceCallException {
        GetUnitType parameters = new GetUnitType();
        parameters.setUnitHsaId(unitHsaId);
        GetUnitResponseType unitResponse = getUnitResponderInterface.getUnit(logicalAddress, parameters);

        if (unitResponse.getResultCode() == ResultCodeEnum.ERROR) {
            if (unitResponse.getUnit() == null || unitResponse.getUnit().getUnitHsaId() != null) {
                LOG.error("Error received when calling GetUnit for {}, result text: {}", unitHsaId, unitResponse.getResultText());
                throw new ExternalServiceCallException("Could not GetUnit for hsaId " + unitHsaId);
            } else {
                LOG.warn("Error received when calling GetUnit for {}, result text: {}", unitHsaId, unitResponse.getResultText());
                LOG.warn("Continuing anyway because information was delivered with the ERROR code.");
            }
        }
        return unitResponse.getUnit();
    }

    @Override
    public HealthCareUnitType getHealthCareUnit(String hsaId) throws ExternalServiceCallException {
        GetHealthCareUnitType parameters = new GetHealthCareUnitType();
        parameters.setHealthCareUnitMemberHsaId(hsaId);
        GetHealthCareUnitResponseType response = getHealthCareUnitResponderInterface.getHealthCareUnit(logicalAddress, parameters);

        if (response.getResultCode() == ResultCodeEnum.ERROR) {
            if (response.getHealthCareUnit() == null || response.getHealthCareUnit().getHealthCareUnitHsaId() != null) {
                LOG.error("Error received when calling GetHealthCareUnit for {}, result text: {}", hsaId, response.getResultText());
                throw new ExternalServiceCallException("Could not GetHealthCareUnit for hsaId " + hsaId);
            } else {
                LOG.warn("Error received when calling GetHealthCareUnit for {}, result text: {}", hsaId, response.getResultText());
                LOG.warn("Continuing anyway because information was delivered with the ERROR code.");
            }
        }
        return response.getHealthCareUnit();
    }

    @Override
    public HealthCareUnitMembersType getHealthCareUnitMembers(String unitHsaId) throws ExternalServiceCallException {
        GetHealthCareUnitMembersType parameters = new GetHealthCareUnitMembersType();
        parameters.setHealthCareUnitHsaId(unitHsaId);
        GetHealthCareUnitMembersResponseType response = getHealthCareUnitMembersResponderInterface.getHealthCareUnitMembers(logicalAddress,
                parameters);

        if (response.getResultCode() == ResultCodeEnum.ERROR) {
            if (response.getHealthCareUnitMembers() == null || response.getHealthCareUnitMembers().getHealthCareUnitHsaId() != null) {
                LOG.error("Error received when calling GetHealthCareUnitMembers for {}, result text: {}", unitHsaId, response.getResultText());
                throw new ExternalServiceCallException("Could not GetHealthCareUnitMembers for hsaId " + unitHsaId);
            } else {
                LOG.warn("Error received when calling GetHealthCareUnitMembers for {}, result text: {}", unitHsaId, response.getResultText());
                LOG.warn("Continuing anyway because information was delivered with the ERROR code.");
            }
        }
        return response.getHealthCareUnitMembers();
    }

}
