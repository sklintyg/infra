package se.inera.intyg.common.tools.anonymisering

import groovy.xml.StreamingMarkupBuilder

class AnonymiseraXml {

    AnonymiseraPersonId anonymiseraPersonId;
    AnonymiseraHsaId anonymiseraHsaId;
    AnonymiseraDatum anonymiseraDatum;

    AnonymiseraXml(AnonymiseraPersonId anonymiseraPersonId, AnonymiseraHsaId anonymiseraHsaId,
        AnonymiseraDatum anonymiseraDatum) {
        this.anonymiseraPersonId = anonymiseraPersonId
        this.anonymiseraHsaId = anonymiseraHsaId
        this.anonymiseraDatum = anonymiseraDatum
    }

    String anonymiseraIntygsXml(String s) {
        anonymiseraIntygsXml(s, null)
    }

    String anonymiseraIntygsXml(String s, String personId) {
        def slurper = new XmlSlurper()
        slurper.keepIgnorableWhitespace = true
        def intyg = slurper.parseText(s)
        intyg.declareNamespace(ns1: 'urn:riv:insuranceprocess:healthreporting:mu7263:3',
                               ns2: 'urn:riv:insuranceprocess:healthreporting:2',
                               ns3: 'urn:riv:insuranceprocess:healthreporting:RegisterMedicalCertificateResponder:3')
        anonymizeXml(intyg, personId)
        def outputBuilder = new StreamingMarkupBuilder()
        outputBuilder.encoding = 'UTF-8'
        return (s.startsWith('<?xml') ? '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>' : "") + outputBuilder.bind{  mkp.yield intyg }
    }

    private void anonymizeXml(def intyg, String personId) {
        intyg.'ns3:lakarutlatande'.'ns1:patient'.'ns2:person-id'.@extension = personId ?: anonymiseraPersonId.anonymisera((String) intyg.'ns3:lakarutlatande'.'ns1:patient'.'ns2:person-id'.@extension)
        anonymizeNode intyg.'ns3:lakarutlatande'.'ns1:patient'.'ns2:fullstandigtNamn'
        String personalId = intyg.'ns3:lakarutlatande'.'ns1:skapadAvHosPersonal'.'ns2:personal-id'.@extension
        intyg.'ns3:lakarutlatande'.'ns1:skapadAvHosPersonal'.'ns2:personal-id'.@extension = anonymiseraHsaId.anonymisera(personalId)
        anonymizeNode intyg.'ns3:lakarutlatande'.'ns1:skapadAvHosPersonal'.'ns2:fullstandigtNamn'
        anonymizeNode intyg.'ns3:lakarutlatande'.'ns1:skapadAvHosPersonal'.'ns2:forskrivarkod'
        anonymizeNode intyg.'ns3:lakarutlatande'?.'ns1:kommentar'
        intyg.'ns3:lakarutlatande'?.'ns1:aktivitet'?.each {
            anonymizeNode it.'ns1:beskrivning'
        }
        anonymizeNode intyg.'ns3:lakarutlatande'?.'ns1:bedomtTillstand'?.'ns1:beskrivning'
        anonymizeNode intyg.'ns3:lakarutlatande'?.'ns1:medicinsktTillstand'?.'ns1:beskrivning'
        intyg.'ns3:lakarutlatande'?.'ns1:funktionstillstand'?.each {
            anonymizeNode it.'ns1:beskrivning'
            anonymizeNode it.'ns1:arbetsformaga'?.'ns1:motivering'
            anonymizeNode it.'ns1:arbetsformaga'?.'ns1:arbetsuppgift'?.'ns1:typAvArbetsuppgift'
        }
        intyg.'ns3:lakarutlatande'?.'ns1:vardkontakt'?.each {
            anonymizeDateNode it.'ns1:vardkontaktstid'
        }
        intyg.'ns3:lakarutlatande'?.'ns1:referens'?.each {
            anonymizeDateNode it.'ns1:datum'
        }
    }

    private void anonymizeNode(def node) {
        node?.replaceBody AnonymizeString.anonymize(node.toString())
    }

    private void anonymizeDateNode(def node) {
        node?.replaceBody anonymiseraDatum.anonymiseraDatum(node.toString())
    }
}
