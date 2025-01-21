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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import com.mobilityguard.grp.service.v2.CollectRequestType;
import com.mobilityguard.grp.service.v2.CollectResponseType;
import com.mobilityguard.grp.service.v2.ProgressStatusType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.funktionstjanster.grp.v2.AuthenticateRequestTypeV23;
import se.funktionstjanster.grp.v2.GrpException;
import se.funktionstjanster.grp.v2.GrpServicePortType;
import se.funktionstjanster.grp.v2.OrderResponseTypeV23;

@ExtendWith(MockitoExtension.class)
public class GrpServicePortTypeStubTest {

    private static final String AUTHENTICATEREQUEST_VALIDATIONMESSAGE = "AuthenticateRequestType cannot be null";
    private static final String AUTHENTICATEREQUEST_ARGUMENTS_VALIDATIONMESSAGE = "A policy must be supplied, "
        + "A provider must be supplied";
    private static final String COLLECTREQUEST_VALIDATIONMESSAGE = "CollectRequestType cannot be null";
    private static final String COLLECTREQUEST_ARGUMENTS_VALIDATIONMESSAGE = "A policy must be supplied, "
        + "A provider must be supplied, An order reference must be supplied";
    private static final String GRP_BANK_ID_PROVIDER = "bankid"; // As specified in CGI GRP docs
    private static final String GRP_SERVICE_ID = "logtest007";
    private static final String GRP_DISPLAY_NAME = "Funktionstjänster Test";

    @Mock
    private GrpServiceStub serviceStub;

    @InjectMocks
    private GrpServicePortType testee = new GrpServicePortTypeStub();

    @Test
    public void nullRequestTypeThrowsException() {
        testAuthenticateRequestInvalidArgument(null, AUTHENTICATEREQUEST_VALIDATIONMESSAGE);
        testCollectRequestInvalidArgument(null, COLLECTREQUEST_VALIDATIONMESSAGE);
    }

    @Test
    public void mandatoryNullArgumentsThrowsException() {
        final var art = new AuthenticateRequestTypeBuilder(null, null).build();
        testAuthenticateRequestInvalidArgument(art, AUTHENTICATEREQUEST_ARGUMENTS_VALIDATIONMESSAGE);

        final var crt = new CollectRequestTypeBuilder(null, null, null).build();
        testCollectRequestInvalidArgument(crt, COLLECTREQUEST_ARGUMENTS_VALIDATIONMESSAGE);
    }

    @Test
    public void mandatoryEmptyArgumentsThrowsException() {
        final var art = new AuthenticateRequestTypeBuilder("", "").build();
        testAuthenticateRequestInvalidArgument(art, AUTHENTICATEREQUEST_ARGUMENTS_VALIDATIONMESSAGE);

        final var crt = new CollectRequestTypeBuilder("", "", "").build();
        testCollectRequestInvalidArgument(crt, COLLECTREQUEST_ARGUMENTS_VALIDATIONMESSAGE);
    }

    @Test
    public void authenticateRequest() throws GrpException {
        final var art = new AuthenticateRequestTypeBuilder(GRP_SERVICE_ID, GRP_BANK_ID_PROVIDER)
            .setDisplayName(GRP_DISPLAY_NAME)
            .build();

        when(serviceStub.updateStatus(anyString(), any())).thenReturn(true);

        final var ort = testee.authenticate(art);
        assertAuthenticateResponse(ort);
    }

    @Test
    public void collectRequest() throws GrpException {
        final var orderRef = UUID.randomUUID().toString();
        final var crt = new CollectRequestTypeBuilder(GRP_SERVICE_ID, GRP_BANK_ID_PROVIDER, orderRef)
            .setDisplayName(GRP_DISPLAY_NAME)
            .build();

        when(serviceStub.getStatus(isNull())).thenReturn(buildCollectResponseType(orderRef));

        final var response = testee.collect(crt);
        assertCollectResponse(response);
    }

    @Test
    public void statusRequest() {
        final var exception = assertThrows(GrpException.class, () -> testee.status(null));
        assertEquals("Not implemented", exception.getMessage());
    }

    @Test
    public void displayNameRequest() {
        final var exception = assertThrows(GrpException.class, () -> testee.displayName(null));
        assertEquals("Not implemented", exception.getMessage());
    }

    @Test
    public void cancelRequest() {
        final var exception = assertThrows(GrpException.class, () -> testee.cancel(null));
        assertEquals("Not implemented", exception.getMessage());
    }

    @Test
    public void signRequest() {
        final var exception = assertThrows(GrpException.class, () -> testee.sign(null));
        assertEquals("Not implemented", exception.getMessage());
    }



    private void assertAuthenticateResponse(OrderResponseTypeV23 ort) {
        assertNotNull(ort.getTransactionId());
        assertFalse(ort.getTransactionId().isEmpty());

        assertNotNull(ort.getOrderRef());
        assertFalse(ort.getOrderRef().isEmpty());

        assertNotNull(ort.getAutoStartToken());
        assertFalse(ort.getAutoStartToken().isEmpty());
    }

    private void assertCollectResponse(CollectResponseType crt) {
        assertNotNull(crt.getTransactionId());
        assertFalse(crt.getTransactionId().isEmpty());

        assertNotNull(crt.getProgressStatus());
        assertSame(crt.getProgressStatus(), ProgressStatusType.STARTED);
    }

    private GrpSignatureStatus buildCollectResponseType(String orderRef) {
        return new GrpSignatureStatus(orderRef, ProgressStatusType.STARTED);
    }

    private void testAuthenticateRequestInvalidArgument(AuthenticateRequestTypeV23 art, String expectedMessage) {
        final var exception = assertThrows(GrpException.class, () -> testee.authenticate(art));
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void testCollectRequestInvalidArgument(CollectRequestType crt, String expectedMessage) {
        final var exception = assertThrows(GrpException.class, () -> testee.collect(crt));
        assertEquals(expectedMessage, exception.getMessage());
    }
}
