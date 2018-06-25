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

import com.secmaker.netid.nias.v1.DeviceInfoType;
import com.secmaker.netid.nias.v1.NetiDAccessServerSoap;
import com.secmaker.netid.nias.v1.ResultCollect;
import com.secmaker.netid.nias.v1.ResultRegister;
import com.secmaker.netid.nias.v1.UserInfoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.nias.stub.model.OngoingSigning;
import se.inera.intyg.infra.integration.nias.stub.util.Keys;
import se.inera.intyg.infra.integration.nias.stub.util.StubSignUtil;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * NetiD Access Server stub. Will actually perform real signing of the supplied digest using
 * private key and includes the public key in the response.
 */
public class NetiDAccessServerSoapStub implements NetiDAccessServerSoap {

    private static final Logger LOG = LoggerFactory.getLogger(NetiDAccessServerSoapStub.class);

    private static final int NOTBEFORE_OR_AFTER_MINUTES = 5;

    private RSAPrivateKey privKey;
    private X509Certificate x509Certificate;

    @PostConstruct
    void init() {
        Keys keys = StubSignUtil.loadFromKeystore();
        this.privKey = keys.getPrivateKey();
        this.x509Certificate = keys.getX509Certificate();
    }

    @Autowired
    private NiasServiceStub niasServiceStub;

    @Override
    public String sign(String personalNumber, String userVisibleData, String userNonVisibleData, String endUserInfo) {
        String orderRef = UUID.randomUUID().toString();
        OngoingSigning ongoingSigning = new OngoingSigning(orderRef, personalNumber, userVisibleData, userNonVisibleData, endUserInfo,
                NiasSignatureStatus.OUTSTANDING_TRANSACTION);
        niasServiceStub.put(orderRef, ongoingSigning);

        // Seems as the "real" NIAS showroom server just returns a plain string instead of a <SignResponse/>
        return orderRef;
    }

    @Override
    public ResultRegister register(String s, String s1, String s2) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public ResultCollect collect(String orderRef) {
        OngoingSigning ongoingSigning = niasServiceStub.get(orderRef);

        ResultCollect resultCollect = new ResultCollect();

        if (ongoingSigning.getStatus() == NiasSignatureStatus.COMPLETE) {
            resultCollect.setProgressStatus(ongoingSigning.getStatus().name());
            DeviceInfoType deviceInfoType = new DeviceInfoType();
            deviceInfoType.setAddress("192.168.1.129");
            deviceInfoType.setName("OS X");
            deviceInfoType.setVersion("10.11");
            resultCollect.setDeviceInfo(deviceInfoType);
            resultCollect.setSignature(createSignature(ongoingSigning.getUserNonVisibleData().getBytes(StandardCharsets.UTF_8)));
            UserInfoType userInfoType = new UserInfoType();
            try {
                userInfoType.setCertificate(Base64.getEncoder().encodeToString(x509Certificate.getEncoded()));
            } catch (CertificateEncodingException e) {
                throw new IllegalStateException("Unable to encode X509Certificate, msg: " + e.getMessage());
            }
            userInfoType.setGivenName("Tolvan");
            userInfoType.setName("Tolvan Tolvansson");
            userInfoType.setPersonalNumber(ongoingSigning.getPersonalNumber());
            userInfoType.setNotBefore(LocalDateTime.now().minusMinutes(NOTBEFORE_OR_AFTER_MINUTES).format(DateTimeFormatter.ISO_DATE_TIME));
            userInfoType.setNotAfter(LocalDateTime.now().plusMinutes(NOTBEFORE_OR_AFTER_MINUTES).format(DateTimeFormatter.ISO_DATE_TIME));
            resultCollect.setUserInfo(userInfoType);

            // Since it's complete, remove
            niasServiceStub.remove(orderRef);
        } else if (ongoingSigning.getStatus() == NiasSignatureStatus.USER_SIGN
                || ongoingSigning.getStatus() == NiasSignatureStatus.OUTSTANDING_TRANSACTION) {
            resultCollect.setProgressStatus(ongoingSigning.getStatus().name());
        } else {
            // Error state, remove signing and return state
            niasServiceStub.remove(orderRef);
            resultCollect.setProgressStatus(ongoingSigning.getStatus().name());
        }
        return resultCollect;
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

    @Override
    public String authenticate(String personId, String s1, String s2) {
        throw new IllegalStateException("Not implemented");
    }
}
