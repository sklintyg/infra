/*
 * Copyright (C) 2016 Inera AB (http://www.inera.se)
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

import groovy.json.*
import groovy.json.internal.LazyMap

class AnonymiseraJson {

    AnonymiseraHsaId anonymiseraHsaId;
    AnonymiseraDatum anonymiseraDatum;
    AnonymiseraPersonId anonymiseraPersonId;

    static {
        LazyMap.metaClass.anonymize = {key->
            anonymizeField(delegate, key)
        }
    }

    private static anonymizeField(Map delegate, String key) {
            def value = delegate[key]
            if (value != null) {
                if (value instanceof List) {
                    delegate[key] = value.collect {AnonymizeString.anonymize(it)}
                } else if (value instanceof Map) {
                    value.each {k, v ->
                        anonymizeField(value, k)
                    }
                } else if (value instanceof Boolean) {
                    //Do nothing
                }
                else {
                    delegate[key] = AnonymizeString.anonymize(value)
                }
            }
    }

    AnonymiseraJson(AnonymiseraHsaId anonymiseraHsaId, AnonymiseraDatum anonymiseraDatum) {
        this.anonymiseraHsaId = anonymiseraHsaId
        this.anonymiseraDatum = anonymiseraDatum
    }

    AnonymiseraJson(AnonymiseraHsaId anonymiseraHsaId, AnonymiseraDatum anonymiseraDatum, AnonymiseraPersonId anonymiseraPersonId) {
        this.anonymiseraHsaId = anonymiseraHsaId
        this.anonymiseraDatum = anonymiseraDatum
        this.anonymiseraPersonId = anonymiseraPersonId
    }

    String anonymiseraIntygsJson(String s) {
        anonymiseraIntygsJson(s, null)
    }

    String anonymiseraIntygsJson(String s, String personId) {
        def intyg = new JsonSlurper().parseText(s)
        anonymizeJson(intyg, personId)
        JsonBuilder builder = new JsonBuilder( intyg )
        return builder.toString()
    }

    void anonymizeJson(def intyg, String personId) {
        intyg.grundData.patient.personId = personId ?: anonymiseraPersonId.anonymisera(intyg.grundData.patient.personId)
        intyg.grundData.patient.anonymize('fornamn')
        intyg.grundData.patient.anonymize('efternamn')
        intyg.grundData.patient.anonymize('fullstandigtNamn')
        intyg.grundData.skapadAv.personId = anonymiseraHsaId.anonymisera(intyg.grundData.skapadAv.personId)
        intyg.grundData.skapadAv.anonymize('fullstandigtNamn')
        intyg.grundData.skapadAv.anonymize('forskrivarKod')
        if (intyg.undersokningAvPatienten) intyg.undersokningAvPatienten = anonymiseraDatum.anonymiseraDatum(intyg.undersokningAvPatienten)
        if (intyg.telefonkontaktMedPatienten) intyg.telefonkontaktMedPatienten = anonymiseraDatum.anonymiseraDatum(intyg.telefonkontaktMedPatienten)
        if (intyg.journaluppgifter) intyg.journaluppgifter = anonymiseraDatum.anonymiseraDatum(intyg.journaluppgifter)
        if (intyg.annanReferens) intyg.annanReferens = anonymiseraDatum.anonymiseraDatum(intyg.annanReferens)
        intyg.anonymize('kommentar')
        intyg.anonymize('rekommendationOvrigt')
        intyg.anonymize('atgardInomSjukvarden')
        intyg.anonymize('annanAtgard')
        intyg.anonymize('sjukdomsforlopp')
        intyg.anonymize('nuvarandeArbetsuppgifter')
        intyg.anonymize('funktionsnedsattning')
        intyg.anonymize('aktivitetsbegransning')
        intyg.anonymize('arbetsformagaPrognos')
        intyg.anonymize('namnfortydligandeOchAdress')
    }

}
