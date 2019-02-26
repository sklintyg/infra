import se.inera.intyg.infra.build.Properties

dependencies {
    compile("se.inera.intyg.clinicalprocess.healthcond.certificate:intyg-clinicalprocess-healthcond-certificate-schemas:${Properties.intygClinicalprocessSchemasVersion}")

    compile("org.slf4j:slf4j-api:${Properties.slf4jVersion}")
    compile("ch.qos.logback:logback-classic:${Properties.logbackVersion}")
    compile("org.springframework:spring-context:${Properties.springVersion}")
    compile("com.fasterxml.jackson.core:jackson-databind:${Properties.jacksonVersion}")
    compile("commons-io:commons-io:${Properties.commonsIoVersion}")
    compile("org.apache.santuario:xmlsec:${Properties.xmlSecVersion}")
    compile("net.sf.saxon:Saxon-HE:${Properties.saxonVersion}")
}
