sourceSets {
    test {
        compileClasspath += configurations.compileOnly
        runtimeClasspath += configurations.compileOnly
    }
}

dependencies {

    val prometheusClientVersion: String by rootProject.extra
    val aspectjWeaverVersion: String by rootProject.extra
    val guavaVersion: String by rootProject.extra
    val springVersion: String by rootProject.extra
    val springSecurityVersion: String by rootProject.extra
    val slf4jVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    val servletApiTestVersion: String by rootProject.extra
    val commonsIoVersion: String by rootProject.extra

    compile("io.prometheus:simpleclient:${prometheusClientVersion}")
    compile("io.prometheus:simpleclient_common:${prometheusClientVersion}")
    compile("io.prometheus:simpleclient_hotspot:${prometheusClientVersion}")
    compile("io.prometheus:simpleclient_servlet:${prometheusClientVersion}")
    compile("org.aspectj:aspectjweaver:${aspectjWeaverVersion}")
    compile("com.google.guava:guava:${guavaVersion}")

    compileOnly(project(":security-common"))
    compileOnly("org.springframework:spring-webmvc:${springVersion}")
    compileOnly("org.springframework:spring-context:${springVersion}")
    compileOnly("org.springframework:spring-core:${springVersion}")
    compileOnly("org.springframework.security:spring-security-web:${springSecurityVersion}")
    compileOnly("org.slf4j:slf4j-api:${slf4jVersion}")
    compileOnly("ch.qos.logback:logback-classic:${logbackVersion}")
    compileOnly("javax.servlet:javax.servlet-api:${servletApiTestVersion}")
    compileOnly("commons-io:commons-io:${commonsIoVersion}")

    testCompile("org.aspectj:aspectjrt:${aspectjWeaverVersion}")
}
