import se.inera.intyg.TagReleaseTask
import se.inera.intyg.VersionPropertyFileTask
import se.inera.intyg.infra.build.Properties

plugins {
  id("se.inera.intyg.plugin.common").version("2.0.3")
  id("org.jetbrains.kotlin.jvm").version("1.3.21")
  maven
}

allprojects {

  group = "se.inera.intyg.infra"
  version = System.getenv("buildVersion") ?: "0-SNAPSHOT"

  repositories {
    mavenLocal()
    maven("https://build-inera.nordicmedtest.se/nexus/repository/releases/")
    maven("http://repo.maven.apache.org/maven2")
  }

  tasks {
    withType<JavaCompile> {
      sourceCompatibility = "1.8"
      targetCompatibility = "1.8"
      options.encoding = "UTF-8"
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
  
  
  tasks {
    withType<VersionPropertyFileTask>()

    "uploadArchives"(Upload::class) {

      repositories {
        withConvention(MavenRepositoryHandlerConvention::class) {
          mavenDeployer {
            withGroovyBuilder {
              "repository"("url" to uri("https://build-inera.nordicmedtest.se/nexus/repository/releases/")) {
                "authentication"("userName" to System.getenv("nexusUsername"), "password" to System.getenv("nexusPassword"))
              }
            }
          }
        }
      }
    }
  }
  
}

tasks {
  withType<TagReleaseTask>()
}


