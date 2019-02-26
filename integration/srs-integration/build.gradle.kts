dependencies {

  val srsSchemasVersion: String by rootProject.extra
  val schemasContractVersion: String by rootProject.extra
  val cxfVersion: String by rootProject.extra
  val logbackVersion: String by rootProject.extra
  val springVersion: String by rootProject.extra
  val hamcrestVersion: String by rootProject.extra

  compile(project(":security-common"))
  compile(project(":hsa-integration"))

  compile("se.inera.intyg.clinicalprocess.healthcond.srs:intyg-clinicalprocess-healthcond-srs-schemas:$srsSchemasVersion")
  compile("se.inera.intyg.schemas:schemas-contract:$schemasContractVersion")
  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:$cxfVersion")

  runtime("org.springframework:spring-context-support:$springVersion")

  testCompile("org.springframework:spring-test:$springVersion")
  testCompile("org.hamcrest:hamcrest-all:$hamcrestVersion")
}
