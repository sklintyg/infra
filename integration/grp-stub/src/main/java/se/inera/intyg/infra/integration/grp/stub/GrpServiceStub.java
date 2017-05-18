/**
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.integration.grp.stub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import se.funktionstjanster.grp.v1.GrpFault;
import se.funktionstjanster.grp.v1.ProgressStatusType;

/**
 * @author Magnus Ekstrand on 2017-05-16.
 */
@Service
@Profile({ "dev", "wc-grp-stub", "wc-all-stubs", "testability-api" })
public class GrpServiceStub {

    private Map<String, ProgressStatusType> signatureStatus = new ConcurrentHashMap<>();

    public synchronized GrpSignatureStatus getStatus(String orderRef) {
        ProgressStatusType status = signatureStatus.get(orderRef);
        return new GrpSignatureStatus(orderRef, status);
    }

    public synchronized boolean updateStatus(GrpSignatureStatus status) throws GrpFault {
        if (status == null) {
            return false;
        }
        return updateStatus(status.getOrderRef(), status.getStatus());
    }

    public synchronized boolean updateStatus(String orderRef, ProgressStatusType status) throws GrpFault {
        if (orderRef == null || status == null) {
            return false;
        }

        try {
            signatureStatus.put(orderRef, status);
        } catch (Exception exception) {
            throw new GrpFault(exception.getMessage());
        }

        return true;
    }

}

