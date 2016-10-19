#!groovy

def buildVersion = "3.1.${BUILD_NUMBER}"

stage('checkout') {
    node {
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        try {
            shgradle "--refresh-dependencies clean build sonarqube -PcodeQuality -DgruntColors=false -DbuildVersion=${buildVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'web/build/reports/tests/test', \
                reportFiles: 'index.html', reportName: 'JUnit results'
        }
    }
}

stage('tag and upload') {
    node {
        shgradle "uploadArchives tagRelease -DbuildVersion=${buildVersion}"
    }
}

stage('propagate') {
    build job: 'intyg-intygstyper-pipeline', wait: false, parameters: [[$class: 'StringParameterValue', name: 'GIT_BRANCH', value: GIT_BRANCH]]
}
