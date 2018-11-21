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

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.funktionstjanster.grp.v1.GrpFault;
import se.funktionstjanster.grp.v1.ProgressStatusType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author Magnus Ekstrand on 2017-05-16.
 */
@Service
@Profile({ "dev", "wc-grp-stub", "wc-all-stubs", "testability-api" })
public class GrpServiceStub {

    // orderRef => status
    private Map<String, ProgressStatusType> signatureStatus = new ConcurrentHashMap<>();

    // txId => orderRef
    private Map<String, String> orderRefMapping = new ConcurrentHashMap<>();

    // txId => personalNumber
    private Map<String, String> personalNumberMapping = new ConcurrentHashMap<>();

    public synchronized List<OngoingGrpSignature> getAll() {
        List<OngoingGrpSignature> outList = new ArrayList<>();
        for (String transactionId : orderRefMapping.keySet()) {
            String orderRef = orderRefMapping.get(transactionId);
            String personalNumber = personalNumberMapping.get(transactionId);
            outList.add(new OngoingGrpSignature(personalNumber, orderRef, transactionId, signatureStatus.get(orderRef)));
        }
        return outList;
    }


    public synchronized GrpSignatureStatus getStatus(String orderRef) {
        ProgressStatusType status = signatureStatus.get(orderRef);
        return new GrpSignatureStatus(orderRef, status);
    }

    public synchronized String getOrderRef(String transactionId) throws GrpFault {
        String orderRef = orderRefMapping.get(transactionId);
        if (isBlank(orderRef)) {
            throw new GrpFault("No mapping between transactionId and orderRef was found");
        }
        return orderRef;
    }

    public synchronized void putOrderRef(String transactionId, String orderRef) throws GrpFault  {
        if (transactionId == null || orderRef == null) {
            throw new GrpFault("Arguments must have values");
        }
        if (orderRefMapping.containsKey(transactionId)) {
            throw new GrpFault("The transactionId key is already associated with a value.");
        }
        try {
            orderRefMapping.put(transactionId, orderRef);
        } catch (Exception exception) {
            throw new GrpFault(exception.getMessage());
        }
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

    public synchronized void fail(String transactionId) {
        String orderRef = orderRefMapping.get(transactionId);
        signatureStatus.remove(orderRef);
        orderRefMapping.remove(transactionId);
        personalNumberMapping.remove(transactionId);
    }

    public void putPersonalNumber(String transactionId, String personalNumber) {
        personalNumberMapping.putIfAbsent(transactionId, personalNumber);
    }

    public String getPersonalNumber(String transactionId) {
        return personalNumberMapping.get(transactionId);
    }
}

