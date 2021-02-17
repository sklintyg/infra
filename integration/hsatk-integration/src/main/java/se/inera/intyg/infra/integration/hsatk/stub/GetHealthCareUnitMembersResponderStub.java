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
package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareUnitStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.SubUnitStub;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v2.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMemberType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.v2.AddressType;

public class GetHealthCareUnitMembersResponderStub implements GetHealthCareUnitMembersResponderInterface {

    @Autowired
    private HsaServiceStub hsaServiceStub;

    @Override
    public GetHealthCareUnitMembersResponseType getHealthCareUnitMembers(String logicalAddress, GetHealthCareUnitMembersType parameters) {
        GetHealthCareUnitMembersResponseType response = new GetHealthCareUnitMembersResponseType();
        /*if (parameters.getHealthCareUnitHsaId().endsWith("-finns-ej")) {
            response.setResultText("Returning ERROR for -finns-ej hsaId");
            response.setResultCode(ResultCodeEnum.ERROR);
            return response;
        }*/

        HealthCareUnitMembersType membersType = new HealthCareUnitMembersType();

        updateUnit(membersType, parameters.getHealthCareUnitHsaId());

        response.setHealthCareUnitMembers(membersType);
        return response;
    }

    private void updateUnit(HealthCareUnitMembersType membersType, String unitHsaId) {

        CareUnitStub careUnitStub = hsaServiceStub.getCareUnit(unitHsaId);
        if (careUnitStub != null && careUnitStub.getId().equals(unitHsaId)) {
            membersType.getHealthCareUnitPrescriptionCode().add(careUnitStub.getPrescriptionCode());

            if (careUnitStub.getSubUnits() != null) {
                for (SubUnitStub subUnit : careUnitStub.getSubUnits()) {
                    if (subUnit.getId().endsWith("-finns-ej")) {
                        continue;
                    }
                    HealthCareUnitMemberType member = new HealthCareUnitMemberType();
                    member.setHealthCareUnitMemberHsaId(subUnit.getId());
                    member.setHealthCareUnitMemberName(subUnit.getName());
                    member.setHealthCareUnitMemberStartDate(subUnit.getStart());
                    member.setHealthCareUnitMemberEndDate(subUnit.getEnd());
                    AddressType addressType = new AddressType();
                    addressType.getAddressLine().add(subUnit.getPostalAddress());

                    member.setHealthCareUnitMemberpostalAddress(addressType);
                    member.setHealthCareUnitMemberpostalCode(subUnit.getPostalCode());
                    member.getHealthCareUnitMemberPrescriptionCode().add(subUnit.getPrescriptionCode());
                    membersType.getHealthCareUnitMember().add(member);
                }
            }
        }
    }
}
