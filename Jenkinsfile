#!groovy

def buildVersion = "3.11.0.${BUILD_NUMBER}"

stage('checkout') {
    node {
        git url: "https://github.com/sklintyg/infra.git", branch: GIT_BRANCH
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        try {
            shgradle "--refresh-dependencies clean build testReport sonarqube -PcodeQuality -PcodeCoverage -DgruntColors=false -DbuildVersion=${buildVersion}"
        } finally {
            publishHTML allowMissing: true, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'build/reports/allTests', \
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
    [ "intygstjanst", "rehabstod", "statistik", "intygsadmin", "logsender", "privatlakarportal" ].each {
        try {
            build job: "dintyg-${it}-test-pipeline", parameters: [string(name: 'GIT_BRANCH', value: GIT_BRANCH)]
        } catch (e) {
            println "Trigger build error (ignored): ${e.message}"
        }
    }
}

stage('notify') {
    node {
        util.notifySuccess()
    }
}
