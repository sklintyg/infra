#!groovy

def buildVersion = "3.0.${BUILD_NUMBER}"

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
        bGradle "./gradlew --refresh-dependencies clean build sonarqube -PcodeQuality -DgruntColors=false -DbuildVersion=${buildVersion}"
    }
}

stage('tag and upload') {
    node {
        bGradle "./gradlew uploadArchives tagRelease -DnexusUsername=$NEXUS_USERNAME -DnexusPassword=$NEXUS_PASSWORD \
                 -DgithubUser=$GITHUB_USERNAME -DgithubPassword=$GITHUB_PASSWORD -DbuildVersion=${buildVersion}"
    }
}

stage ('propagate') {
    build job: 'intyg-intygstyper-pipeline', wait: false, parameters: [[$class: 'StringParameterValue', name: 'GIT_BRANCH', value: GIT_BRANCH]]
}
