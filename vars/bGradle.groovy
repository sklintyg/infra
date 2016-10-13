def call(gradleCommand) {
    try {
        withEnv(javaEnv()) {
            sh gradleCommand
        }
    } catch (e) {
        notifyFailed()
        throw e
    }
}
