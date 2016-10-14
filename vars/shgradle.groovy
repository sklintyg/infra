def call(gradleCommand) {
    util.run {
        withEnv(util.javaEnv()) {
            sh "./gradlew " + gradleCommand +
                " -DnexusUsername=$NEXUS_USERNAME -DnexusPassword=$NEXUS_PASSWORD" +
                " -DgithubUser=$GITHUB_USERNAME -DgithubPassword=$GITHUB_PASSWORD" +
                " -DbuildVersion=${buildVersion} -DcommonVersion=${commonVersion} -DtyperVersion=${typerVersion}"
        }
    }
}
