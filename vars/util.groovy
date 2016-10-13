def safeExecute(Closure body) {
    try {
        body()
    } catch (e) {
        currentBuild.result = "FAILED"
        notifyFailed()
        throw e
    }
}
