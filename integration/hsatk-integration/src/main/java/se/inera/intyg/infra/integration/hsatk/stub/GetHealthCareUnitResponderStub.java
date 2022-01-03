/*
 * Copyright (C) 2022 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareUnitStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.SubUnitStub;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v2.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;

public class GetHealthCareUnitResponderStub implements GetHealthCareUnitResponderInterface {

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Override
    public GetHealthCareUnitResponseType getHealthCareUnit(String logicalAddress, GetHealthCareUnitType parameters) {
        GetHealthCareUnitResponseType responseType = new GetHealthCareUnitResponseType();

        // This is not correct. The getHealthCareUnit query may return both mottagningar and vardenheter.
        SubUnitStub subUnit = hsaServiceStub.getSubUnit(parameters.getHealthCareUnitMemberHsaId());
        if (subUnit != null) {
            HealthCareUnitType member = new HealthCareUnitType();

            // Mottagning
            member.setHealthCareUnitMemberHsaId(subUnit.getId());
            member.setHealthCareUnitMemberName(subUnit.getName());
            member.setHealthCareUnitMemberStartDate(subUnit.getStart());
            member.setHealthCareUnitMemberEndDate(subUnit.getEnd());

            // Överordnad enhet, används för att plocka fram överordnad enhets epostadress när egen saknas.
            member.setHealthCareUnitHsaId(subUnit.getParentHsaId());
            member.setUnitIsHealthCareUnit(false);

            responseType.setHealthCareUnit(member);
            return responseType;
        }

        CareUnitStub careUnitStub = hsaServiceStub.getCareUnit(parameters.getHealthCareUnitMemberHsaId());
        if (careUnitStub != null) {
            HealthCareUnitType unit = new HealthCareUnitType();
            unit.setHealthCareUnitHsaId(careUnitStub.getId());
            unit.setHealthCareProviderStartDate(careUnitStub.getStart());
            unit.setHealthCareProviderEndDate(careUnitStub.getEnd());
            unit.setHealthCareUnitName(careUnitStub.getName());
            unit.setUnitIsHealthCareUnit(true);
            unit.setHealthCareProviderHsaId(careUnitStub.getCareProviderHsaId());
            unit.setHealthCareProviderOrgNo(careUnitStub.getHealthCareProviderOrgno());
            responseType.setHealthCareUnit(unit);
            return responseType;
        }

        return responseType;
    }
}
