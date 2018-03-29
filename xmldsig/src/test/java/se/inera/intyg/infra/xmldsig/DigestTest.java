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

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static org.junit.Assert.assertEquals;

/**
 * This class doesn't actually test production code, it just validates that the algorithms we
 * use for digests produce expected values given SecMaker documentation.
 */
public class DigestTest {


    
    private String third = "<intyg><intygs-id><root>TSTNMT2321000156-1077</root><extension>6aa1d0fc-1845-41d8-8d31-39c044dca592</extension></intygs-id><typ><code>LUSE</code><codeSystem>b64ea353-e8f6-4832-b563-fc7d46f29548</codeSystem><displayName>Läkarutlåtande för sjukersättning</displayName></typ><version>1.0</version><signeringstidpunkt>2018-03-28T10:30:40</signeringstidpunkt><patient><person-id><root>1.2.752.129.2.1.3.1</root><extension>191212121212</extension></person-id><fornamn></fornamn><efternamn></efternamn><postadress></postadress><postnummer></postnummer><postort></postort></patient><skapadAv><personal-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-1079</extension></personal-id><fullstandigtNamn>Arnold Johansson</fullstandigtNamn><forskrivarkod>0000000</forskrivarkod><enhet><enhets-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-1077</extension></enhets-id><arbetsplatskod><root>1.2.752.29.4.71</root><extension>1234567890</extension></arbetsplatskod><enhetsnamn>NMT vg3 ve1</enhetsnamn><postadress>NMT gata 3</postadress><postnummer>12345</postnummer><postort>Testhult</postort><telefonnummer>0101112131416</telefonnummer><epost>enhet3@webcert.invalid.se</epost><vardgivare><vardgivare-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-102Q</extension></vardgivare-id><vardgivarnamn>NMT vg3</vardgivarnamn></vardgivare></enhet></skapadAv><svar id=\"1\"><instans>1</instans><delsvar id=\"1.1\"><cv><code>UNDERSOKNING</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Min undersökning av patienten</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-08</delsvar></svar><svar id=\"1\"><instans>2</instans><delsvar id=\"1.1\"><cv><code>JOURNALUPPGIFTER</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Journaluppgifter från den</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-09</delsvar></svar><svar id=\"1\"><instans>3</instans><delsvar id=\"1.1\"><cv><code>ANHORIG</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Anhörigs beskrivning av patienten</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-10</delsvar></svar><svar id=\"1\"><instans>4</instans><delsvar id=\"1.1\"><cv><code>ANNAT</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Annat</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-11</delsvar><delsvar id=\"1.3\">Gissar</delsvar></svar><svar id=\"2\"><delsvar id=\"2.1\">1970-01-01</delsvar></svar><svar id=\"3\"><delsvar id=\"3.1\">true</delsvar></svar><svar id=\"4\"><instans>1</instans><delsvar id=\"4.1\"><cv><code>NEUROPSYKIATRISKT</code><codeSystem>KV_FKMU_0005</codeSystem><displayName>Neuropsykiatriskt utlåtande</displayName></cv></delsvar><delsvar id=\"4.2\">2016-08-01</delsvar><delsvar id=\"4.3\">VE1</delsvar></svar><svar id=\"4\"><instans>2</instans><delsvar id=\"4.1\"><cv><code>HABILITERING</code><codeSystem>KV_FKMU_0005</codeSystem><displayName>Underlag från habiliteringen</displayName></cv></delsvar><delsvar id=\"4.2\">2016-08-02</delsvar><delsvar id=\"4.3\">VE2</delsvar></svar><svar id=\"4\"><instans>3</instans><delsvar id=\"4.1\"><cv><code>HABILITERING</code><codeSystem>KV_FKMU_0005</codeSystem><displayName>Underlag från habiliteringen</displayName></cv></delsvar><delsvar id=\"4.2\">2016-08-03</delsvar><delsvar id=\"4.3\">VE3</delsvar></svar><svar id=\"5\"><delsvar id=\"5.1\">Bakgrunden är okänd.</delsvar></svar><svar id=\"6\"><delsvar id=\"6.2\"><cv><code>Y900</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Blodalkoholhalt lägre än 0,2 promille</displayName></cv></delsvar><delsvar id=\"6.1\">Blodalkoholhalt lägre än 0,2 promille</delsvar><delsvar id=\"6.4\"><cv><code>T202</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Brännskada av andra graden på huvudet och halsen</displayName></cv></delsvar><delsvar id=\"6.3\">Brännskada av andra graden på huvudet och halsen</delsvar><delsvar id=\"6.6\"><cv><code>W0134</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Fall i samma plan genom halkning, snavning eller snubbling-idrottsanläggning-vitalaktivitet</displayName></cv></delsvar><delsvar id=\"6.5\">Fall i samma plan genom halkning, snavning eller snubbling-idrottsanläggning-vitalaktivitet</delsvar></svar><svar id=\"7\"><delsvar id=\"7.1\">Förra lördagen på lokala puben</delsvar></svar><svar id=\"45\"><delsvar id=\"45.1\">true</delsvar><delsvar id=\"45.2\">Brännskadan</delsvar></svar><svar id=\"8\"><delsvar id=\"8.1\">Potatis/10</delsvar></svar><svar id=\"9\"><delsvar id=\"9.1\">Ingen funktionsnedsättning av kommunikation och social interaktion</delsvar></svar><svar id=\"10\"><delsvar id=\"10.1\">Väldigt ouppmärksam, kollar i mobilen hela tiden</delsvar></svar><svar id=\"11\"><delsvar id=\"11.1\">Smart person 5/7</delsvar></svar><svar id=\"12\"><delsvar id=\"12.1\">Reagerar inte på smärta</delsvar></svar><svar id=\"13\"><delsvar id=\"13.1\">Bra balans vid sängliggande</delsvar></svar><svar id=\"14\"><delsvar id=\"14.1\">Kroppsliga funktioner fungerar hyggligt</delsvar></svar><svar id=\"17\"><delsvar id=\"17.1\">Inga begränsningar</delsvar></svar><svar id=\"18\"><delsvar id=\"18.1\">Avslutade medicinska behandlingar/åtgärder</delsvar></svar><svar id=\"19\"><delsvar id=\"19.1\">Pågående medicinska behandlingar/åtgärder</delsvar></svar><svar id=\"20\"><delsvar id=\"20.1\">Planerade medicinska behandlingar/åtgärder</delsvar></svar><svar id=\"21\"><delsvar id=\"21.1\">Allt.</delsvar></svar><svar id=\"22\"><delsvar id=\"22.1\">Ser bra ut</delsvar></svar><svar id=\"23\"><delsvar id=\"23.1\">Det mesta</delsvar></svar><svar id=\"25\"><delsvar id=\"25.1\">Lite upplysningar</delsvar></svar><svar id=\"26\"><delsvar id=\"26.1\">true</delsvar><delsvar id=\"26.2\">Ring mig</delsvar></svar></intyg>";

    private String newTest = "<intyg><intygs-id><root>TSTNMT2321000156-1077</root><extension>9f02dd2f-f57c-4a73-8190-2fe602cd6e27</extension></intygs-id><typ><code>LISJP</code><codeSystem>b64ea353-e8f6-4832-b563-fc7d46f29548</codeSystem><displayName>Läkarintyg för sjukpenning</displayName></typ><version>1.0</version><signeringstidpunkt>2018-03-08T11:54:22</signeringstidpunkt><patient><person-id><root>1.2.752.129.2.1.3.1</root><extension>191212121212</extension></person-id><fornamn></fornamn><efternamn></efternamn><postadress></postadress><postnummer></postnummer><postort></postort></patient><skapadAv><personal-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-1079</extension></personal-id><fullstandigtNamn>Arnold Johansson</fullstandigtNamn><forskrivarkod>0000000</forskrivarkod><enhet><enhets-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-1077</extension></enhets-id><arbetsplatskod><root>1.2.752.29.4.71</root><extension>1234567890</extension></arbetsplatskod><enhetsnamn>NMT vg3 ve1</enhetsnamn><postadress>NMT gata 3</postadress><postnummer>12345</postnummer><postort>Testhult</postort><telefonnummer>0101112131416</telefonnummer><epost>enhet3@webcert.invalid.se</epost><vardgivare><vardgivare-id><root>1.2.752.129.2.1.4.1</root><extension>TSTNMT2321000156-102Q</extension></vardgivare-id><vardgivarnamn>NMT vg3</vardgivarnamn></vardgivare></enhet></skapadAv><svar id=\"1\"><instans>1</instans><delsvar id=\"1.1\"><cv><code>UNDERSOKNING</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Min undersokning av patienten</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-01</delsvar></svar><svar id=\"1\"><instans>2</instans><delsvar id=\"1.1\"><cv><code>TELEFONKONTAKT</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Min telefonkontakt med patienten</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-02</delsvar></svar><svar id=\"1\"><instans>3</instans><delsvar id=\"1.1\"><cv><code>JOURNALUPPGIFTER</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Journaluppgifter fran den</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-03</delsvar></svar><svar id=\"1\"><instans>4</instans><delsvar id=\"1.1\"><cv><code>ANNAT</code><codeSystem>KV_FKMU_0001</codeSystem><displayName>Annat</displayName></cv></delsvar><delsvar id=\"1.2\">2016-08-04</delsvar><delsvar id=\"1.3\">Telepatisk kommunikation</delsvar></svar><svar id=\"28\"><instans>1</instans><delsvar id=\"28.1\"><cv><code>NUVARANDE_ARBETE</code><codeSystem>KV_FKMU_0002</codeSystem><displayName>Nuvarande arbete</displayName></cv></delsvar></svar><svar id=\"29\"><delsvar id=\"29.1\">Siare</delsvar></svar><svar id=\"6\"><delsvar id=\"6.2\"><cv><code>D50</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Jarnbristanemi</displayName></cv></delsvar><delsvar id=\"6.1\">Jarnbristanemi</delsvar><delsvar id=\"6.4\"><cv><code>G10</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Huntingtons sjukdom</displayName></cv></delsvar><delsvar id=\"6.3\">Huntingtons sjukdom</delsvar><delsvar id=\"6.6\"><cv><code>T241</code><codeSystem>1.2.752.116.1.1.1.1.3</codeSystem><displayName>Brannskada av forsta graden pa hoft och nedre extremitet utom fotled och fot</displayName></cv></delsvar><delsvar id=\"6.5\">Brannskada av forsta graden pa hoft och nedre extremitet utom fotled och fot</delsvar></svar><svar id=\"35\"><delsvar id=\"35.1\">Inga fynd gjordes</delsvar></svar><svar id=\"17\"><delsvar id=\"17.1\">Har svart att sitta och ligga.. Och sta. Far huka sig.</delsvar></svar><svar id=\"19\"><delsvar id=\"19.1\">Meditering, sjalvmedicinering</delsvar></svar><svar id=\"20\"><delsvar id=\"20.1\">Inga planerade atgarder. Patienten har ingen almanacka.</delsvar></svar><svar id=\"32\"><instans>1</instans><delsvar id=\"32.1\"><cv><code>HELT_NEDSATT</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>100%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-08</start><end>2016-08-22</end></datePeriod></delsvar></svar><svar id=\"32\"><instans>2</instans><delsvar id=\"32.1\"><cv><code>TRE_FJARDEDEL</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>75%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-23</start><end>2016-08-24</end></datePeriod></delsvar></svar><svar id=\"32\"><instans>3</instans><delsvar id=\"32.1\"><cv><code>HALFTEN</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>50%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-25</start><end>2016-08-27</end></datePeriod></delsvar></svar><svar id=\"32\"><instans>4</instans><delsvar id=\"32.1\"><cv><code>EN_FJARDEDEL</code><codeSystem>KV_FKMU_0003</codeSystem><displayName>25%</displayName></cv></delsvar><delsvar id=\"32.2\"><datePeriod><start>2016-08-29</start><end>2016-11-26</end></datePeriod></delsvar></svar><svar id=\"37\"><delsvar id=\"37.1\">Har foljt beslutstodet till punkt och pricka.</delsvar></svar><svar id=\"33\"><delsvar id=\"33.1\">true</delsvar><delsvar id=\"33.2\">Har bra och daliga dagar. Battre att jobba 22h-24h de bra dagarna sa patienten kan vila sedan.</delsvar></svar><svar id=\"34\"><delsvar id=\"34.1\">true</delsvar></svar><svar id=\"39\"><delsvar id=\"39.1\"><cv><code>ATER_X_ANTAL_DGR</code><codeSystem>KV_FKMU_0006</codeSystem><displayName>Patienten kommer med stor sannolikhet att aterga helt i nuvarande sysselsattning efter x antal dagar</displayName></cv></delsvar><delsvar id=\"39.3\"><cv><code>SEXTIO_DGR</code><codeSystem>KV_FKMU_0007</codeSystem><displayName>60 dagar</displayName></cv></delsvar></svar><svar id=\"40\"><instans>1</instans><delsvar id=\"40.1\"><cv><code>ARBETSTRANING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Arbetstraning</displayName></cv></delsvar></svar><svar id=\"40\"><instans>2</instans><delsvar id=\"40.1\"><cv><code>ARBETSANPASSNING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Arbetsanpassning</displayName></cv></delsvar></svar><svar id=\"40\"><instans>3</instans><delsvar id=\"40.1\"><cv><code>SOKA_NYTT_ARBETE</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Soka nytt arbete</displayName></cv></delsvar></svar><svar id=\"40\"><instans>4</instans><delsvar id=\"40.1\"><cv><code>BESOK_ARBETSPLATS</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Besok pa arbetsplatsen</displayName></cv></delsvar></svar><svar id=\"40\"><instans>5</instans><delsvar id=\"40.1\"><cv><code>ERGONOMISK</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Ergonomisk bedomning</displayName></cv></delsvar></svar><svar id=\"40\"><instans>6</instans><delsvar id=\"40.1\"><cv><code>HJALPMEDEL</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Hjalpmedel</displayName></cv></delsvar></svar><svar id=\"40\"><instans>7</instans><delsvar id=\"40.1\"><cv><code>KONFLIKTHANTERING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Konflikthantering</displayName></cv></delsvar></svar><svar id=\"40\"><instans>8</instans><delsvar id=\"40.1\"><cv><code>KONTAKT_FHV</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Kontakt med foretagshalsovard</displayName></cv></delsvar></svar><svar id=\"40\"><instans>9</instans><delsvar id=\"40.1\"><cv><code>OMFORDELNING</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>Omfordelning av arbetsuppgifter</displayName></cv></delsvar></svar><svar id=\"40\"><instans>10</instans><delsvar id=\"40.1\"><cv><code>OVRIGA_ATGARDER</code><codeSystem>KV_FKMU_0004</codeSystem><displayName>ovrigt</displayName></cv></delsvar></svar><svar id=\"44\"><delsvar id=\"44.1\">Darfor.</delsvar></svar><svar id=\"25\"><delsvar id=\"25.1\">Inga ovriga upplysningar.</delsvar></svar><svar id=\"26\"><delsvar id=\"26.1\">true</delsvar><delsvar id=\"26.2\">Alltid roligt att prata med FK.</delsvar></svar></intyg>";

    private  String canon =  "<ns2:intyg xmlns:ns2=\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\"><intygs-id xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">TSTNMT2321000156-1077</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">b6a106a1-7056-417f-9065-356bdb73d2f3</ns3:extension></intygs-id><typ xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><ns3:code xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">LISJP</ns3:code><ns3:codeSystem xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">b64ea353-e8f6-4832-b563-fc7d46f29548</ns3:codeSystem><ns3:displayName xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\" xmlns=\"\">Läkarintyg för sjukpenning</ns3:displayName></typ><version xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\">1.0</version><signeringstidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\">2018-03-09T17:20:27</signeringstidpunkt><skickatTidpunkt xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\">2018-03-09T17:20:27</skickatTidpunkt><patient xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><person-id xmlns=\"\"><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.3.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">191212121212</ns3:extension></person-id><fornamn xmlns=\"\"/><efternamn xmlns=\"\"/><postadress xmlns=\"\"/><postnummer xmlns=\"\"/><postort xmlns=\"\"/></patient><skapadAv xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\"><personal-id xmlns=\"\"><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.4.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">TSTNMT2321000156-1079</ns3:extension></personal-id><fullstandigtNamn xmlns=\"\">Arnold Johansson</fullstandigtNamn><forskrivarkod xmlns=\"\">0000000</forskrivarkod><enhet xmlns=\"\"><enhets-id><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.4.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">TSTNMT2321000156-1077</ns3:extension></enhets-id><arbetsplatskod><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.29.4.71</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1234567890</ns3:extension></arbetsplatskod><enhetsnamn>NMT vg3 ve1</enhetsnamn><postadress>NMT gata 3</postadress><postnummer>12345</postnummer><postort>Testhult</postort><telefonnummer>0101112131416</telefonnummer><epost>enhet3@webcert.invalid.se</epost><vardgivare><vardgivare-id><ns3:root xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">1.2.752.129.2.1.4.1</ns3:root><ns3:extension xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\">TSTNMT2321000156-102Q</ns3:extension></vardgivare-id><vardgivarnamn>NMT vg3</vardgivarnamn></vardgivare></enhet></skapadAv><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"27\"><delsvar xmlns=\"\" id=\"27.1\">true</delsvar></svar><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"6\"><delsvar xmlns=\"\" id=\"6.2\"><ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"><ns3:code>J22</ns3:code><ns3:codeSystem>1.2.752.116.1.1.1.1.3</ns3:codeSystem><ns3:displayName>Icke specificerad akut infektion i nedre luftvägarna</ns3:displayName></ns3:cv></delsvar><delsvar xmlns=\"\" id=\"6.1\">Icke specificerad akut infektion i nedre luftvägarna</delsvar></svar><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"32\"><instans xmlns=\"\">1</instans><delsvar xmlns=\"\" id=\"32.1\"><ns3:cv xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"><ns3:code>EN_FJARDEDEL</ns3:code><ns3:codeSystem>KV_FKMU_0003</ns3:codeSystem><ns3:displayName>25%</ns3:displayName></ns3:cv></delsvar><delsvar xmlns=\"\" id=\"32.2\"><ns3:datePeriod xmlns:ns3=\"urn:riv:clinicalprocess:healthcond:certificate:types:3\"><ns3:start>2018-03-09</ns3:start><ns3:end>2018-04-05</ns3:end></ns3:datePeriod></delsvar></svar><svar xmlns=\"urn:riv:clinicalprocess:healthcond:certificate:3\" id=\"26\"><delsvar xmlns=\"\" id=\"26.1\">false</delsvar></svar></ns2:intyg>";

    private static final String INTYG_DATA = "Data To Be Signed";
    public static final String EXPECTED_SHA1_DIGEST = "PXLVCJ1DFmrG6OQa4RZ6dMd+0Z4=";
    public static final String EXPECTED_SHA256_DIGEST = "6rHZDWzIBQC4xksvOS0xzXgitPn+4EgJpunODzpWaSo=";
    public static final String DATA_TO_BE_SIGNED = "Data To Be Signed";

    private String original_sha1 = "3d72d5089d43166ac6e8e41ae1167a74c77ed19e";
    private String original_sha256 = "eab1d90d6cc80500b8c64b2f392d31cd7822b4f9fee04809a6e9ce0f3a56692a";

    @Before
    public void init() {
        org.apache.xml.security.Init.init();
    }

 //   @Test
    public  void testNewDigest() throws IOException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        assertEquals("ooiyONrIELMcH71p6nBPE4+sM1oIiN6VENqkdS5yxtU=", Base64.getEncoder().encodeToString(messageDigest.digest(third.getBytes())));
    }

    /**
     * Note: We have "proof" that the 3d72d5089d43166ac6e8e41ae1167a74c77ed19e shall digest and base64-encode
     * into PXLVCJ1DFmrG6OQa4RZ6dMd+0Z4= from SecMaker documentation.
     * To transform "Data To Be Signed".getBytes() into original_sha1 do:
     * <code>
     * String rebuilt = Hex.encodeHex(MessageDigest.digest("Data To Be Signed").getBytes())
     * rebuilt.equals(original_sha1) == true
     * </code>
     */
    @Test
    public void testDigestSha1() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

        byte[] data = messageDigest.digest(DATA_TO_BE_SIGNED.getBytes("UTF-8"));

        // Lexically, the example string in uppercase is equal to
        assertEquals(original_sha1.toUpperCase(), DatatypeConverter.printHexBinary(data));

        assertEquals(EXPECTED_SHA1_DIGEST, new String(java.util.Base64.getEncoder().encode(data)));
    }

    @Test
    public void testDigestSha64() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte[] data = messageDigest.digest(INTYG_DATA.getBytes("UTF-8"));
        assertEquals(original_sha256.toUpperCase(), DatatypeConverter.printHexBinary(data));
        assertEquals(EXPECTED_SHA256_DIGEST, new String(java.util.Base64.getEncoder().encode(data)));
    }


    @Test
    public  void testDigest() throws IOException {

        String digest = new XMLDSigServiceImpl().digestToBase64(canon);
        System.out.println(digest);
        System.out.println(Base64.getEncoder().encodeToString(digest.getBytes()));

        InputStream xmlResource = getXmlResource("unsigned/signed-lisjp.xml");
        String purexml = IOUtils.toString(xmlResource);
        String canonxml = new XMLDSigServiceImpl().canonicalizeXml(purexml);
        String digested = new XMLDSigServiceImpl().digestToBase64(canonxml);
        System.out.println(digested);
        System.out.println(Base64.getEncoder().encodeToString(digested.getBytes()));

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
