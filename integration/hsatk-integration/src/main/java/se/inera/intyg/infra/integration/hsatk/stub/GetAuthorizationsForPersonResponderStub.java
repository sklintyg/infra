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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareProviderStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CareUnitStub;
import se.inera.intyg.infra.integration.hsatk.stub.model.CredentialInformation;
import se.inera.intyg.infra.integration.hsatk.stub.model.HsaPerson;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedperson.v2.rivtabp21.GetCredentialsForPersonIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedpersonresponder.v2.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.getcredentialsforpersonincludingprotectedpersonresponder.v2.GetCredentialsForPersonIncludingProtectedPersonType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CommissionType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.CredentialInformationType;
import se.riv.infrastructure.directory.authorizationmanagement.v2.HsaSystemRoleType;
import se.riv.infrastructure.directory.employee.v2.PaTitleType;

public class GetAuthorizationsForPersonResponderStub
    implements GetCredentialsForPersonIncludingProtectedPersonResponderInterface {

    private static final Logger LOG = LoggerFactory.getLogger(GetHealthCareProviderResponderStub.class);

    @Autowired
    HsaServiceStub hsaServiceStub;

    @Override
    public GetCredentialsForPersonIncludingProtectedPersonResponseType getCredentialsForPersonIncludingProtectedPerson(
        String logicalAddress,
        GetCredentialsForPersonIncludingProtectedPersonType parameters) {
        // CHECKSTYLE:OFF LineLength
        GetCredentialsForPersonIncludingProtectedPersonResponseType response = new GetCredentialsForPersonIncludingProtectedPersonResponseType();
        // CHECKSTYLE:ON LineLength

        response.getCredentialInformation()
            .addAll(miuInformationTypesForEnhetsIds(
                hsaServiceStub.getCredentialInformation(parameters.getPersonHsaId()), parameters.getPersonHsaId()));

        return response;
    }

    private List<CredentialInformationType> miuInformationTypesForEnhetsIds(CredentialInformation credentialInformation,
        String hsaPersonId) {
        List<CredentialInformationType> informationTypes = new ArrayList<>();
        HsaPerson hsaPerson = hsaServiceStub.getHsaPerson(hsaPersonId);
        LOG.info("Transforming stubdata to return types for CI: {} and person: {}", credentialInformation, hsaPerson);
        if (hsaPerson != null && credentialInformation != null) {
            CredentialInformationType cit = new CredentialInformationType();
            cit.setPersonHsaId(hsaPersonId);
            cit.setPersonalPrescriptionCode(hsaPerson.getPersonalPrescriptionCode());
            if (hsaPerson.getPaTitle() != null) {
                PaTitleType paTitleType = new PaTitleType();
                for (HsaPerson.PaTitle paTitle : hsaPerson.getPaTitle()) {
                    paTitleType.setPaTitleCode(paTitle.getTitleCode());
                }
                cit.getPaTitleCode().add(paTitleType.getPaTitleCode());
            }
            if (hsaPerson.getSystemRoles() != null) {
                for (String systemRole : hsaPerson.getSystemRoles()) {
                    HsaSystemRoleType hsaSystemRoleType = new HsaSystemRoleType();
                    hsaSystemRoleType.setRole(systemRole);
                    cit.getHsaSystemRole().add(hsaSystemRoleType);
                }
            }

            if (credentialInformation.getCommissionList() != null) {
                for (CredentialInformation.Commission commission : credentialInformation.getCommissionList()) {
                    CareUnitStub careUnit = hsaServiceStub.getCareUnit(commission.getHealthCareUnitHsaId());
                    if (careUnit == null) {
                        continue;
                    }
                    CareProviderStub careProvider = hsaServiceStub.getCareProvider(careUnit.getCareProviderHsaId());

                    if (careProvider == null) {
                        continue;
                    }

                    // NYTT, lägg på systemRoles från stubbens data ifall sådan finns tillgänglig.

                    if (commission.getCommissionPurpose() != null || !commission.getCommissionPurpose().isEmpty()) {
                        for (String commissionPurpose : commission.getCommissionPurpose()) {
                            CommissionType commissionType = new CommissionType();
                            commissionType.setCommissionHsaId(credentialInformation.getHsaId());
                            commissionType.setCommissionPurpose(commissionPurpose);
                            commissionType.setCommissionName(credentialInformation.getGivenName());
                            commissionType.setHealthCareUnitHsaId(careUnit.getId());
                            commissionType.setHealthCareUnitName(careUnit.getName());
                            commissionType.setHealthCareUnitStartDate(careUnit.getStart());
                            commissionType.setHealthCareUnitEndDate(careUnit.getEnd());

                            commissionType.setHealthCareProviderHsaId(careProvider.getId());
                            commissionType.setHealthCareProviderName(careProvider.getName());
                            commissionType.setHealthCareProviderOrgNo(careUnit.getHealthCareProviderOrgno());

                            cit.getCommission().add(commissionType);
                        }
                    }
                }
            }
            informationTypes.add(cit);
        }
        return informationTypes;
    }

}
