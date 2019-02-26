dependencies {

  val springSecuritySaml2CoreVersion: String by rootProject.extra
  val servletApiVersion: String by rootProject.extra
  val servletApiTestVersion: String by rootProject.extra

  compile(project(":security-common"))
  compile(project(":security-authorities"))

  compile("org.springframework.security.extensions:spring-security-saml2-core:$springSecuritySaml2CoreVersion")

  compileOnly("javax.servlet:servlet-api:$servletApiVersion")

  testCompile("javax.servlet:javax.servlet-api:$servletApiTestVersion")
}
