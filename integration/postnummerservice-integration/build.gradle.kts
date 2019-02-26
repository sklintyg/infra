import se.inera.intyg.infra.build.Properties

dependencies {

    compile("org.slf4j:slf4j-api:${Properties.slf4jVersion}")
    compile("ch.qos.logback:logback-classic:${Properties.logbackVersion}")
    compile("org.springframework:spring-context:${Properties.springVersion}")
    compile("com.fasterxml.jackson.core:jackson-databind:${Properties.jacksonVersion}")
    compile("org.apache.commons:commons-lang3:${Properties.commonsLangVersion}")
}
