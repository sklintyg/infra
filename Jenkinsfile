#!groovy

node {
    def buildVersion = "3.19.1.${BUILD_NUMBER}"
    def versionFlags = "-DbuildVersion=${buildVersion}"

    stage('checkout') {
        git url: GIT_URL, branch: GIT_BRANCH
        util.run { checkout scm }
    }

    stage('build') {
        try {
            shgradle11 "--refresh-dependencies clean build -PcodeQuality -DgruntColors=false ${versionFlags}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/allTests', \
                reportFiles: 'index.html', reportName: 'JUnit results'
        }
    }

    stage('tag and upload') {
        shgradle11 "uploadArchives tagRelease ${versionFlags}"
    }

    stage('notify') {
        util.notifySuccess()
    }
}
