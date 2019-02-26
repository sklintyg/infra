dependencies {

    val slf4jVersion: String by rootProject.extra
    val springVersion: String by rootProject.extra
    val springSecurityVersion: String by rootProject.extra
    val servletApiVersion: String by rootProject.extra
    val guavaVersion: String by rootProject.extra
    val hamcrestVersion: String by rootProject.extra
    val mockitoVersion: String by rootProject.extra

    compile("org.slf4j:slf4j-api:$slf4jVersion")
    compile("org.springframework:spring-web:$springVersion")
    compile("org.springframework.security:spring-security-web:$springSecurityVersion")

    compile("javax.servlet:servlet-api:$servletApiVersion")
    compile("com.google.guava:guava:$guavaVersion")

    testCompile("org.hamcrest:hamcrest-all:$hamcrestVersion")
    testCompile("org.mockito:mockito-core:$mockitoVersion")
}
