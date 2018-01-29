package se.inera.intyg.infra.integration.nias.stub;

import com.secmaker.netid.nias.v1.AuthenticateResponse;
import com.secmaker.netid.nias.v1.NetiDAccessServerSoap;
import com.secmaker.netid.nias.v1.ResultCollect;
import com.secmaker.netid.nias.v1.ResultRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXB;
import java.io.StringWriter;

public class NetiDAccessServerSoapStub implements NetiDAccessServerSoap {

    private static final Logger LOG = LoggerFactory.getLogger(NetiDAccessServerSoapStub.class);

    @Override
    public String sign(String s, String s1, String s2, String s3) {
        return null;
    }

    @Override
    public ResultRegister register(String s, String s1, String s2) {
        return null;
    }

    @Override
    public ResultCollect collect(String s) {
        return null;
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
