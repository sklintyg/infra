def javaEnv() {
    def javaHome = tool 'JDK8u66'
    ["PATH=${env.PATH}:${javaHome}/bin", "JAVA_HOME=${javaHome}"]
}

def call(gradleCommand) {
    util.safeExecute {
        withEnv(javaEnv()) {
            sh gradleCommand
        }
    }
}
