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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TestDataTransformer {

    private enum AddSubState { Add, Sub }

    private TestDataTransformer() {
    }

    public static JsonNode transformIntyg(JsonNode intygData) {
        JsonNode data = intygData.deepCopy();

        traverse(data, null, null);
        return data;
    }

    public static void traverse(JsonNode node, String nodeName, JsonNode parent) {
        if (node.isObject()) {
            Iterator<String> fieldNames = node.fieldNames();

            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                JsonNode fieldValue = node.get(fieldName);
                traverse(fieldValue, fieldName, node);
            }
        } else if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode arrayElement = arrayNode.get(i);
                traverse(arrayElement, null, node);
            }
        } else {
            ValueNode valueNode = (ValueNode) node;
            String value = valueNode.asText();
            if (isRelativeDate(value)) {
                value = parseRelativeDate(value);
                if (parent.isObject()) {
                    ObjectNode parentObject = (ObjectNode) parent;
                    parentObject.put(nodeName, value);
                }
            }
            else if (containsRelativeDate(value)) {
                Pattern pat = Pattern.compile("\\{(.*?)}");
                Matcher m = pat.matcher(value);
                StringBuffer sb = new StringBuffer();
                while (m.find()){
                    String s = parseRelativeDate(m.group(1));
                    m.appendReplacement(sb, s);
                }
                m.appendTail(sb);
                value = sb.toString();
                if (parent.isObject()) {
                    ObjectNode parentObject = (ObjectNode) parent;
                    parentObject.put(nodeName, value);
                }
            }
        }
    }

    private static boolean containsRelativeDate(String value) { return value.contains("{") && value.contains("}"); }

    private static boolean isRelativeDate(String value) {
        return value.startsWith("{") && value.endsWith("}");
    }

    public static String parseRelativeDate(String date) {
        LocalDateTime newTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        //date = "{-1W3D+12h}";

        AddSubState asState = AddSubState.Add;
        int quantifier = 1;

        StringBuilder number = new StringBuilder();
        for (int i = 0; i < date.length(); i++) {
            char c = date.charAt(i);

            if (Character.isDigit(c)) {
                number.append(c);
                if ((i + 1) < date.length() && Character.isDigit(date.charAt(i + 1))) {
                    continue;
                }

                quantifier = Integer.parseInt(number.toString());
                number = new StringBuilder();
                continue;
            }

            switch (c) {
                case '+':
                    asState = AddSubState.Add;
                    break;
                case '-':
                    asState = AddSubState.Sub;
                    break;
                case 'Y':
                    if (asState == AddSubState.Add) {
                        newTime = newTime.plusYears(quantifier);
                    } else {
                        newTime = newTime.minusYears(quantifier);
                    }
                    //quantifier = 1;
                    break;
                case 'M':
                    if (asState == AddSubState.Add) {
                        newTime = newTime.plusMonths(quantifier);
                    } else {
                        newTime = newTime.minusMonths(quantifier);
                    }
                    break;
                case 'W':
                case 'V':
                    if (asState == AddSubState.Add) {
                        newTime = newTime.plusWeeks(quantifier);
                    } else {
                        newTime = newTime.minusWeeks(quantifier);
                    }
                    break;
                case 'D':
                    if (asState == AddSubState.Add) {
                        newTime = newTime.plusDays(quantifier);
                    } else {
                        newTime = newTime.minusDays(quantifier);
                    }
                    break;
                case 'h':
                    if (asState == AddSubState.Add) {
                        newTime = newTime.plusHours(quantifier);
                    } else {
                        newTime = newTime.minusHours(quantifier);
                    }
                    break;
                case 'm':
                    if (asState == AddSubState.Add) {
                        newTime = newTime.plusMinutes(quantifier);
                    } else {
                        newTime = newTime.minusMinutes(quantifier);
                    }
                    break;
                case 's':
                    if (asState == AddSubState.Add) {
                        newTime = newTime.plusSeconds(quantifier);
                    } else {
                        newTime = newTime.minusSeconds(quantifier);
                    }
                    break;
                default:
                    break;
            }
        }

        return newTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
