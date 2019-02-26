dependencies {
    val slf4jVersion: String by rootProject.extra
    val springVersion: String by rootProject.extra
    val jacksonVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra

    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.springframework:spring-context:$springVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
}
