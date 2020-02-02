package net.serenitybdd.core.buildinfo

import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.util.SystemEnvironmentVariables
import spock.lang.Specification

class WhenReportingCustomBuildInfo extends Specification {

    EnvironmentVariables environmentVariables = new SystemEnvironmentVariables()

    def "it should report property values from the sysinfo variable group"() {
        given:
        environmentVariables.setProperty("sysinfo.lead","Daisy")
        when:
            def buildInfo = new BuildInfoProvider(environmentVariables)
        then:
        buildInfo.buildProperties.generalProperties["Lead"] == "Daisy"
    }

    def "it should evaluate environment variables with the 'end' prefix"() {
        given:
        environmentVariables.setProperty("BUILD_NUMBER","123")
        environmentVariables.setProperty("sysinfo.build",'${env.BUILD_NUMBER}')
        when:
        def buildInfo = new BuildInfoProvider(environmentVariables)
        then:
        buildInfo.buildProperties.generalProperties["Build"] == "123"
    }

    def "it should evaluate system variables with the 'sys' prefix"() {
        given:
        environmentVariables.setValue("BUILD_NUMBER","123")
        environmentVariables.setProperty("sysinfo.build",'${env.BUILD_NUMBER}')
        when:
        def buildInfo = new BuildInfoProvider(environmentVariables)
        then:
        buildInfo.buildProperties.generalProperties["Build"] == "123"
    }

}
