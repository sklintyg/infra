pluginManagement {
  repositories {
    maven("https://build-inera.nordicmedtest.se/nexus/repository/releases/")
    gradlePluginPortal()
  }
}

rootProject.name = "se.inera.intyg.infra"

include(":common-redis-cache-core")
include(":dynamiclink")
include(":grp-stub")
include(":nias-stub")
include(":hsa-integration")
include(":pu-integration")
include(":srs-integration")
include(":postnummerservice-integration")
include(":log-messages")
include(":loggtjanst-stub")
include(":security-common")
include(":security-authorities")
include(":security-filter")
include(":security-siths")
include(":sjukfall-engine")
include(":xmldsig")
include(":monitoring")

fun getProjectDirName(project: String): String {
  return when(project) {
       "common-redis-cache-core" ->"$rootDir/redis-cache/core"
       "dynamiclink" ->"$rootDir/dynamiclink"
       "grp-stub" ->"$rootDir/integration/grp-stub"
       "nias-stub" ->"$rootDir/integration/nias-stub"
       "hsa-integration" ->"$rootDir/integration/hsa-integration"
       "pu-integration" ->"$rootDir/integration/pu-integration"
       "srs-integration" ->"$rootDir/integration/srs-integration"
       "postnummerservice-integration" ->"$rootDir/integration/postnummerservice-integration"
       "log-messages" ->"$rootDir/loggtjanst/log-messages"
       "loggtjanst-stub" ->"$rootDir/loggtjanst/loggtjanst-stub"
       "security-common" ->"$rootDir/security/common"
       "security-authorities" ->"$rootDir/security/authorities"
       "security-filter" ->"$rootDir/security/filter"
       "security-siths" ->"$rootDir/security/siths"
       "sjukfall-engine" ->"$rootDir/sjukfall/engine"
       "xmldsig" ->"$rootDir/xmldsig"
       "monitoring" ->"$rootDir/monitoring"
    else -> "unknown"
  }
}

for (project in rootProject.children) {
  val projectName = project.name

  project.projectDir = file(getProjectDirName(projectName))
  project.buildFileName = "build.gradle.kts"

  if (!project.projectDir.isDirectory) {
    throw IllegalArgumentException("Project directory ${project.projectDir} for project ${project.name} does not exist.")
  }

  if (!project.buildFile.isFile) {
    throw IllegalArgumentException("Build file ${project.buildFile} for project ${project.name} does not exist.")
  }
}
