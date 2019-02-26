dependencies {

  val rivtaHsaAuthorizationmanagementSchemasVersion: String by rootProject.extra
  val rivtaHsaEmployeeSchemasVersion: String by rootProject.extra
  val rivtaDirectoryOrganizationSchemasVersion: String by rootProject.extra
  val springSecurityVersion: String by rootProject.extra
  val commonsLangVersion: String by rootProject.extra
  val logbackVersion: String by rootProject.extra
  val guavaVersion: String by rootProject.extra
  val jacksonVersion: String by rootProject.extra
  val cxfVersion: String by rootProject.extra
  val awaitilityVersion: String by rootProject.extra

  compile(project(":common-redis-cache-core"))

  compile("se.riv.infrastructure.directory.authorizationmanagement:infrastructure-directory-authorizationmanagement-schemas:$rivtaHsaAuthorizationmanagementSchemasVersion")
  compile("se.riv.infrastructure.directory.employee:infrastructure-directory-employee-schemas:$rivtaHsaEmployeeSchemasVersion")
  compile("se.riv.infrastructure.directory.organization:infrastructure-directory-organization-schemas:$rivtaDirectoryOrganizationSchemasVersion")

  compile("org.springframework.security:spring-security-web:$springSecurityVersion")

  compile("org.apache.commons:commons-lang3:$commonsLangVersion")
  compile("ch.qos.logback:logback-classic:$logbackVersion")
  compile("com.google.guava:guava:$guavaVersion")

  compile("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
  compile("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:$jacksonVersion")
  compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:$cxfVersion")

  testCompile("org.awaitility:awaitility:$awaitilityVersion")
  testCompile("it.ozimov:embedded-redis:0.7.2")
}
