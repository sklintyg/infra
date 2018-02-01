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
package se.inera.intyg.infra.integration.nias.stub.model;

import se.inera.intyg.infra.integration.nias.stub.NiasSignatureStatus;

public class OngoingSigning {

    private String orderRef;
    private String personalNumber;
    private String userVisibleData;
    private String userNonVisibleData;
    private String endUserInfo;
    private NiasSignatureStatus status;

    public OngoingSigning(String orderRef, String personalNumber, String userVisibleData, String userNonVisibleData, String endUserInfo) {
        this.orderRef = orderRef;
        this.personalNumber = personalNumber;
        this.userVisibleData = userVisibleData;
        this.userNonVisibleData = userNonVisibleData;
        this.endUserInfo = endUserInfo;
    }

    public OngoingSigning(String orderRef, String personalNumber, String userVisibleData, String userNonVisibleData, String endUserInfo,
            NiasSignatureStatus status) {
        this(orderRef, personalNumber, userVisibleData, userNonVisibleData, endUserInfo);
        this.status = status;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getUserVisibleData() {
        return userVisibleData;
    }

    public void setUserVisibleData(String userVisibleData) {
        this.userVisibleData = userVisibleData;
    }

    public String getUserNonVisibleData() {
        return userNonVisibleData;
    }

    public void setUserNonVisibleData(String userNonVisibleData) {
        this.userNonVisibleData = userNonVisibleData;
    }

    public String getEndUserInfo() {
        return endUserInfo;
    }

    public void setEndUserInfo(String endUserInfo) {
        this.endUserInfo = endUserInfo;
    }

    public NiasSignatureStatus getStatus() {
        return status;
    }

    public void setStatus(NiasSignatureStatus status) {
        this.status = status;
    }
}
