import se.inera.intyg.infra.build.Properties

dependencies {

  compileOnly(project(":xmldsig"))

  compile("com.secmaker.netid.nias:netid-access-server-schemas:1.0.0")

  compile("org.springframework.security:spring-security-web:${Properties.springSecurityVersion}")

  compile("org.apache.commons:commons-lang3:${Properties.commonsLangVersion}")
  compile("ch.qos.logback:logback-classic:${Properties.logbackVersion}")
  compile("com.google.guava:guava:${Properties.guavaVersion}")

  compile("com.fasterxml.jackson.core:jackson-databind:${Properties.jacksonVersion}")
  compile("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:${Properties.jacksonVersion}")
  compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Properties.jacksonVersion}")

  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:${Properties.cxfVersion}")
}
