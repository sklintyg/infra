import se.inera.intyg.infra.build.Properties

dependencies {
    compile("org.springframework.data:spring-data-redis:${Properties.springDataRedisVersion}")
    compile("redis.clients:jedis:2.9.0")
    compile("org.springframework:spring-core:${Properties.springVersion}")
    compile("com.esotericsoftware:kryo:4.0.1")
    compile("com.google.guava:guava:${Properties.guavaVersion}")
    compile("it.ozimov:embedded-redis:0.7.2")

    runtime("org.springframework:spring-context-support:${Properties.springVersion}")

    testImplementation("org.springframework:spring-test:${Properties.springVersion}")

    testRuntime("ch.qos.logback:logback-classic:${Properties.logbackVersion}")
}
