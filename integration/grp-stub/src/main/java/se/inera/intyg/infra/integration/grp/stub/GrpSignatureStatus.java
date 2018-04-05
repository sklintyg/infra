/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
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

import se.funktionstjanster.grp.v1.ProgressStatusType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Magnus Ekstrand on 2017-05-18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GrpSignatureStatus {

    private String orderRef;

    private ProgressStatusType status;

    public GrpSignatureStatus() {
        super();
    }

    public GrpSignatureStatus(String orderRef, ProgressStatusType status) {
        this.orderRef = orderRef;
        this.status = status;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public ProgressStatusType getStatus() {
        return status;
    }

    public void setStatus(ProgressStatusType status) {
        this.status = status;
    }
}
