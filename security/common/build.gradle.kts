import se.inera.intyg.infra.build.Properties

dependencies {
    compile(project(":hsa-integration"))

    compile("com.fasterxml.jackson.core:jackson-annotations:${Properties.jacksonVersion}")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Properties.jacksonDataformatVersion}")

    compile("org.springframework:spring-context:${Properties.springVersion}")
    compile("org.slf4j:slf4j-api:${Properties.slf4jVersion}")

    compileOnly("javax.servlet:servlet-api:${Properties.servletApiVersion}")
}
