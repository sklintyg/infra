package se.inera.certificate.tools.anonymisering

class AnonymizeString {
    
    static String anonymize(String s) {
        s.replaceAll('[^\'"()\\{\\}\\[\\]\\s0-9]','x').replaceAll('[0-9]','9')
    }
}
