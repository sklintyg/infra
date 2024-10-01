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

import com.google.common.base.Joiner;
import com.mobilityguard.grp.service.v2.CancelRequestType;
import com.mobilityguard.grp.service.v2.CancelResponseType;
import com.mobilityguard.grp.service.v2.CollectRequestType;
import com.mobilityguard.grp.service.v2.CollectResponseType;
import com.mobilityguard.grp.service.v2.DisplayNameRequestType;
import com.mobilityguard.grp.service.v2.DisplayNameResponseType;
import com.mobilityguard.grp.service.v2.GrpFaultType;
import com.mobilityguard.grp.service.v2.ProgressStatusType;
import com.mobilityguard.grp.service.v2.Property;
import com.mobilityguard.grp.service.v2.StatusRequestType;
import com.mobilityguard.grp.service.v2.StatusResponseType;
import com.mobilityguard.grp.service.v2.ValidationInfoType;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import se.funktionstjanster.grp.v2.AuthenticateRequestTypeV23;
import se.funktionstjanster.grp.v2.GrpException;
import se.funktionstjanster.grp.v2.GrpServicePortType;
import se.funktionstjanster.grp.v2.OrderResponseTypeV23;
import se.funktionstjanster.grp.v2.SignRequestTypeV23;
import se.inera.intyg.infra.integration.grp.stub.util.Keys;
import se.inera.intyg.infra.integration.grp.stub.util.StubSignUtil;

/**
 * @author Magnus Ekstrand on 2017-05-16.
 */
public class GrpServicePortTypeStub implements GrpServicePortType {

    @Autowired
    private GrpServiceStub serviceStub;

    private RSAPrivateKey privKey;

    @PostConstruct
    void init() {
        Keys keys = StubSignUtil.loadFromKeystore();
        this.privKey = keys.getPrivateKey();
    }

    @Override
    public OrderResponseTypeV23 authenticate(AuthenticateRequestTypeV23 authenticateRequestType) throws GrpException {
        validate(authenticateRequestType);

        // Build the response
        OrderResponseTypeV23 response = new OrderResponseTypeV23();

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
        serviceStub.putPersonalNumber(response.getTransactionId(), authenticateRequestType.getSubjectIdentifier());

        return response;
    }

    @Override
    public CollectResponseType collect(CollectRequestType collectRequestType) throws GrpException {
        validate(collectRequestType);

        // Build the response
        CollectResponseType response = new CollectResponseType();

        String transactionId = collectRequestType.getTransactionId();
        if (isBlank(transactionId)) {
            transactionId = UUID.randomUUID().toString();
        } else if (serviceStub.isMarkedAsFailed(transactionId)) {
            GrpFaultType faultType = new GrpFaultType();
            faultType.setFaultStatus(serviceStub.getFailureReason(transactionId));
            throw new GrpException(("Marked as failed due to" + faultType + "thru stub api"), faultType);
        }
        response.setTransactionId(transactionId);

        String orderRef = serviceStub.getOrderRef(response.getTransactionId());
        response.setProgressStatus(serviceStub.getStatus(orderRef).getStatus());

        // Sign using a make-believe private key if complete.
        if (response.getProgressStatus() == ProgressStatusType.COMPLETE) {
            final var signature = createSignature(orderRef.getBytes(StandardCharsets.UTF_8));
            final var validationInfo = new ValidationInfoType();
            validationInfo.setSignature(signature);
            response.setValidationInfo(validationInfo);
        }

        Property p = new Property();
        p.setName("Subject.SerialNumber");
        p.setValue(serviceStub.getPersonalNumber(response.getTransactionId()));
        response.getAttributes().add(p);

        return response;
    }

    @Override
    public StatusResponseType status(StatusRequestType statusRequestType) throws GrpException {
        throw new GrpException("Not implemented");
    }

    @Override
    public DisplayNameResponseType displayName(DisplayNameRequestType displayNameRequestType) throws GrpException {
        throw new GrpException("Not implemented");
    }

    @Override
    public OrderResponseTypeV23 sign(SignRequestTypeV23 signRequestTypeV23) throws GrpException {
        throw new GrpException("Not implemented");
    }

    @Override
    public CancelResponseType cancel(CancelRequestType cancelRequestType) throws GrpException {
        throw new GrpException("Not implemented");
    }

    private String createSignature(byte[] digest) {
        try {
            Signature rsa = Signature.getInstance("SHA256withRSA");
            rsa.initSign(privKey);
            rsa.update(digest);
            byte[] signatureBytes = rsa.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new IllegalStateException("Not possible to sign digest: " + e.getMessage());
        }
    }

    private void validate(AuthenticateRequestTypeV23 request) throws GrpException {
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

        if (!messages.isEmpty()) {
            throw new GrpException(Joiner.on(", ").join(messages));
        }
    }

    private void validate(CollectRequestType request) throws GrpException {
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

        if (!messages.isEmpty()) {
            throw new GrpException(Joiner.on(", ").join(messages));
        }
    }
}
