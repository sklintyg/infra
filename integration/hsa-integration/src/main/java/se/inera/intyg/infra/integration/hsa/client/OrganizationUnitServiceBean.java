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

import com.google.common.annotations.VisibleForTesting;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v2.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v2.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunit.v2.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GetUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.UnitType;

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
    @Cacheable(cacheResolver = "hsaCacheResolver", key = "#unitHsaId", unless = "#result == null")
    public UnitType getUnit(String unitHsaId) throws HsaServiceCallException {
        GetUnitType parameters = new GetUnitType();
        parameters.setUnitHsaId(unitHsaId);

        try {
            GetUnitResponseType unitResponse = getUnitResponderInterface.getUnit(logicalAddress, parameters);

            if (unitResponse.getUnit() == null || unitResponse.getUnit().getUnitHsaId() == null) {
                throw new HsaServiceCallException("Could not GetUnit for hsaId " + unitHsaId);
            }
            return unitResponse.getUnit();

        } catch (SOAPFaultException soapFaultException) {
            throw new HsaServiceCallException(soapFaultException);
        }
    }

    @Override
    @Cacheable(cacheResolver = "hsaCacheResolver", key = "#hsaId", unless = "#result == null")
    public HealthCareUnitType getHealthCareUnit(String hsaId) throws HsaServiceCallException {
        GetHealthCareUnitType parameters = new GetHealthCareUnitType();
        parameters.setHealthCareUnitMemberHsaId(hsaId);

        try {
            GetHealthCareUnitResponseType response = getHealthCareUnitResponderInterface.getHealthCareUnit(logicalAddress, parameters);

            if (response.getHealthCareUnit() == null || response.getHealthCareUnit().getHealthCareUnitHsaId() == null) {
                throw new HsaServiceCallException("Could not GetHealthCareUnit for hsaId " + hsaId);
            }

            return response.getHealthCareUnit();
        } catch (SOAPFaultException soapFaultException) {
            throw new HsaServiceCallException(soapFaultException);
        }
    }

    @Override
    @Cacheable(cacheResolver = "hsaCacheResolver", key = "#unitHsaId", unless = "#result == null")
    public HealthCareUnitMembersType getHealthCareUnitMembers(String unitHsaId) throws HsaServiceCallException {
        GetHealthCareUnitMembersType parameters = new GetHealthCareUnitMembersType();
        parameters.setHealthCareUnitHsaId(unitHsaId);

        try {
            GetHealthCareUnitMembersResponseType response = getHealthCareUnitMembersResponderInterface
                .getHealthCareUnitMembers(logicalAddress,
                    parameters);

            if (response.getHealthCareUnitMembers() == null || response.getHealthCareUnitMembers().getHealthCareUnitHsaId() == null) {
                throw new HsaServiceCallException("Could not GetHealthCareUnitMembers for hsaId " + unitHsaId);
            }
            return response.getHealthCareUnitMembers();
        } catch (SOAPFaultException soapFaultException) {
            throw new HsaServiceCallException(soapFaultException);
        }
    }

    @VisibleForTesting
    public void setGetHealthCareUnitMembersResponderInterface(
        GetHealthCareUnitMembersResponderInterface getHealthCareUnitMembersResponderInterface) {
        this.getHealthCareUnitMembersResponderInterface = getHealthCareUnitMembersResponderInterface;
    }
}
