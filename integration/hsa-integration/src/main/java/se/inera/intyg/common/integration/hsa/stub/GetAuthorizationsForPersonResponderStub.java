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

package se.inera.intyg.common.integration.hsa.stub;

import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.common.integration.hsa.model.AgandeForm;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonType;
import se.riv.infrastructure.directory.v1.CommissionType;
import se.riv.infrastructure.directory.v1.CredentialInformationType;
import se.riv.infrastructure.directory.v1.HsaSystemRoleType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2015-12-03.
 */
public class GetAuthorizationsForPersonResponderStub implements GetCredentialsForPersonIncludingProtectedPersonResponderInterface {

    @Autowired
    private HsaServiceStub serviceStub;

    @Override
    public GetCredentialsForPersonIncludingProtectedPersonResponseType getCredentialsForPersonIncludingProtectedPerson(String logicalAddress,
            GetCredentialsForPersonIncludingProtectedPersonType parameters) {
        GetCredentialsForPersonIncludingProtectedPersonResponseType response = new GetCredentialsForPersonIncludingProtectedPersonResponseType();
        response.setResultCode(ResultCodeEnum.OK);

        if (serviceStub.getMedarbetaruppdrag().size() > 0) {
            for (Medarbetaruppdrag miu : serviceStub.getMedarbetaruppdrag()) {
                if (miu.getHsaId().equalsIgnoreCase(parameters.getPersonHsaId())) {
                    response.getCredentialInformation().addAll(miuInformationTypesForEnhetsIds(miu, parameters.getPersonHsaId()));
                }
            }
        }

        return response;
    }

    private List<CredentialInformationType> miuInformationTypesForEnhetsIds(Medarbetaruppdrag medarbetaruppdrag, String hsaPersonId) {
        List<CredentialInformationType> informationTypes = new ArrayList<>();
        CredentialInformationType cit = new CredentialInformationType();
        cit.setPersonHsaId(hsaPersonId);


        for (Vardgivare vardgivare : serviceStub.getVardgivare()) {
            for (Vardenhet enhet : vardgivare.getVardenheter()) {
                if (enhet.getId().endsWith("-finns-ej")) {
                    continue;
                }
                for (Medarbetaruppdrag.Uppdrag uppdrag : medarbetaruppdrag.getUppdrag()) {
                    if (uppdrag.getEnhet().endsWith("-finns-ej")) {
                        continue;
                    }
                    if (uppdrag.getEnhet().equals(enhet.getId())) {

                        // NYTT, lägg på systemRoles från stubbens data ifall sådan finns tillgänglig.
                        addSystemRole(cit, uppdrag);

                        for (String andamal : uppdrag.getAndamal()) {
                            CommissionType miuInfo = new CommissionType();
                            miuInfo.setCommissionHsaId(medarbetaruppdrag.getHsaId());
                            miuInfo.setCommissionPurpose(andamal);
                            miuInfo.setHealthCareUnitHsaId(enhet.getId());
                            miuInfo.setHealthCareUnitName(enhet.getNamn());
                            miuInfo.setHealthCareUnitStartDate(enhet.getStart());
                            miuInfo.setHealthCareUnitEndDate(enhet.getEnd());

                            miuInfo.setHealthCareProviderHsaId(vardgivare.getId());
                            miuInfo.setHealthCareProviderName(vardgivare.getNamn());
                            miuInfo.setHealthCareProviderOrgNo(AgandeForm.PRIVAT.equals(enhet.getAgandeForm()) ? "5555555555" : "2222222222");

                            cit.getCommission().add(miuInfo);
                        }
                    }
                }
            }

        }
        informationTypes.add(cit);
        return informationTypes;
    }

    /**
     * If our user has defined systemRole(s) in the stub, add them here to the credential.

     * @param cit
     * @param uppdrag
     */
    private void addSystemRole(CredentialInformationType cit, Medarbetaruppdrag.Uppdrag uppdrag) {
        if (uppdrag.getSystemRoles() != null) {
            cit.getHsaSystemRole().addAll(uppdrag.getSystemRoles().stream().map((String s) ->
            {
                HsaSystemRoleType systemRole = new HsaSystemRoleType();
                systemRole.setRole(s);
                return systemRole;
            }).collect(Collectors.toList()));
        }

    }
}
