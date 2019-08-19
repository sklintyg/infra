/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.hsa.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponderInterface;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonResponseType;
import se.riv.infrastructure.directory.authorizationmanagement.v1.GetCredentialsForPersonIncludingProtectedPersonType;
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
    public void testOk() throws HsaServiceCallException {
        when(credzService.getCredentialsForPersonIncludingProtectedPerson(
            or(isNull(), anyString()),
            any(GetCredentialsForPersonIncludingProtectedPersonType.class))
        ).thenReturn(buildResponse());

        List<CredentialInformationType> authorizationsForPerson = testee.getAuthorizationsForPerson(HSA_ID, null, null);
        assertNotNull(authorizationsForPerson);
    }

    private GetCredentialsForPersonIncludingProtectedPersonResponseType buildResponse() {
        GetCredentialsForPersonIncludingProtectedPersonResponseType resp = new GetCredentialsForPersonIncludingProtectedPersonResponseType();
        resp.setResultCode(ResultCodeEnum.OK);
        return resp;
    }
}
