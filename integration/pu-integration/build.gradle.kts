import se.inera.intyg.infra.build.Properties

dependencies {

  compile(project(":common-redis-cache-core"))

  compile("org.springframework.data:spring-data-redis:${Properties.springDataRedisVersion}")
  compile("se.riv.strategicresourcemanagement.persons.person:strategicresourcemanagement-persons-person-schemas:${Properties.rivtaGetPersonsPersonSchemasVersion}")

  compile("se.inera.intyg.schemas:schemas-contract:${Properties.schemasContractVersion}")
  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:${Properties.cxfVersion}")
  compile("org.apache.commons:commons-lang3:${Properties.commonsLangVersion}")
  compile("org.apache.commons:commons-collections4:${Properties.commonsCollectionsVersion}")
  runtime("org.springframework:spring-context-support:${Properties.springVersion}")

  testCompile("se.riv.population.residentmaster:population-residentmaster-schemas:${Properties.rivtaResidentMasterSchemasVersion}")

  testCompile("org.springframework:spring-test:${Properties.springVersion}")
  testCompile("org.hamcrest:hamcrest-all:${Properties.hamcrestVersion}")
  testCompile("it.ozimov:embedded-redis:0.7.2")
}
