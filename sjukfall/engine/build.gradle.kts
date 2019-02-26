dependencies {
    val commonsLangVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    val springVersion: String by rootProject.extra

    compile("org.apache.commons:commons-lang3:${commonsLangVersion}")
    compile("ch.qos.logback:logback-classic:${logbackVersion}")
    compile("org.springframework:spring-context:${springVersion}")
}
