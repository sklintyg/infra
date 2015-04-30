package se.inera.certificate.tools.anonymisering

import groovy.json.*
import groovy.json.internal.LazyMap

class AnonymiseraJson {
    
    AnonymiseraHsaId anonymiseraHsaId;
    AnonymiseraDatum anonymiseraDatum;
    AnonymiseraPersonId anonymiseraPersonId;
    
    static {
        LazyMap.metaClass.anonymize = {key->
            def value = delegate[key]
            if (value) {
                if (value instanceof List) {
                    delegate[key] = value.collect {AnonymizeString.anonymize(it)}
                } else {
                    delegate[key] = AnonymizeString.anonymize(value)
                }
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
        intyg.patient.id.extension = personId ?: anonymiseraPersonId.anonymisera(intyg.patient.id.extension)
        intyg.patient.anonymize('fornamn')
        intyg.patient.anonymize('efternamn')
        intyg.patient.anonymize('fullstandigtNamn')
        intyg.skapadAv.id.extension = anonymiseraHsaId.anonymisera(intyg.skapadAv.id.extension)
        intyg.skapadAv.anonymize('namn')
        intyg.skapadAv.anonymize('forskrivarkod')
        intyg.patient?.arbetsuppgifter?.each { it.anonymize('typAvArbetsuppgift') }
        intyg.anonymize('kommentarer')
        intyg.aktiviteter?.each { it.anonymize('beskrivning') }
        intyg.observationer?.each {
            it.anonymize('beskrivning')
            it.prognoser?.each {prognos -> prognos.anonymize('beskrivning')} 
        }
        intyg.vardkontakter?.each {
            it.vardkontaktstid.from = anonymiseraDatum.anonymiseraDatum(it.vardkontaktstid.from)
            it.vardkontaktstid.tom = it.vardkontaktstid.from
        }
        intyg.referenser?.each {
            it.datum = anonymiseraDatum.anonymiseraDatum(it.datum)
        }
    }
    
}
