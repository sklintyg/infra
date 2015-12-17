/*
 * Copyright (C) 2015 Inera AB (http://www.inera.se)
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

package se.inera.intyg.common.tools.anonymisering

import org.apache.commons.io.FileUtils
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.core.io.ClassPathResource
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class AnonymiseraJsonTest {

    AnonymiseraHsaId anonymiseraHsaId = [anonymisera:{"SE1010"}] as AnonymiseraHsaId
    AnonymiseraDatum anonymiseraDatum = new AnonymiseraDatum()
    AnonymiseraJson anonymiseraJson = new AnonymiseraJson(anonymiseraHsaId, anonymiseraDatum)

    AnonymiseraJsonTest() {
        anonymiseraDatum.random = [nextInt: {(AnonymiseraDatum.DATE_RANGE/2)+1}] as Random
    }

    @Test
    void testaAnonymiseringAvMaximaltIntyg() {
        String json = FileUtils.readFileToString(new ClassPathResource("/fk7263_L_template.json").getFile(), "UTF-8")
        String expected = FileUtils.readFileToString(new ClassPathResource("/fk7263_L_anonymized.json").getFile(), "UTF-8")
        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-1010")
        JSONAssert.assertEquals(expected, actual, true);
    }
    @Test
    void testaAnonymiseringAvTSIntyg() {
        String json = buildJsonIntyg("/fk7263_L_template.json") { result ->
            result.funktionsnedsattning = [funktionsnedsattning: false, beskrivning: 'en liten text', ]
        }

        String expected = buildJsonIntyg("/fk7263_L_anonymized.json") { result ->
            result.funktionsnedsattning = [funktionsnedsattning: false, beskrivning: 'xx xxxxx xxxx', ]
        }

        String actual = anonymiseraJson.anonymiseraIntygsJson(json, "10101010-1010")

        JSONAssert.assertEquals(expected, actual, true);
    }

    def buildJsonIntyg(def file, def clos = null) {
        def intygString = getClass().getResource( file ).getText( 'UTF-8' )
        def result = new JsonSlurper().parseText intygString

        if(clos) clos.call result

        return new JsonBuilder(result).toString()
    }
}
