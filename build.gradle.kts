plugins {
  id("se.inera.intyg.plugin.common").version("2.0.3")
  id("org.jetbrains.kotlin.jvm").version("1.3.21")
  maven
}

group = "se.inera.intyg.infra"
version = System.getenv("buildVersion") ?: "0-SNAPSHOT"

extra.set("funktionstjansterGrpSchemasVersion", "1.0.4")

extra.set("rivtaDirectoryOrganizationSchemasVersion", "1.1.RC5.5")
extra.set("rivtaEhrLogstoreSchemasVersion", "1.2.4")
extra.set("rivtaHsaAuthorizationmanagementSchemasVersion", "1.0.RC4.5")
extra.set("rivtaHsaEmployeeSchemasVersion", "1.0.RC4.5")
extra.set("rivtaResidentMasterSchemasVersion", "1.1.3.5")
extra.set("rivtaGetPersonsPersonSchemasVersion", "3.0")

extra.set("intygClinicalprocessSchemasVersion", "1.0.16")

extra.set("srsSchemasVersion", "0.0.7")

extra.set("schemasContractVersion", "2.1.7")

extra.set("springDataRedisVersion", "1.8.12.RELEASE")
extra.set("commonsIoVersion", "2.4")
extra.set("commonsLangVersion", "3.8.1")
extra.set("commonsCollectionsVersion", "4.2")
extra.set("cxfVersion", "3.1.3")
extra.set("guavaVersion", "14.0.1")
extra.set("hamcrestVersion", "1.3")
extra.set("igniteSpringVersion", "1.8.0")
extra.set("jacksonDataformatVersion", "2.9.7")
extra.set("jacksonVersion", "2.9.7")
extra.set("logbackVersion", "1.2.3")
extra.set("saxonVersion", "9.8.0-10")
extra.set("servletApiVersion", "2.5")
extra.set("slf4jVersion", "1.7.21")
extra.set("springSecuritySaml2CoreVersion", "1.0.3.RELEASE")
extra.set("springSecurityVersion", "4.2.5.RELEASE")
extra.set("springVersion", "4.3.17.RELEASE")
extra.set("xmlSecVersion", "2.1.1")
extra.set("prometheusClientVersion", "0.4.0")
extra.set("aspectjWeaverVersion", "1.8.9")

extra.set("awaitilityVersion", "2.0.0")
extra.set("mockitoVersion", "2.16.0")
extra.set("servletApiTestVersion", "3.1.0")


allprojects {

  repositories {
    mavenLocal()
    maven("https://build-inera.nordicmedtest.se/nexus/repository/releases/")
    maven("http://repo.maven.apache.org/maven2")
  }

  tasks {
    withType<JavaCompile> {
      sourceCompatibility = "1.8"
      targetCompatibility = "1.8"

    }
  }
}

val springVersion: String by extra
val mockitoVersion: String by extra

subprojects {

  apply(plugin = "se.inera.intyg.plugin.common")
  apply(plugin = "org.gradle.maven")
  apply(plugin = "org.jetbrains.kotlin.jvm")

  dependencies {
    "implementation"(kotlin("stdlib-jdk8"))
    testImplementation("junit:junit:4.+")
    testImplementation("org.springframework:spring-test:$springVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
  }
}
