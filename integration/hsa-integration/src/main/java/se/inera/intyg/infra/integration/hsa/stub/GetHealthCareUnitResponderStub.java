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
package se.inera.intyg.infra.integration.hsa.stub;

import org.springframework.beans.factory.annotation.Autowired;

import se.inera.intyg.infra.integration.hsa.model.Mottagning;
import se.inera.intyg.infra.integration.hsa.model.Vardenhet;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v1.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.GetHealthCareUnitType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v1.HealthCareUnitType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

/**
 * Created by eriklupander on 2015-12-08.
 */
public class GetHealthCareUnitResponderStub implements GetHealthCareUnitResponderInterface {

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Override
    public GetHealthCareUnitResponseType getHealthCareUnit(String logicalAddress, GetHealthCareUnitType parameters) {
        GetHealthCareUnitResponseType responseType = new GetHealthCareUnitResponseType();

        // This is not correct. The getHealthCareUnit query may return both mottagningar and vardenheter.
        Mottagning mottagning = hsaServiceStub.getMottagning(parameters.getHealthCareUnitMemberHsaId());
        if (mottagning != null) {
            HealthCareUnitType member = new HealthCareUnitType();

            // Mottagning
            member.setHealthCareUnitMemberHsaId(mottagning.getId());
            member.setHealthCareUnitMemberName(mottagning.getNamn());
            member.setHealthCareUnitMemberStartDate(mottagning.getStart());
            member.setHealthCareUnitMemberEndDate(mottagning.getEnd());

            // Överordnad enhet, används för att plocka fram överordnad enhets epostadress när egen saknas.
            member.setHealthCareUnitHsaId(mottagning.getParentHsaId());
            member.setUnitIsHealthCareUnit(false);

            responseType.setHealthCareUnit(member);
            responseType.setResultCode(ResultCodeEnum.OK);
            return responseType;
        }

        Vardenhet vardenhet = hsaServiceStub.getVardenhet(parameters.getHealthCareUnitMemberHsaId());
        if (vardenhet != null) {
            HealthCareUnitType unit = new HealthCareUnitType();
            unit.setHealthCareUnitHsaId(vardenhet.getId());
            unit.setHealthCareProviderStartDate(vardenhet.getStart());
            unit.setHealthCareProviderEndDate(vardenhet.getEnd());
            unit.setUnitIsHealthCareUnit(true);
            unit.setHealthCareProviderHsaId(vardenhet.getVardgivareHsaId());
            responseType.setHealthCareUnit(unit);
            responseType.setResultCode(ResultCodeEnum.OK);
            return responseType;
        }

        responseType.setResultText("HsaServiceStub returned NULL Mottagning for hsaId: '" + parameters.getHealthCareUnitMemberHsaId() + "'");
        responseType.setResultCode(ResultCodeEnum.ERROR);
        return responseType;
    }
}
