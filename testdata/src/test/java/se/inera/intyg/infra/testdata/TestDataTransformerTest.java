/*
 * Copyright (C) 2020 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.testdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import org.junit.Ignore;
import org.junit.Test;

public class TestDataTransformerTest {

    @Test
    @Ignore
    public void transformIntygJSONModel() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "{\n"
            + "  \"data\": {\n"
            + "    \"status\": \"SIGNED\",\n"
            + "    \"skapad\": \"{-2h}\",\n"
            + "    \"version\": 1.0,\n"
            + "    \"senast_sparad_datum\": \"{-1h30m}\",\n"
            + "    \"senast_sparad_av_namn\": \"Arnold Johansson\",\n"
            + "    \"senast_sparad_av_hsaid\": \"TSTNMT2321000156-1079\",\n"
            + "    \"vidarebefordrad\": \"FALSE\",\n"
            + "    \"skickad_till_mottagare\": \"FKASSA\",\n"
            + "    \"skickad_till_mottagare_datum\": \"{-65m}\",\n"
            + "    \"aterkallad_datum\": \"\",\n"
            + "    \"model\": {\n"
            + "      \"id\": \"9020fbb9-e387-40b0-ba75-ac2746e4736b\",\n"
            + "      \"grundData\": {\n"
            + "        \"signeringsdatum\": \"{-80m}\",\n"
            + "        \"skapadAv\": {\n"
            + "          \"personId\": \"TSTNMT2321000156-1079\",\n"
            + "          \"fullstandigtNamn\": \"Arnold Johansson\",\n"
            + "          \"forskrivarKod\": \"0000000\",\n"
            + "          \"befattningar\": [\n"
            + "            \"203090\"\n"
            + "          ],\n"
            + "          \"specialiteter\": []\n"
            + "        },\n"
            + "        \"testIntyg\": false\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}";

        JsonNode data = mapper.readTree(jsonString);

        JsonNode result = TestDataTransformer.transformIntyg(data);

        String json = result.toString();

        fail();
    }

    @Test
    //@Ignore
    public void transformIntygXMLModel() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String jsonString = "{\n"
            + "  \"certificate\":{\n"
            + "      \"id\": \"2020ddb9-e387-40dd-bfdd-ac2746e473dd\",\n"
            + "      \"type\": \"ts-diabetes\",\n"
            + "      \"typeVersion\": \"3.0\",\n"
            + "      \"signingDoctorName\": \"Arnold Johansson\",\n"
            + "      \"careUnitId\": \"TSTNMT2321000156-1077\",\n"
            + "      \"careUnitName\": \"NMT vg3 ve1\",\n"
            + "      \"careGiverId\": \"TSTNMT2321000156-102Q\",\n"
            + "      \"civicRegistrationNumber\": \"191212121212\",\n"
            + "      \"signedDate\": \"{-6h}\",\n"
            + "      \"additionalInfo\": \"AM, A1, A2, A, B, BE, Traktor, C1, C1E, C, CE, D1, D1E, D, DE, Taxi\",\n"
            + "      \"deletedByCareGiver\": false,\n"
            + "      \"wireTapped\": false,\n"
            + "      \"certificateStates\": [\n"
            + "          {\n"
            + "              \"target\": \"TRANSP\",\n"
            + "              \"state\": \"SENT\",\n"
            + "              \"timestamp\": \"{-5h}\"\n"
            + "          },\n"
            + "          {\n"
            + "              \"target\": \"HSVARD\",\n"
            + "              \"state\": \"RECEIVED\",\n"
            + "              \"timestamp\": \"{-4h}\"\n"
            + "          }\n"
            + "      ],\n"
            + "      \"testCertificate\": false,\n"
            + "      \"deleted\": false,\n"
            + "      \"revoked\": false,\n"
            + "      \"originalCertificate\": \"<?xml version=\\\"1.0\\\"?>    <ns2:RegisterCertificate xmlns=\\\"urn:riv:clinicalprocess:healthcond:certificate:3\\\" xmlns:ns2=\\\"urn:riv:clinicalprocess:healthcond:certificate:RegisterCertificateResponder:3\\\" xmlns:ns3=\\\"urn:riv:clinicalprocess:healthcond:certificate:types:3\\\">        <version>3.0</version>        <signeringstidpunkt>{-6h}</signeringstidpunkt>        <skickatTidpunkt>{-5h}</skickatTidpunkt>        <patient>          <person-id>            <ns3:root>1.2.752.129.2.1.3.1</ns3:root>            <ns3:extension>191212121212</ns3:extension>          </person-id>          <fornamn>Tolvan</fornamn>          <efternamn>Tolvansson</efternamn>          <postadress>Svensson, Storgatan 1, PL 1234</postadress>          <postnummer>12345</postnummer>          <postort>Sm&#xE5;m&#xE5;la</postort>        </patient>     </ns2:RegisterCertificate>\"\n"
            + "     }"
            + "  }";

        JsonNode data = mapper.readTree(jsonString);

        JsonNode result = TestDataTransformer.transformIntyg(data);

        String json = result.toString();

        fail();
    }

    @Test
    public void parseRelativeDate() {
        final LocalDateTime now = LocalDateTime.now();
        int minuteValue = now.getMinute();

        String second = "{-60s}";
        String secondRes = TestDataTransformer.parseRelativeDate(second);
        assertEquals((minuteValue - 1 < 0 ? minuteValue + 60 - 1 : minuteValue - 1),
            Integer.parseInt(secondRes.substring(14, 16)));

        String second2 = "{+120s}";
        String second2Res = TestDataTransformer.parseRelativeDate(second2);
        assertEquals((minuteValue + 2 > 59 ? minuteValue + 2 - 60 : minuteValue + 2),
            Integer.parseInt(second2Res.substring(14, 16)));

        String minute = "{-30m}";
        String minuteRes = TestDataTransformer.parseRelativeDate(minute);
        assertEquals((minuteValue - 30 < 0 ? minuteValue + 60 - 30 : minuteValue - 30),
            Integer.parseInt(minuteRes.substring(14, 16)));

        String minute2 = "{+45m}";
        String minute2Res = TestDataTransformer.parseRelativeDate(minute2);
        assertEquals((minuteValue + 45 > 59 ? minuteValue + 45 - 60 : minuteValue + 45),
            Integer.parseInt(minute2Res.substring(14, 16)));

        int hourValue = now.getHour();
        String hour = "{-12h}";
        String hourRes = TestDataTransformer.parseRelativeDate(hour);
        assertEquals((hourValue - 12 < 1 ? hourValue + 24 - 12 : hourValue - 12),
            Integer.parseInt(hourRes.substring(11, 13)));

        String hour2 = "{+2h}";
        String hour2Res = TestDataTransformer.parseRelativeDate(hour2);
        assertEquals((hourValue + 2 > 24 ? hourValue + 2 - 24 : hourValue + 2),
            Integer.parseInt(hour2Res.substring(11, 13)));

        String year = "{-2Y}";
        String yearRes = TestDataTransformer.parseRelativeDate(year);
        assertEquals(Integer.toString(now.getYear() - 2), yearRes.substring(0, 4));

        String year1 = "{+1Y}";
        String year1Res = TestDataTransformer.parseRelativeDate(year1);
        assertEquals(Integer.toString(now.getYear() + 1), year1Res.substring(0, 4));

        int monthValue = now.getMonthValue();
        String month = "{-3M}";
        String monthRes = TestDataTransformer.parseRelativeDate(month);
        assertEquals((monthValue <= 3 ? monthValue + 12 - 3 : monthValue - 3), Integer.parseInt(monthRes.substring(5, 7)));

        String month1 = "{+2M}";
        String month1Res = TestDataTransformer.parseRelativeDate(month1);
        assertEquals((monthValue + 2 > 12 ? monthValue + 2 - 12 : monthValue + 2),
            Integer.parseInt(month1Res.substring(5, 7)));

        int dayOfYear = now.getDayOfYear();
        int daysPerYear = (Year.isLeap(now.getYear()) ? 366 : 365);
        String day = "{-1W}";
        String dayRes = TestDataTransformer.parseRelativeDate(day);
        LocalDateTime parsedDate = LocalDateTime.parse(dayRes);
        assertEquals((dayOfYear - 7 < 1 ? dayOfYear + daysPerYear - 7 : dayOfYear - 7), parsedDate.getDayOfYear());

        String day2 = "{+2W}";
        String day2Res = TestDataTransformer.parseRelativeDate(day2);
        parsedDate = LocalDateTime.parse(day2Res);
        assertEquals((dayOfYear + 14 > daysPerYear ? dayOfYear + 14 - daysPerYear : dayOfYear + 14), parsedDate.getDayOfYear());

        String day3 = "{-22D}";
        String day3Res = TestDataTransformer.parseRelativeDate(day3);
        parsedDate = LocalDateTime.parse(day3Res);
        assertEquals((dayOfYear - 22 < 1 ? dayOfYear + daysPerYear - 22 : dayOfYear - 22), parsedDate.getDayOfYear());

        String day4 = "{+300D}";
        String day4Res = TestDataTransformer.parseRelativeDate(day4);
        parsedDate = LocalDateTime.parse(day4Res);
        assertEquals((dayOfYear + 300 > daysPerYear ? dayOfYear + 300 - daysPerYear : dayOfYear + 300), parsedDate.getDayOfYear());

        String date1 = "{-1Y+2M-1W+2D-4h30m}";
        String date1Res = TestDataTransformer.parseRelativeDate(date1);
        parsedDate = LocalDateTime.parse(date1Res);
        LocalDateTime modifiedDate = now.minusYears(1).plusMonths(2).minusWeeks(1).plusDays(2).minusHours(4).minusMinutes(30).truncatedTo(
            ChronoUnit.SECONDS);
        assertEquals(modifiedDate, parsedDate);

        String date2 = "{+1Y-2M+1W-2D+4h30m}";
        String date2Res = TestDataTransformer.parseRelativeDate(date2);
        parsedDate = LocalDateTime.parse(date2Res);
        modifiedDate = now.plusYears(1).minusMonths(2).plusWeeks(1).minusDays(2).plusHours(4).plusMinutes(30).truncatedTo(
            ChronoUnit.SECONDS);
        assertEquals(modifiedDate, parsedDate);
    }
}