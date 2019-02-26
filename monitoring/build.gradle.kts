import se.inera.intyg.infra.build.Properties

sourceSets {
    test {
        compileClasspath += configurations.compileOnly
        runtimeClasspath += configurations.compileOnly
    }
}

dependencies {

    compile("io.prometheus:simpleclient:${Properties.prometheusClientVersion}")
    compile("io.prometheus:simpleclient_common:${Properties.prometheusClientVersion}")
    compile("io.prometheus:simpleclient_hotspot:${Properties.prometheusClientVersion}")
    compile("io.prometheus:simpleclient_servlet:${Properties.prometheusClientVersion}")
    compile("org.aspectj:aspectjweaver:${Properties.aspectjWeaverVersion}")
    compile("com.google.guava:guava:${Properties.guavaVersion}")

    compileOnly(project(":security-common"))
    compileOnly("org.springframework:spring-webmvc:${Properties.springVersion}")
    compileOnly("org.springframework:spring-context:${Properties.springVersion}")
    compileOnly("org.springframework:spring-core:${Properties.springVersion}")
    compileOnly("org.springframework.security:spring-security-web:${Properties.springSecurityVersion}")
    compileOnly("org.slf4j:slf4j-api:${Properties.slf4jVersion}")
    compileOnly("ch.qos.logback:logback-classic:${Properties.logbackVersion}")
    compileOnly("javax.servlet:javax.servlet-api:${Properties.servletApiTestVersion}")
    compileOnly("commons-io:commons-io:${Properties.commonsIoVersion}")

    testCompile("org.aspectj:aspectjrt:${Properties.aspectjWeaverVersion}")
}
