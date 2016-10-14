def javaEnv() {
    def javaHome = tool 'JDK8u66'
    ["PATH=${env.PATH}:${javaHome}/bin", "JAVA_HOME=${javaHome}"]
}

def run(Closure body) {
    try {
        body()
    } catch (e) {
        currentBuild.result = "FAILED"
        notifyFailed()
        throw e
    }
}
