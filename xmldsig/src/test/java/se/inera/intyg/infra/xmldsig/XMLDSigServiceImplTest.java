package se.inera.intyg.infra.xmldsig;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import se.inera.intyg.infra.xmldsig.model.SignatureType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XMLDSigServiceImplTest {

    private static final String EXPECTED_DIGEST = "O9yXfiJLhy455fp0m041p0my5NTBJKl+YdIdZTA/Smg=";
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final String INTYG_XML_LOCATION = "classpath:/unsigned/lisjp.xml";

    private XMLDSigServiceImpl testee = new XMLDSigServiceImpl();

    @Before
    public void init() {
        testee.init();
    }

    @Test
    public void testBuildPartialSignatureWithDigest() throws IOException {
        InputStream xmlResourceInputStream = getXmlResource();
        SignatureType signatureType = testee.prepareSignature(IOUtils.toString(xmlResourceInputStream));
        assertNotNull(signatureType);

        byte[] digestValue = signatureType.getSignedInfo().getReference().get(0).getDigestValue();
        assertEquals(EXPECTED_DIGEST, new String(digestValue, UTF8));
        assertTrue(Arrays.equals(EXPECTED_DIGEST.getBytes(), digestValue));
    }


    private InputStream getXmlResource() {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext()) {
            Resource resource = context.getResource(INTYG_XML_LOCATION);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
