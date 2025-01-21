/*
 * Copyright (C) 2025 Inera AB (http://www.inera.se)
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

import com.mobilityguard.grp.service.v2.CollectRequestType;

/**
 * @author Magnus Ekstrand on 2017-05-17.
 */
//Builder Class
public class CollectRequestTypeBuilder {

    // required parameters
    private final String policy;
    private final String provider;
    private final String orderRef;

    // optional parameters
    private String displayName;
    private String transactionId;

    public CollectRequestTypeBuilder(String policy, String provider, String orderRef) {
        this.policy = policy;
        this.provider = provider;
        this.orderRef = orderRef;
    }

    public CollectRequestTypeBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public CollectRequestTypeBuilder setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }

    public CollectRequestType build() {
        CollectRequestType crt = new CollectRequestType();
        crt.setPolicy(this.policy);
        crt.setProvider(this.provider);
        crt.setOrderRef(this.orderRef);
        crt.setRpDisplayName(this.displayName);
        crt.setTransactionId(this.transactionId);
        return crt;
    }

}
