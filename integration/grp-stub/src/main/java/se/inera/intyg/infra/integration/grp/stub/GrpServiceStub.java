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
package se.inera.intyg.infra.integration.grp.stub;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.mobilityguard.grp.service.v2.FaultStatusType;
import com.mobilityguard.grp.service.v2.GrpFaultType;
import com.mobilityguard.grp.service.v2.ProgressStatusType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import se.funktionstjanster.grp.v2.GrpException;

/**
 * @author Magnus Ekstrand on 2017-05-16.
 */
@Service
@Profile({"dev", "wc-grp-stub", "wc-all-stubs", "testability-api"})
public class GrpServiceStub {

    public static final int FOUR = 4;
    // transactionId => reason for failure, will be returned to WC on next collect
    private final Map<String, FaultStatusType> failedSignings = new ConcurrentHashMap<>();

    // orderRef => status
    private final Map<String, ProgressStatusType> signatureStatus = new ConcurrentHashMap<>();

    // txId => orderRef
    private final Map<String, String> orderRefMapping = new ConcurrentHashMap<>();

    // txId => personalNumber
    private final Map<String, String> personalNumberMapping = new ConcurrentHashMap<>();

    // txId => created
    private final Map<String, LocalDateTime> orderRefCreatedMapping = new ConcurrentHashMap<>();

    public FaultStatusType getFailureReason(String transactionId) {
        return failedSignings.get(transactionId);
    }

    public boolean isMarkedAsFailed(String transactionId) {
        return failedSignings.get(transactionId) != null;
    }

    public synchronized List<OngoingGrpSignature> getAll() {
        List<OngoingGrpSignature> outList = new ArrayList<>();
        for (String transactionId : orderRefMapping.keySet()) {
            String orderRef = orderRefMapping.get(transactionId);
            String personalNumber = personalNumberMapping.get(transactionId);
            outList.add(new OngoingGrpSignature(personalNumber, orderRef, transactionId, signatureStatus.get(orderRef)));
        }
        prune();
        return outList;
    }


    public synchronized GrpSignatureStatus getStatus(String orderRef) {
        ProgressStatusType status = signatureStatus.get(orderRef);
        return new GrpSignatureStatus(orderRef, status);
    }

    public synchronized String getOrderRef(String transactionId) throws GrpException {
        String orderRef = orderRefMapping.get(transactionId);
        if (isBlank(orderRef)) {
            // If transaction doesn't exist, we've already removed it since it's old (or WC got things backwards)
            GrpFaultType faultType = new GrpFaultType();
            faultType.setFaultStatus(FaultStatusType.EXPIRED_TRANSACTION);
            throw new GrpException("No mapping between transactionId and orderRef was found, assuming timeout", faultType);
        }
        return orderRef;
    }

    public synchronized void putOrderRef(String transactionId, String orderRef) throws GrpException {
        if (transactionId == null || orderRef == null) {
            throw new GrpException("Arguments must have values");
        }
        if (orderRefMapping.containsKey(transactionId)) {
            throw new GrpException("The transactionId key is already associated with a value.");
        }
        try {
            orderRefMapping.put(transactionId, orderRef);
            orderRefCreatedMapping.put(transactionId, LocalDateTime.now());
        } catch (Exception exception) {
            throw new GrpException(exception.getMessage());
        }
    }

    public synchronized boolean updateStatus(GrpSignatureStatus status) throws GrpException {
        if (status == null) {
            return false;
        }
        return updateStatus(status.getOrderRef(), status.getStatus());
    }

    public synchronized boolean updateStatus(String orderRef, ProgressStatusType status) throws GrpException {
        if (orderRef == null || status == null) {
            return false;
        }

        try {
            signatureStatus.put(orderRef, status);
        } catch (Exception exception) {
            throw new GrpException(exception.getMessage());
        }

        return true;
    }

    public synchronized void fail(String transactionId, FaultStatusType failReason) {
        // Mark transaction as failed
        failedSignings.put(transactionId, failReason);
    }

    public synchronized void remove(String transactionId) {
        String orderRef = orderRefMapping.get(transactionId);
        signatureStatus.remove(orderRef);
        orderRefMapping.remove(transactionId);
        personalNumberMapping.remove(transactionId);
        failedSignings.remove(transactionId);
    }

    public void putPersonalNumber(String transactionId, String personalNumber) {
        personalNumberMapping.putIfAbsent(transactionId, personalNumber);
    }

    public String getPersonalNumber(String transactionId) {
        return personalNumberMapping.get(transactionId);
    }

    private void prune() {
        final var i = orderRefMapping.entrySet().iterator();

        while (i.hasNext()) {
            final var entry = i.next();
            final var transactionId = entry.getKey();
            final var orderRef = entry.getValue();
            final var created = orderRefCreatedMapping.get(entry.getKey());

            if (LocalDateTime.now().isAfter(created.plusMinutes(FOUR))) {
                orderRefCreatedMapping.remove(entry.getKey());
                signatureStatus.remove(orderRef);
                personalNumberMapping.remove(transactionId);
                failedSignings.remove(transactionId);
                i.remove();
            }
        }
    }
}
