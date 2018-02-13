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

import com.secmaker.netid.nias.v1.AuthenticateResponse;
import com.secmaker.netid.nias.v1.DeviceInfoType;
import com.secmaker.netid.nias.v1.NetiDAccessServerSoap;
import com.secmaker.netid.nias.v1.ResultCollect;
import com.secmaker.netid.nias.v1.ResultRegister;
import com.secmaker.netid.nias.v1.SignResponse;
import com.secmaker.netid.nias.v1.UserInfoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import se.inera.intyg.infra.integration.nias.stub.model.OngoingSigning;

import javax.xml.bind.JAXB;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class NetiDAccessServerSoapStub implements NetiDAccessServerSoap {

    private static final Logger LOG = LoggerFactory.getLogger(NetiDAccessServerSoapStub.class);
    private static final int NOTBEFORE_OR_AFTER_MINUTES = 5;

    // CHECKSTYLE:OFF LineLength
    private static String testCertificate = "MIIGlzCCBH+gAwIBAgIRANKzVCto/Yfl7EFpkTKewNIwDQYJKoZIhvcNAQEFBQAwQDELMAkGA1UEBhMCU0UxETAPBgNVBAoMCEluZXJhIEFCMR4wHAYDVQQDDBVTSVRIUyBUeXBlIDIgQ0EgdjEg"
            +
            "UFAwHhcNMTQxMDA4MTIzMTQxWhcNMTYxMDA4MjE1ODAwWjCBqTELMAkGA1UEBhMCU0UxGDAWBgoJkiaJk/IsZAEZFghTZXJ2aWNlczEUMBIGCgmSJomT8ixkARkWBE5vZDExETAPBgNVBAoMCEluZXJhIEFCMS4wLAYDVQQDDCVpZHAyLmFjY3R"
            +
            "lc3Quc2FrZXJoZXRzdGphbnN0LmluZXJhLnNlMScwJQYDVQQFEx5UX1NFUlZJQ0VTX1NFMTY1NTY1NTk0MjMwLTEwQjAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCTX5f9jknNAfc0oQaTSPMA8EfbQUoUktlnQ/O74yln7oxTR8"
            +
            "Dvna2N76E13Q3XuSi2XPAuDIPz/OO4xMJhGEgVZxMgyTOHFYGxvtvOhEfncUhVUrEMBARqTPftp+y3reqAn9qc9w9kvu6q21VKZWsvZw62u9wD9IdM1dA1m3Ilgb5cebI6vLdAUpWYQwwtVBtnxpWa1tw1rYBkDUsCoQSrOLe16HueY5sDdnWeu"
            +
            "J4OQ2E1GBh3dBO9ncl0x0hXwzS3wlSPb3EcLPm3N7Vv2ZMZvI5dmOka8pygNjgy6kA2ivBAAmgGbXdxwB406TCCOokPOLyYsr2Wh0MNEp1KOXEXAgMBAAGjggIgMIICHDAOBgNVHQ8BAf8EBAMCAKAwdwYDVR0fBHAwbjAxoC+gLYYraHR0cDov"
            +
            "L2NybDFwcC5zaXRocy5zZS9zaXRoc3R5cGUyY2F2MXBwLmNybDA5oDegNYYzaHR0cDovL2NybDJwcC5zaXRocy5zanVuZXQub3JnL3NpdGhzdHlwZTJjYXYxcHAuY3JsMIHaBggrBgEFBQcBAQSBzTCByjAjBggrBgEFBQcwAYYXaHR0cDovL29"
            +
            "jc3AxcHAuc2l0aHMuc2UwKwYIKwYBBQUHMAGGH2h0dHA6Ly9vY3NwMnBwLnNpdGhzLnNqdW5ldC5vcmcwNgYIKwYBBQUHMAKGKmh0dHA6Ly9haWFwcC5zaXRocy5zZS9zaXRoc3R5cGUyY2F2MXBwLmNlcjA+BggrBgEFBQcwAoYyaHR0cDovL2"
            +
            "FpYXBwLnNpdGhzLnNqdW5ldC5vcmcvc2l0aHN0eXBlMmNhdjFwcC5jZXIwSwYDVR0gBEQwQjBABgcqhXAjYwICMDUwMwYIKwYBBQUHAgEWJ2h0dHA6Ly9ycGFwcC5zaXRocy5zZS9zaXRoc3JwYXYxcHAuaHRtbDAnBgNVHSUEIDAeBggrBgEFB"
            +
            "QcDBAYIKwYBBQUHAwEGCCsGAQUFBwMCMB0GA1UdDgQWBBST0pyEqOjPdHILWq9SxARkG5e+uzAfBgNVHSMEGDAWgBT5V/vYlSUtHe5/9szrwg+78CJv0jANBgkqhkiG9w0BAQUFAAOCAgEATzZZuY9H3i/gonBANHVxQiBHJbfXcCz5TCTxENCG"
            +
            "TAFSlxTEF8xhXue9W41DRo78f18q/nS0WJUDXjeYgR+19PsJdxC6gbFbVFjc8I99Ml6qqCSD3T+8j9HIpqP2VMt5SRXpVGfziyqodadVhNZBo3DM1uIv/oXyKNbPSel5i/C7J+W8tffxQfo/iZpqQ/w7wpiGPO5y3BcZK47D9pFLjnG4JJZ08Pn"
            +
            "5ugG37en1BCMa7bvIhyTuzJjU043Mw/UDCiKty5eP/xfwCzPLkictFJFUlCVjFHGj5boOxnHDObN1dans/Z3jNZMpIT+hM7+UsGw0B2T0+h+360Et9edtJSqgTAQxqwYQcBYtvVDEwsQ5WgWGcMX+ZeBy6jglp2DusshxEuIVRARK+y37I9V8xz"
            +
            "FNxRmeZOZfSvJO0ztOUfifskEkAco4HF9YIg/1eM72lDYU2WVKehm/unRNFb19xKFSmvRpRLHIcP5L6VbOEXTFY7HPvHrXNVYBcGk49A38TT1oklkHPd6xvUJKjpKzQSlKz0jtjcgVYrFMqKJivTM8fjXZ/uQWNK/8E1YzU9P1yq+jBko/r3wJm"
            +
            "nMrTf63Bm1jN6YnlI+esTc7WYTrrwXtona4xteDxR+zD2LBYN7bgwiR9JD24P78WzUjHDB/QEKUAQwHPO0pa8FnOg5Elc4=";
    // CHECKSTYLE:ON LineLength

    @Autowired
    private NiasServiceStub niasServiceStub;

    @Override
    public String sign(String personalNumber, String userVisibleData, String userNonVisibleData, String endUserInfo) {
        String orderRef = UUID.randomUUID().toString();
        OngoingSigning ongoingSigning = new OngoingSigning(orderRef, personalNumber, userVisibleData, userNonVisibleData, endUserInfo,
                NiasSignatureStatus.OUTSTANDING_TRANSACTION);
        niasServiceStub.put(orderRef, ongoingSigning);

        SignResponse signResponse = new SignResponse();
        signResponse.setSignResult(orderRef);

        StringWriter sw = new StringWriter();
        JAXB.marshal(signResponse, sw);
        return sw.toString();
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
            resultCollect.setSignature("signature-data");
            UserInfoType userInfoType = new UserInfoType();
            userInfoType.setCertificate(testCertificate);
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

    @Override
    public String authenticate(String personId, String s1, String s2) {
        LOG.info("ENTER - authenticate with personId {}", personId);
        AuthenticateResponse authenticateResponse = new AuthenticateResponse();
        authenticateResponse.setAuthenticateResult("OK");

        StringWriter sw = new StringWriter();
        JAXB.marshal(authenticateResponse, sw);
        return sw.toString();
    }
}
