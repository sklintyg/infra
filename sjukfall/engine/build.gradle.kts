import se.inera.intyg.infra.build.Properties

dependencies {
    compile("org.apache.commons:commons-lang3:${Properties.commonsLangVersion}")
    compile("ch.qos.logback:logback-classic:${Properties.logbackVersion}")
    compile("org.springframework:spring-context:${Properties.springVersion}")
}
