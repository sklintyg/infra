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
package se.inera.intyg.infra.integration.grp.stub;

import java.util.List;
import se.funktionstjanster.grp.v1.AuthenticateRequestType;
import se.funktionstjanster.grp.v1.EndUserInfoType;
import se.funktionstjanster.grp.v1.RequirementAlternativesType;

/**
 * @author Magnus Ekstrand on 2017-05-17.
 */
//Builder Class
public class AuthenticateRequestTypeBuilder {

    // required parameters
    private String policy;
    private String provider;

    // optional parameters
    private String displayName;
    private String transactionId;
    private String personalNumber;
    private List<EndUserInfoType> endUserInfo;
    private RequirementAlternativesType requirementAlternatives;

    public AuthenticateRequestTypeBuilder(String policy, String provider) {
        this.policy = policy;
        this.provider = provider;
    }

    public AuthenticateRequestTypeBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public AuthenticateRequestTypeBuilder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public AuthenticateRequestTypeBuilder setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
        return this;
    }

    public AuthenticateRequestTypeBuilder setEndUserInfo(List<EndUserInfoType> endUserInfo) {
        this.endUserInfo = endUserInfo;
        return this;
    }

    public AuthenticateRequestTypeBuilder setRequirementAlternatives(RequirementAlternativesType requirementAlternatives) {
        this.requirementAlternatives = requirementAlternatives;
        return this;
    }

    public AuthenticateRequestType build() {
        AuthenticateRequestType art = new AuthenticateRequestType();
        art.setPolicy(this.policy);
        art.setProvider(this.provider);
        art.setDisplayName(this.displayName);
        art.setTransactionId(this.transactionId);
        art.setPersonalNumber(this.personalNumber);
        art.setRequirementAlternatives(this.requirementAlternatives);

        return art;
    }

}
