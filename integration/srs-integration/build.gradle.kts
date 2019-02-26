import se.inera.intyg.infra.build.Properties

dependencies {
  compile(project(":security-common"))
  compile(project(":hsa-integration"))

  compile("se.inera.intyg.clinicalprocess.healthcond.srs:intyg-clinicalprocess-healthcond-srs-schemas:${Properties.srsSchemasVersion}")
  compile("se.inera.intyg.schemas:schemas-contract:${Properties.schemasContractVersion}")
  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:${Properties.cxfVersion}")

  runtime("org.springframework:spring-context-support:${Properties.springVersion}")

  testCompile("org.springframework:spring-test:${Properties.springVersion}")
  testCompile("org.hamcrest:hamcrest-all:${Properties.hamcrestVersion}")
}
