package net.serenitybdd.core.buildinfo

import net.thucydides.core.util.EnvironmentVariables
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

    def "it should evaluate environment variables with the 'env' prefix"() {
        given:
        environmentVariables.setProperty("THE_BUILD_NUMBER","123")
        environmentVariables.setProperty("sysinfo.build",'${env.THE_BUILD_NUMBER}')
        when:
        def buildInfo = new BuildInfoProvider(environmentVariables)
        then:
        buildInfo.buildProperties.generalProperties["Build"] == "123"
    }

    def "it should evaluate system variables with the 'env' prefix"() {
        given:
        environmentVariables.setValue("THE_BUILD_NUMBER","123")
        environmentVariables.setProperty("sysinfo.build",'${env.THE_BUILD_NUMBER}')
        when:
        def buildInfo = new BuildInfoProvider(environmentVariables)
        then:
        buildInfo.buildProperties.generalProperties["Build"] == "123"
    }

    def "it should allow human-readable sections"() {
        given:
        environmentVariables.setProperty("sysinfo.staff.ran_by","John")
        when:
        def buildInfo = new BuildInfoProvider(environmentVariables)
        then:
        buildInfo.buildProperties.sectionTitles == ["Staff"]
        and:
        buildInfo.buildProperties.sections["Staff"]["Ran by"] == "John"
    }

}
