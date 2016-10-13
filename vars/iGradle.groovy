def javaEnv() {
    def javaHome = tool 'JDK8u66'
    ["PATH=${env.PATH}:${javaHome}/bin", "JAVA_HOME=${javaHome}"]
}

def call(gradleCommand) {
    try {
        wrap([$class: 'Xvfb']) {
            withEnv(javaEnv()) {
                sh gradleCommand
            }
        }
    } catch (e) {
        currentBuild.result = "FAILED"
        notifyFailed()
        throw e
    }
}
