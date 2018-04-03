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
package se.inera.intyg.infra.xmldsig;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import se.inera.intyg.infra.xmldsig.util.XsltUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class TransformTest {

    @Before
    public void init() {
        org.apache.xml.security.Init.init();
      //  System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.jaxp.SaxonTransformerFactory") ;
        // System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"); // "" "net.sf.saxon.jaxp.SaxonTransformerFactory");
    }

    @Test
    public void testTransform() throws UnsupportedEncodingException {
        InputStream is = getXmlResource("classpath:/unsigned/signed-lisjp-i18n.xml");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        XsltUtil.transform(is, buffer, "stripnamespaces.xslt");
        byte[] bytes = buffer.toByteArray();
        InputStream inputStream = new ByteArrayInputStream(bytes);

        ByteArrayOutputStream out2  = new ByteArrayOutputStream();
        XsltUtil.transform(inputStream, out2, "stripmetadata.xslt");

        ByteArrayOutputStream out3  = new ByteArrayOutputStream();
        XsltUtil.transform(IOUtils.toInputStream(new String(out2.toByteArray()), Charset.forName("UTF-8")), out3, "stripparentelement.xslt");


        String xml = new String(out3.toByteArray(), Charset.forName("UTF-8"));

        System.out.println(xml);
    }

    private InputStream getXmlResource(String source) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext()) {
            Resource resource = context.getResource(source);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
