package net.thucydides.core.util

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.util.VersionProvider
import spock.lang.Specification


/**
 * Created by john on 19/06/2014.
 */
class WhenFindingTheCurrentAppVersion extends Specification {

    def "should find version from the versions.properties file"() {
        when:
        VersionProvider versionProvider = new VersionProvider()
        then:
            println versionProvider.version
            versionProvider.version != null
    }

    def "should find the build number from the environment variables"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
            environmentVariables.setValue("BUILD_NUMBER","123")
        when:
            VersionProvider versionProvider = new VersionProvider(environmentVariables)
        then:
            versionProvider.buildNumberText == "123"
    }


    def "should return UNKNOWN if build number is not provided"() {
        when:
        def environmentVariables = new MockEnvironmentVariables()
        VersionProvider versionProvider = new VersionProvider(environmentVariables)
        then:
        versionProvider.buildNumberText == "UNKNOWN"
    }


    def "should be able to use different environment variables for the build number"() {
        given:
        def environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setValue("SVN_REVISION","1234")
        environmentVariables.setProperty("build.number.variable","SVN_REVISION")
        when:
        VersionProvider versionProvider = new VersionProvider(environmentVariables)
        then:
        versionProvider.buildNumberText == "1234"
    }

}
