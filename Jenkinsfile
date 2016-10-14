#!groovy

def buildVersion = "3.0.${BUILD_NUMBER}"

stage('checkout') {
    node {
        util.run { checkout scm }
    }
}

stage('build') {
    node {
        shgradle "--refresh-dependencies clean build sonarqube -PcodeQuality -DgruntColors=false"
    }
}

stage('tag and upload') {
    node {
        shgradle "uploadArchives tagRelease"
    }
}

stage ('propagate') {
    build job: 'intyg-intygstyper-pipeline', wait: false, parameters: [[$class: 'StringParameterValue', name: 'GIT_BRANCH', value: GIT_BRANCH]]
}
