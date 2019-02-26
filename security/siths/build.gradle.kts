import se.inera.intyg.infra.build.Properties

dependencies {
  compile(project(":security-common"))
  compile(project(":security-authorities"))

  compile("org.springframework.security.extensions:spring-security-saml2-core:${Properties.springSecuritySaml2CoreVersion}")

  compileOnly("javax.servlet:servlet-api:${Properties.servletApiVersion}")

  testCompile("javax.servlet:javax.servlet-api:${Properties.servletApiTestVersion}")
}
