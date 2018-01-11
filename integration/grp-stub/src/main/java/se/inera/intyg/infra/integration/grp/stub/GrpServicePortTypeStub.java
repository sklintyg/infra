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

import com.google.common.base.Joiner;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.beans.factory.annotation.Autowired;
import se.funktionstjanster.grp.v1.AuthenticateRequestType;
import se.funktionstjanster.grp.v1.CollectRequestType;
import se.funktionstjanster.grp.v1.CollectResponseType;
import se.funktionstjanster.grp.v1.GrpFault;
import se.funktionstjanster.grp.v1.GrpServicePortType;
import se.funktionstjanster.grp.v1.OrderResponseType;
import se.funktionstjanster.grp.v1.ProgressStatusType;
import se.funktionstjanster.grp.v1.Property;
import se.funktionstjanster.grp.v1.SignRequestType;
import se.funktionstjanster.grp.v1.SignatureFileRequestType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Magnus Ekstrand on 2017-05-16.
 */
public class GrpServicePortTypeStub implements GrpServicePortType {

    public static final String PERSON_ID = "19121212-1212";

    @Autowired
    private GrpServiceStub serviceStub;

    @Override
    public OrderResponseType authenticate(AuthenticateRequestType authenticateRequestType) throws GrpFault {
        validate(authenticateRequestType);

        // Build the response
        OrderResponseType response = new OrderResponseType();

        String transactionId = authenticateRequestType.getTransactionId();
        if (isBlank(transactionId)) {
            transactionId = UUID.randomUUID().toString();
        }
        response.setTransactionId(transactionId);

        response.setOrderRef(UUID.randomUUID().toString());
        response.setAutoStartToken("start");

        // Update GRP service stub
        serviceStub.putOrderRef(response.getTransactionId(), response.getOrderRef());
        serviceStub.updateStatus(response.getOrderRef(), ProgressStatusType.STARTED);

        return response;
    }

    @Override
    public CollectResponseType collect(CollectRequestType collectRequestType) throws GrpFault {
        validate(collectRequestType);

        // Build the response
        CollectResponseType response = new CollectResponseType();

        String transactionId = collectRequestType.getTransactionId();
        if (isBlank(transactionId)) {
            transactionId = UUID.randomUUID().toString();
        }
        response.setTransactionId(transactionId);

        String signature = "{\"signatur\":\"SIGNATURE\"}";
        response.setSignature(signature);

        String orderRef = serviceStub.getOrderRef(response.getTransactionId());
        response.setProgressStatus(serviceStub.getStatus(orderRef).getStatus());

        Property p = new Property();
        p.setName("Subject.SerialNumber");
        p.setValue(PERSON_ID);
        response.getAttributes().add(p);

        return response;
    }

    @Override
    public OrderResponseType sign(SignRequestType signRequestType) throws GrpFault {
        throw new GrpFault("Not implemented");
    }

    @Override
    public OrderResponseType fileSign(SignatureFileRequestType signatureFileRequestType) throws GrpFault {
        throw new GrpFault("Not implemented");
    }

    private void validate(AuthenticateRequestType request) throws GrpFault {
        List<String> messages = new ArrayList<>();

        if (request == null) {
            messages.add("AuthenticateRequestType cannot be null");
        } else {
            if (isBlank(request.getPolicy())) {
                messages.add("A policy must be supplied");
            }
            if (isBlank(request.getProvider())) {
                messages.add("A provider must be supplied");
            }
        }

        if (messages.size() > 0) {
            throw new GrpFault(Joiner.on(", ").join(messages));
        }
    }

    private void validate(CollectRequestType request) throws GrpFault {
        List<String> messages = new ArrayList<>();

        if (request == null) {
            messages.add("CollectRequestType cannot be null");
        } else {
            if (isBlank(request.getPolicy())) {
                messages.add("A policy must be supplied");
            }
            if (isBlank(request.getProvider())) {
                messages.add("A provider must be supplied");
            }
            if (isBlank(request.getOrderRef())) {
                messages.add("An order reference must be supplied");
            }
        }

        if (messages.size() > 0) {
            throw new GrpFault(Joiner.on(", ").join(messages));
        }
    }

}
