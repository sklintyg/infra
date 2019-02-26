dependencies {

    val springDataRedisVersion: String by rootProject.extra
    val springVersion: String by rootProject.extra
    val guavaVersion: String by rootProject.extra
    val logbackVersion: String by rootProject.extra
    
    implementation("org.springframework.data:spring-data-redis:$springDataRedisVersion")
    implementation("redis.clients:jedis:2.9.0")
    implementation("org.springframework:spring-core:$springVersion")
    implementation("com.esotericsoftware:kryo:4.0.1")
    implementation("com.google.guava:guava:$guavaVersion")
    implementation("it.ozimov:embedded-redis:0.7.2")

    runtime("org.springframework:spring-context-support:$springVersion")

    testImplementation("org.springframework:spring-test:$springVersion")

    testRuntime("ch.qos.logback:logback-classic:$logbackVersion")
}
