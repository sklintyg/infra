import se.inera.intyg.infra.build.Properties

plugins {
  id("se.inera.intyg.plugin.common").version("2.0.3")
  id("org.jetbrains.kotlin.jvm").version("1.3.21")
  maven
}

group = "se.inera.intyg.infra"
version = System.getenv("buildVersion") ?: "0-SNAPSHOT"

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

subprojects {

  apply(plugin = "se.inera.intyg.plugin.common")
  apply(plugin = "org.gradle.maven")
  apply(plugin = "org.jetbrains.kotlin.jvm")

  dependencies {
    "implementation"(kotlin("stdlib-jdk8"))
    testCompile("junit:junit:4.+")
    testCompile("org.springframework:spring-test:${Properties.springVersion}")
    testCompile("org.mockito:mockito-core:${Properties.mockitoVersion}")
  }
}
