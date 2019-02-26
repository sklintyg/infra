dependencies {

  val jacksonVersion: String by rootProject.extra

  compile("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
}
