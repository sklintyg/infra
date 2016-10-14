def call(gradleCommand) {
    util.run {
        withEnv(util.javaEnv()) {
            sh "./gradlew " + gradleCommand
        }
    }
}
