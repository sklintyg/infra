/*
 * Copyright (C) 2024 Inera AB (http://www.inera.se)
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
import se.inera.intyg.infra.integration.hsatk.stub.model.AbstractUnitStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareUnitStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.SubUnitStub;
import se.riv.infrastructure.directory.organization.getunit.v3.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.GetUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v3.UnitType;
import se.riv.infrastructure.directory.organization.v3.AddressType;

public class GetUnitResponderStub implements GetUnitResponderInterface {

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Override
    public GetUnitResponseType getUnit(String logicalAddress, GetUnitType parameters) {
        GetUnitResponseType response = new GetUnitResponseType();

        // First, check if it is a Vardgivare
        CareProviderStub careProviderStub = hsaServiceStub.getCareProvider(parameters.getUnitHsaId());
        if (careProviderStub != null) {
            UnitType unit = new UnitType();
            unit.setUnitName(careProviderStub.getName());
            unit.setUnitHsaId(careProviderStub.getId());
            response.setUnit(unit);

        } else {
            // Then check if it is a Vardenhet
            CareUnitStub careUnitStub = hsaServiceStub.getCareUnit(parameters.getUnitHsaId());
            if (careUnitStub != null) {

                UnitType unit = abstractVardenhetToUnitType(careUnitStub);
                response.setUnit(unit);
            } else {
                // Finally, test if it is a Mottagning.
                SubUnitStub subUnit = hsaServiceStub.getSubUnit(parameters.getUnitHsaId());
                if (subUnit == null) {
                    return response;
                }
                UnitType unit = abstractVardenhetToUnitType(subUnit);
                response.setUnit(unit);
            }
        }
        return response;
    }

    private UnitType abstractVardenhetToUnitType(AbstractUnitStub careUnit) {
        UnitType unit = new UnitType();
        unit.setUnitName(careUnit.getName());
        unit.setUnitHsaId(careUnit.getId());
        unit.setMail(careUnit.getMail());
        unit.setPostalCode(careUnit.getPostalCode());
        unit.getTelephoneNumber().add(careUnit.getTelephoneNumber());

        AddressType addressType = new AddressType();
        addressType.getAddressLine().add(careUnit.getPostalAddress());
        addressType.getAddressLine().add(careUnit.getPostalTown());
        unit.setPostalAddress(addressType);

        unit.setCountyCode(careUnit.getCountyCode());
        unit.setMunicipalityCode(careUnit.getMunicipalityCode());

        return unit;
    }
}
