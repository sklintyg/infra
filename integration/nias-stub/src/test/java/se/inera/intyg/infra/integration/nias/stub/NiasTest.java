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
import org.apache.cxf.helpers.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.inera.intyg.infra.integration.nias.stub.model.OngoingSigning;
import se.inera.intyg.infra.integration.nias.stub.util.StubSignUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NiasTest {

    @Mock
    private NiasServiceStub niasServiceStub;

    @InjectMocks
    private NetiDAccessServerSoapStub testee;

    @Test
    public void testKeys() throws IOException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException {
        RSAPublicKey pubKey = StubSignUtil.loadPublicKey();
        RSAPrivateKey privKey = StubSignUtil.loadPrivateKey();

        testee.setPrivKey(privKey);
        testee.setPubKey(pubKey);

        when(niasServiceStub.get(anyString()))
                .thenReturn(new OngoingSigning("ref", "19121212-1212", null, "my digest", null, NiasSignatureStatus.COMPLETE));
        ResultCollect collectedResult = testee.collect("ref");

        try {
            // Load the public key from the response
            String publicKeyString = collectedResult.getUserInfo().getCertificate();
            X509EncodedKeySpec spec1 = new X509EncodedKeySpec(
                    IOUtils.readBytesFromStream(new ByteArrayInputStream(Base64.getDecoder().decode(publicKeyString))));

            KeyFactory kf1 = KeyFactory.getInstance("RSA");
            RSAPublicKey loadedPublicKey = (RSAPublicKey) kf1.generatePublic(spec1);
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(loadedPublicKey);
            verifier.update("my digest".getBytes());
            boolean verifies = verifier.verify(Base64.getDecoder().decode(collectedResult.getSignature().getBytes()));

            assertTrue(verifies);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            fail("Here be dragons");
        }
    }

}
