import se.inera.intyg.infra.build.Properties

dependencies {
    compile("org.slf4j:slf4j-api:${Properties.slf4jVersion}")
    compile("org.springframework:spring-web:${Properties.springVersion}")
    compile("org.springframework.security:spring-security-web:${Properties.springSecurityVersion}")

    compile("javax.servlet:servlet-api:${Properties.servletApiVersion}")
    compile("com.google.guava:guava:${Properties.guavaVersion}")

    testCompile("org.hamcrest:hamcrest-all:${Properties.hamcrestVersion}")
    testCompile("org.mockito:mockito-core:${Properties.mockitoVersion}")
}
