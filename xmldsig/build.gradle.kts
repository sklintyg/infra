dependencies {

    val slf4jVersion: String by rootProject.extra
    val intygClinicalprocessSchemasVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    val springVersion: String by rootProject.extra
    val jacksonVersion: String by rootProject.extra
    val commonsIoVersion: String by rootProject.extra
    val xmlSecVersion: String by rootProject.extra
    val saxonVersion: String by rootProject.extra

    compile("se.inera.intyg.clinicalprocess.healthcond.certificate:intyg-clinicalprocess-healthcond-certificate-schemas:$intygClinicalprocessSchemasVersion")

    compile("org.slf4j:slf4j-api:$slf4jVersion")
    compile("ch.qos.logback:logback-classic:$logbackVersion")
    compile("org.springframework:spring-context:$springVersion")
    compile("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    compile("commons-io:commons-io:$commonsIoVersion")
    compile("org.apache.santuario:xmlsec:$xmlSecVersion")
    compile("net.sf.saxon:Saxon-HE:$saxonVersion")
}
