def javaEnv() {
    def javaHome = tool 'JDK8u66'
    ["PATH=${env.PATH}:${javaHome}/bin", "JAVA_HOME=${javaHome}"]
}

def notifyFailed() {
    emailext (subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
              body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':\n\nCheck console output at ${env.BUILD_URL}""",
              recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']])
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

def waitForServer(String url) {
    timeout(240) {
	waitUntil {
	    def r = sh script: "wget -q ${url} --no-check-certificate -O /dev/null", returnStatus: true
	    return (r == 0);
	}
    }
}
