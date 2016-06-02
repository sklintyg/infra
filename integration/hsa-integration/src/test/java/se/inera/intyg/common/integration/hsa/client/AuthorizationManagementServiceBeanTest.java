package se.inera.intyg.common.integration.hsa.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.common.support.modules.support.api.exception.ExternalServiceCallException;
import se.riv.infrastructure.directory.authorizationmanagement.v1.*;
import se.riv.infrastructure.directory.v1.CredentialInformationType;
import se.riv.infrastructure.directory.v1.ResultCodeEnum;

/**
 * This test is a bit superfluent since the tested method has no branching or error handling
 * whatsoever.
 *
 * Created by eriklupander on 2016-03-11.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorizationManagementServiceBeanTest {

    private static final String HSA_ID = "hsa-id";

    @Mock
    GetCredentialsForPersonIncludingProtectedPersonResponderInterface credzService;

    @InjectMocks
    private AuthorizationManagementServiceBean testee;

    @Test
    public void testOk() throws ExternalServiceCallException {
        when(credzService.getCredentialsForPersonIncludingProtectedPerson(anyString(), any(GetCredentialsForPersonIncludingProtectedPersonType.class)))
            .thenReturn(buildResponse());
        List<CredentialInformationType> authorizationsForPerson = testee.getAuthorizationsForPerson(HSA_ID, null, null);
        assertNotNull(authorizationsForPerson);
    }

    private GetCredentialsForPersonIncludingProtectedPersonResponseType buildResponse() {
        GetCredentialsForPersonIncludingProtectedPersonResponseType resp = new GetCredentialsForPersonIncludingProtectedPersonResponseType();
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }
}
