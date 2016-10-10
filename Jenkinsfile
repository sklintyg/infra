#!groovy

def javaEnv() {
    def javaHome = tool 'JDK8u66'
    ["PATH=${env.PATH}:${javaHome}/bin", "JAVA_HOME=${javaHome}"]
}

stage('checkout') {
    node {
        try {
            checkout scm
        } catch (e) {
            currentBuild.result = "FAILED"
            notifyFailed()
            throw e
        }
    }
}

stage('build') {
    node {
        try {
            withEnv(javaEnv()) {
                sh './gradlew --refresh-dependencies clean build sonarqube -PcodeQuality'
            }
        } catch (e) {
            currentBuild.result = "FAILED"
            notifyFailed()
            throw e
        }
    }
}

stage('tag and upload') {
    node {
        try {
            withEnv(javaEnv()) {
                sh './gradlew uploadArchives tagRelease -DnexusUsername=$NEXUS_USERNAME -DnexusPassword=$NEXUS_PASSWORD \
                    -DgithubUser=$GITHUB_USERNAME -DgithubPassword=$GITHUB_PASSWORD'
            }
        } catch (e) {
            currentBuild.result = "FAILED"
            notifyFailed()
            throw e
        }
    }
}

stage ('start next job') {
    build job: 'intyg-intygstyper-pipeline', parameters: [[$class: 'StringParameterValue', name: 'GIT_BRANCH', value: env.$GIT_BRANCH]]
}

def notifyFailed() {
    emailext (subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
              body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':\n\nCheck console output at ${env.BUILD_URL}""",
              recipientProviders: [[$class: 'CulpritsRecipientProvider'], [$class: 'DevelopersRecipientProvider']])
}
