import se.inera.intyg.infra.build.Properties

dependencies {

  compile(project(":common-redis-cache-core"))

  compile("se.riv.infrastructure.directory.authorizationmanagement:infrastructure-directory-authorizationmanagement-schemas:${Properties.rivtaHsaAuthorizationmanagementSchemasVersion}")
  compile("se.riv.infrastructure.directory.employee:infrastructure-directory-employee-schemas:${Properties.rivtaHsaEmployeeSchemasVersion}")
  compile("se.riv.infrastructure.directory.organization:infrastructure-directory-organization-schemas:${Properties.rivtaDirectoryOrganizationSchemasVersion}")

  compile("org.springframework.security:spring-security-web:${Properties.springSecurityVersion}")

  compile("org.apache.commons:commons-lang3:${Properties.commonsLangVersion}")
  compile("ch.qos.logback:logback-classic:${Properties.logbackVersion}")
  compile("com.google.guava:guava:${Properties.guavaVersion}")

  compile("com.fasterxml.jackson.core:jackson-databind:${Properties.jacksonVersion}")
  compile("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:${Properties.jacksonVersion}")
  compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Properties.jacksonVersion}")

  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:${Properties.cxfVersion}")

  testCompile("org.awaitility:awaitility:${Properties.awaitilityVersion}")
  testCompile("it.ozimov:embedded-redis:0.7.2")
}
