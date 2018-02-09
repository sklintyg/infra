package se.inera.intyg.infra.xmldsig;


import org.apache.xml.security.c14n.Canonicalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.xmldsig.factory.PartialSignatureFactory;
import se.inera.intyg.infra.xmldsig.model.SignatureType;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class XMLDSigServiceImpl implements XMLDSigService {

    private static final Logger LOG = LoggerFactory.getLogger(XMLDSigServiceImpl.class);

    @PostConstruct
    public void init() {
        org.apache.xml.security.Init.init();
    }

    @Override
    public SignatureType prepareSignature(String intygXml) {
        SignatureType signatureType = PartialSignatureFactory.buildSignature();

        String canonicalizedXml = canonicalizeXml(intygXml);
        byte[] digest = generateDigest(canonicalizedXml);
        signatureType.getSignedInfo().getReference().get(0).setDigestValue(digest);

        return signatureType;
    }

    private byte[] generateDigest(String canonXmlString) {
        try {

            // get instance of the message digest based on the SHA-256 hashing algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // call the digest method passing the byte stream on the text, this directly updates the message
            // being digested and perform the hashing
            byte[] hash = digest.digest(canonXmlString.getBytes(StandardCharsets.UTF_8));

            // encode the endresult byte hash and return
            return Base64.getEncoder().encode(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    private String canonicalizeXml(String intygXml) {
        try {
            Canonicalizer canonicalizer = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
            byte[] canonicalizedXmlAsBytes = canonicalizer.canonicalize(intygXml.getBytes("UTF-8"));
            return new String(canonicalizedXmlAsBytes, Charset.forName("UTF-8"));
        } catch (Exception e) {
            LOG.error(e.getClass().getName() + " caught canonicalizing intyg XML, message: " + e.getMessage());
            throw new IllegalArgumentException(e.getCause());
        }
    }
}
