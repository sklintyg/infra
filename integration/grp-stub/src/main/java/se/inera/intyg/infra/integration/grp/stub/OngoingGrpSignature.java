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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mobilityguard.grp.service.v2.ProgressStatusType;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OngoingGrpSignature {

    private String personalNumber;
    private String orderRef;
    private String transactionId;
    private ProgressStatusType grpSignatureStatus;
    private LocalDateTime created;

    public OngoingGrpSignature(String personalNumber, String orderRef, String transactionId, ProgressStatusType grpSignatureStatus) {
        this.orderRef = orderRef;
        this.transactionId = transactionId;
        this.grpSignatureStatus = grpSignatureStatus;
        this.created = LocalDateTime.now();
    }

    @JsonIgnore
    public LocalDateTime getCreated() {
        return created;
    }
}
