package net.serenitybdd.builds

class ProjectVersionCounter {
    final Boolean isRelease;

    ProjectVersionCounter(Boolean isRelease) {
        this.isRelease = isRelease
    }

    def getNextVersion() {
        def currentVersion = "git describe --tags".execute().text
        if (currentVersion.isEmpty()) {
            currentVersion = "v1.0.0"
        }
        def matcher = currentVersion =~ "\\d+"
        def majorMinorNumbers = matcher[0] + "." + matcher[1]
        def currentBuildNumber = Integer.valueOf(matcher[2])
        def nextBuildNumber = currentBuildNumber + 1
        return (isRelease) ?
                majorMinorNumbers + "." + nextBuildNumber :
                majorMinorNumbers + "." + nextBuildNumber + "-SNAPSHOT"
    }
}
