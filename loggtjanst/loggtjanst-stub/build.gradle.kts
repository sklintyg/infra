import se.inera.intyg.infra.build.Properties

dependencies {
  compile("org.springframework.data:spring-data-redis:${Properties.springDataRedisVersion}")
  compile("se.riv.ehr.logstore:ehr-logstore-schemas:${Properties.rivtaEhrLogstoreSchemasVersion}")

  compile("com.fasterxml.jackson.core:jackson-databind:${Properties.jacksonVersion}")
  compile("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:${Properties.jacksonVersion}")
  compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${Properties.jacksonVersion}")
  compile("org.springframework:spring-context:${Properties.springVersion}")
  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:${Properties.cxfVersion}")
}
