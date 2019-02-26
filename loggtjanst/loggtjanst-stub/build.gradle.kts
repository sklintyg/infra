dependencies {

  val springDataRedisVersion: String by rootProject.extra
  val rivtaEhrLogstoreSchemasVersion: String by rootProject.extra
  val jacksonVersion: String by rootProject.extra
  val springVersion: String by rootProject.extra
  val cxfVersion: String by rootProject.extra

  compile("org.springframework.data:spring-data-redis:$springDataRedisVersion")
  compile("se.riv.ehr.logstore:ehr-logstore-schemas:$rivtaEhrLogstoreSchemasVersion")

  compile("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
  compile("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:$jacksonVersion")
  compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
  compile("org.springframework:spring-context:$springVersion")
  compile("org.apache.cxf:cxf-rt-frontend-jaxrs:$cxfVersion")
}
