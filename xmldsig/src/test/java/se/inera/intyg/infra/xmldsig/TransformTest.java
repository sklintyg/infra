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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import se.inera.intyg.infra.xmldsig.util.XsltUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class TransformTest {

    private String expected = "<intyg><intygs-id><root>TSTNMT2321000156-1077</root><extension>9f02dd2f-f57c-4a73-8190-2fe602cd6e27</extension></intygs-id><typ><code>LISJP</code><codeSystem>b64ea353-e8f6-4832-b563-fc7d46f29548</codeSystem><displayName>Läkarintyg för sjukpenning</displayName></typ><version>1.0</version><signeringstidpunkt>2018-03-08T11:54:22</signeringstidpunkt><patient><person-id><root>1.2.752.129.2.1.3.1</root><extension>191212121212</extension></person-id><fornamn/><efternamn/><postadress/><postnummer/><postort/></patient><skapadAv><personal-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-1079</extension></personal-id><fullstandigtNamn>Arnold Johansson</fullstandigtNamn><forskrivarkod>0000000</forskrivarkod><enhet><enhets-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-1077</extension></enhets-id><arbetsplatskod><root>1.2.752.29.4.71</root><extension>1234567890</extension></arbetsplatskod><enhetsnamn>NMT vg3 ve1</enhetsnamn><postadress>NMT gata 3</postadress><postnummer>12345</postnummer><postort>Testhult</postort><telefonnummer>0101112131416</telefonnummer><epost>enhet3@webcert.invalid.se</epost><vardgivare><vardgivare-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-102Q</extension></vardgivare-id><vardgivarnamn>NMT vg3</vardgivarnamn></vardgivare></enhet></skapadAv><svar id=\"1\"><instans>1</instans><delsvar id=\"1.1\"><cv><code>UNDERSOKNING</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Min undersokning av patienten</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-01</delsvar></svar><svar id=\"1\"><instans>2</instans><delsvar id=\"1.1\"><cv><code>TELEFONKONTAKT</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Min telefonkontakt med patienten</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-02</delsvar></svar><svar id=\"1\"><instans>3</instans><delsvar id=\"1.1\"><cv><code>JOURNALUPPGIFTER</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Journaluppgifter fran den</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-03</delsvar></svar><svar id=\"1\"><instans>4</instans><delsvar id=\"1.1\"><cv><code>ANNAT</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Annat</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-04</delsvar><delsvar id=\"1.3\">Telepatisk kommunikation</delsvar></svar><svar id=\"28\"><instans>1</instans><delsvar id=\"28.1\"><cv><code>NUVARANDE_ARBETE</code><codeSystem>KV_FKMU_0002</codeSystem><displayName>Nuvarande arbete</displayName></cv></delsvar></svar><svar id=\"29\"><delsvar id=\"29.1\">Siare</delsvar></svar><svar id=\"6\"><delsvar id=\"6.2\"><cv><code>D50</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Jarnbristanemi</displayName></cv></delsvar><delsvar id=\"6.1\">Jarnbristanemi</delsvar><delsvar id=\"6.4\"><cv><code>G10</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Huntingtons sjukdom</displayName></cv></delsvar><delsvar id=\"6.3\">Huntingtons sjukdom</delsvar><delsvar id=\"6.6\"><cv><code>T241</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Brannskada av forsta graden pa hoft och nedre extremitet utom fotled och fot</displayName></cv></delsvar><delsvar id=\"6.5\">Brannskada av forsta graden pa hoft och nedre extremitet utom fotled och fot</delsvar></svar><svar id=\"35\"><delsvar id=\"35.1\">Inga fynd gjordes</delsvar></svar><svar id=\"17\"><delsvar id=\"17.1\">Har svart att sitta och ligga.. Och sta. Far huka sig.</delsvar></svar><svar id=\"19\"><delsvar id=\"19.1\">Meditering, sjalvmedicinering</delsvar></svar><svar id=\"20\"><delsvar id=\"20.1\">Inga planerade atgarder. Patienten har ingen almanacka.</delsvar></svar><svar id=\"32\"><instans>1</instans><delsvar id=\"32.1\"><cv><code>HELT_NEDSATT</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>100%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-08</start><end>2016-08-22</end></datePeriod></delsvar></svar><svar id=\"32\"><instans>2</instans><delsvar id=\"32.1\"><cv><code>TRE_FJARDEDEL</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>75%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-23</start><end>2016-08-24</end></datePeriod></delsvar></svar><svar id=\"32\"><instans>3</instans><delsvar id=\"32.1\"><cv><code>HALFTEN</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>50%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-25</start><end>2016-08-27</end></datePeriod></delsvar></svar><svar id=\"32\"><instans>4</instans><delsvar id=\"32.1\"><cv><code>EN_FJARDEDEL</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>25%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-29</start><end>2016-11-26</end></datePeriod></delsvar></svar><svar id=\"37\"><delsvar id=\"37.1\">Har foljt beslutstodet till punkt och pricka.</delsvar></svar><svar id=\"33\"><delsvar id=\"33.1\">true</delsvar><delsvar id=\"33.2\">Har bra och daliga dagar. Battre att jobba 22h-24h de bra dagarna sa patienten kan vila sedan.</delsvar></svar><svar id=\"34\"><delsvar id=\"34.1\">true</delsvar></svar><svar id=\"39\"><delsvar id=\"39.1\"><cv><code>ATER_X_ANTAL_DGR</code><codeSystem>KV_FKMU_0006</codeSystem><displayName>Patienten kommer med stor sannolikhet att aterga helt i nuvarande sysselsattning efter x antal dagar</displayName></cv></delsvar><delsvar id=\"39.3\"><cv><code>SEXTIO_DGR</code><codeSystem>KV_FKMU_0007</codeSystem><displayName>60 dagar</displayName></cv></delsvar></svar><svar id=\"40\"><instans>1</instans><delsvar id=\"40.1\"><cv><code>ARBETSTRANING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Arbetstraning</displayName></cv></delsvar></svar><svar id=\"40\"><instans>2</instans><delsvar id=\"40.1\"><cv><code>ARBETSANPASSNING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Arbetsanpassning</displayName></cv></delsvar></svar><svar id=\"40\"><instans>3</instans><delsvar id=\"40.1\"><cv><code>SOKA_NYTT_ARBETE</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Soka nytt arbete</displayName></cv></delsvar></svar><svar id=\"40\"><instans>4</instans><delsvar id=\"40.1\"><cv><code>BESOK_ARBETSPLATS</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Besok pa arbetsplatsen</displayName></cv></delsvar></svar><svar id=\"40\"><instans>5</instans><delsvar id=\"40.1\"><cv><code>ERGONOMISK</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Ergonomisk bedomning</displayName></cv></delsvar></svar><svar id=\"40\"><instans>6</instans><delsvar id=\"40.1\"><cv><code>HJALPMEDEL</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Hjalpmedel</displayName></cv></delsvar></svar><svar id=\"40\"><instans>7</instans><delsvar id=\"40.1\"><cv><code>KONFLIKTHANTERING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Konflikthantering</displayName></cv></delsvar></svar><svar id=\"40\"><instans>8</instans><delsvar id=\"40.1\"><cv><code>KONTAKT_FHV</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Kontakt med foretagshalsovard</displayName></cv></delsvar></svar><svar id=\"40\"><instans>9</instans><delsvar id=\"40.1\"><cv><code>OMFORDELNING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Omfordelning av arbetsuppgifter</displayName></cv></delsvar></svar><svar id=\"40\"><instans>10</instans><delsvar id=\"40.1\"><cv><code>OVRIGA_ATGARDER</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>ovrigt</displayName></cv></delsvar></svar><svar id=\"44\"><delsvar id=\"44.1\">Darfor.</delsvar></svar><svar id=\"25\"><delsvar id=\"25.1\">Inga ovriga upplysningar.</delsvar></svar><svar id=\"26\"><delsvar id=\"26.1\">true</delsvar><delsvar id=\"26.2\">Alltid roligt att prata med FK.</delsvar></svar></intyg>";

    @Before
    public void init() {
        org.apache.xml.security.Init.init();
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.jaxp.SaxonTransformerFactory") ;
        //System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl"); // "" "net.sf.saxon.jaxp.SaxonTransformerFactory");
    }

    @Test
    public void testTransform() throws UnsupportedEncodingException {
        InputStream is = getXmlResource("classpath:/unsigned/signed-lisjp-i18n.xml");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        XsltUtil.transform(is, buffer, "stripall.xslt");
        byte[] bytes = buffer.toByteArray();

        String actual = new String(bytes, Charset.forName("UTF-8"));
        System.out.println(actual);

        Assert.assertEquals(expected, actual);

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
