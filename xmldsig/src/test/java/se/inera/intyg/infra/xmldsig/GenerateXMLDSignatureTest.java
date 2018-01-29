package se.inera.intyg.infra.xmldsig;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class GenerateXMLDSignatureTest {

    private GenerateXMLDSignature testee = new GenerateXMLDSignature();

    @Test
    public void testPrepareSignature() throws IOException {
        InputStream is = getXmlResource();
        String digest = testee.prepareSignature(IOUtils.toString(is));
        assertEquals("wR60dCk5VQ4HiA+ooWAwf2WQd3Si6Yp0iipzl35mbO8=", digest);
    }

    @Test
    public void testCanonializer() throws IOException {
        InputStream is = getXmlResource();
        String canXml = testee.canon(IOUtils.toString(is));
        assertNotNull(canXml);
        System.out.println(canXml);
    }

    @Test
    public void testSignIntyg() {
        InputStream xmlResource = getXmlResource();
        testee.generateSignature(xmlResource);
    }

    @Test
    public void testGenerateDigest() throws IOException {
        InputStream xmlResource = getXmlResource();
        String str = IOUtils.toString(xmlResource);
        String digest = testee.generateDigest(str);
        System.out.println(digest);
    }

    private InputStream getXmlResource() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        try {
            Resource resource = context.getResource("classpath:/unsigned/lisjp.xml");
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            context.close();
        }
    }
}
