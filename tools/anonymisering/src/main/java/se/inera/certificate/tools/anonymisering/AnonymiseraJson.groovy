package se.inera.certificate.tools.anonymisering

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
