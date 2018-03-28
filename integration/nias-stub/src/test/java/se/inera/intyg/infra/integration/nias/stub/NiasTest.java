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
package se.inera.intyg.infra.integration.nias.stub;

import com.secmaker.netid.nias.v1.ResultCollect;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.nias.stub.model.OngoingSigning;
import se.inera.intyg.infra.integration.nias.stub.util.Keys;
import se.inera.intyg.infra.integration.nias.stub.util.StubSignUtil;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NiasTest {

    @Mock
    private NiasServiceStub niasServiceStub;

    @InjectMocks
    private NetiDAccessServerSoapStub testee;

    @Test
    public void testKeys() throws IOException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        testee.init();

        Keys keys = StubSignUtil.loadFromKeystore();

        when(niasServiceStub.get(anyString()))
                .thenReturn(new OngoingSigning("ref", "19121212-1212", null, "my digest", null, NiasSignatureStatus.COMPLETE));
        ResultCollect collectedResult = testee.collect("ref");

        try {
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(keys.getX509Certificate().getPublicKey());
            verifier.update("my digest".getBytes());
            boolean verifies = verifier.verify(Base64.getDecoder().decode(collectedResult.getSignature().getBytes()));

            assertTrue(verifies);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            fail("Here be dragons");
        }
    }

}
