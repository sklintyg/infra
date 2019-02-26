dependencies {

    val slf4jVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    val springVersion: String by rootProject.extra
    val jacksonVersion: String by rootProject.extra
    val commonsLangVersion: String by rootProject.extra

    compile("org.slf4j:slf4j-api:$slf4jVersion")
    compile("ch.qos.logback:logback-classic:$logbackVersion")
    compile("org.springframework:spring-context:$springVersion")
    compile("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    compile("org.apache.commons:commons-lang3:$commonsLangVersion")
}
