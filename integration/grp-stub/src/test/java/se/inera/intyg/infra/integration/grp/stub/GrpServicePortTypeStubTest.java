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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.funktionstjanster.grp.v1.AuthenticateRequestType;
import se.funktionstjanster.grp.v1.CollectRequestType;
import se.funktionstjanster.grp.v1.CollectResponseType;
import se.funktionstjanster.grp.v1.GrpFault;
import se.funktionstjanster.grp.v1.GrpServicePortType;
import se.funktionstjanster.grp.v1.OrderResponseType;
import se.funktionstjanster.grp.v1.ProgressStatusType;

import java.util.UUID;

/**
 * @author Magnus Ekstrand on 2017-05-17.
 */
@RunWith(MockitoJUnitRunner.class)
public class GrpServicePortTypeStubTest {
    private final String AUTHENTICATEREQUEST_VALIDATIONMESSAGE = "AuthenticateRequestType cannot be null";
    private final String AUTHENTICATEREQUEST_ARGUMENTS_VALIDATIONMESSAGE = "A policy must be supplied, " +
        "A provider must be supplied";

    private final String COLLECTREQUEST_VALIDATIONMESSAGE = "CollectRequestType cannot be null";
    private final String COLLECTREQUEST_ARGUMENTS_VALIDATIONMESSAGE = "A policy must be supplied, " +
        "A provider must be supplied, An order reference must be supplied";

    private final String GRP_BANK_ID_PROVIDER = "bankid"; // As specified in CGI GRP docs
    private final String GRP_SERVICE_ID = "logtest007";
    private final String GRP_DISPLAY_NAME = "Funktionstjänster Test";

    @Mock
    private GrpServiceStub serviceStub;

    @InjectMocks
    private GrpServicePortType testee = new GrpServicePortTypeStub();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullRequestTypeThrowsException() throws GrpFault {
        testAuthenticateRequestInvalidArgument(null, AUTHENTICATEREQUEST_VALIDATIONMESSAGE);
        testCollectRequestInvalidArgument(null, COLLECTREQUEST_VALIDATIONMESSAGE);
    }

    @Test
    public void mandatoryNullArgumentsThrowsException() throws GrpFault {
        AuthenticateRequestType art = new AuthenticateRequestTypeBuilder(null, null).build();
        testAuthenticateRequestInvalidArgument(art, AUTHENTICATEREQUEST_ARGUMENTS_VALIDATIONMESSAGE);

        CollectRequestType crt = new CollectRequestTypeBuilder(null, null, null).build();
        testCollectRequestInvalidArgument(crt, COLLECTREQUEST_ARGUMENTS_VALIDATIONMESSAGE);
    }

    @Test
    public void mandatoryEmptyArgumentsThrowsException() throws GrpFault {
        AuthenticateRequestType art = new AuthenticateRequestTypeBuilder("", "").build();
        testAuthenticateRequestInvalidArgument(art, AUTHENTICATEREQUEST_ARGUMENTS_VALIDATIONMESSAGE);

        CollectRequestType crt = new CollectRequestTypeBuilder("", "", "").build();
        testCollectRequestInvalidArgument(crt, COLLECTREQUEST_ARGUMENTS_VALIDATIONMESSAGE);
    }

    @Test
    public void authenticateRequest() throws GrpFault {
        AuthenticateRequestType art = new AuthenticateRequestTypeBuilder(GRP_SERVICE_ID, GRP_BANK_ID_PROVIDER)
            .setDisplayName(GRP_DISPLAY_NAME)
            .build();

        when(serviceStub.updateStatus(anyString(), any())).thenReturn(true);

        OrderResponseType ort = testee.authenticate(art);
        assertAuthenticateResponse(ort);
    }

    @Test
    public void collectRequest() throws GrpFault {
        String orderRef = UUID.randomUUID().toString();
        CollectRequestType crt = new CollectRequestTypeBuilder(GRP_SERVICE_ID, GRP_BANK_ID_PROVIDER, orderRef)
            .setDisplayName(GRP_DISPLAY_NAME)
            .build();

        when(serviceStub.getStatus(anyString())).thenReturn(buildCollectResponseType(orderRef));

        CollectResponseType response = testee.collect(crt);
        assertCollectResponse(response);
    }

    @Test
    public void signRequest() throws GrpFault {
        thrown.expect(GrpFault.class);
        thrown.expectMessage("Not implemented");
        testee.sign(null);
    }

    @Test
    public void signatureFileRequest() throws GrpFault {
        thrown.expect(GrpFault.class);
        thrown.expectMessage("Not implemented");
        testee.fileSign(null);
    }

    private void assertAuthenticateResponse(OrderResponseType ort) {
        assertNotNull(ort.getTransactionId());
        assertTrue(ort.getTransactionId().length() > 0);

        assertNotNull(ort.getOrderRef());
        assertTrue(ort.getOrderRef().length() > 0);

        assertNotNull(ort.getAutoStartToken());
        assertTrue(ort.getAutoStartToken().length() > 0);
    }

    private void assertCollectResponse(CollectResponseType crt) {
        assertNotNull(crt.getTransactionId());
        assertTrue(crt.getTransactionId().length() > 0);

        assertNotNull(crt.getProgressStatus());
        assertTrue(crt.getProgressStatus() == ProgressStatusType.STARTED);
    }

    private GrpSignatureStatus buildCollectResponseType(String orderRef) {
        return new GrpSignatureStatus(orderRef, ProgressStatusType.STARTED);
    }

    private void testAuthenticateRequestInvalidArgument(AuthenticateRequestType art, String expectedMessage) throws GrpFault {
        thrown.expect(GrpFault.class);
        thrown.expectMessage(expectedMessage);
        testee.authenticate(art);
    }

    private void testCollectRequestInvalidArgument(CollectRequestType crt, String expectedMessage) throws GrpFault {
        thrown.expect(GrpFault.class);
        thrown.expectMessage(expectedMessage);
        testee.collect(crt);
    }
}
