package net.thucydides.model.domain

import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.requirements.model.RequirementsConfiguration
import spock.lang.Specification

class WhenBuildCucumberRequirementsConfigurationOnClasspath extends Specification {

    MockEnvironmentVariables environmentVariables

    void setup() {
        environmentVariables = new MockEnvironmentVariables()
    }

    def "should be able to build Cucumber requirements configuration from classpath"() {
        when:
            new RequirementsConfiguration(environmentVariables, "features")
        then:
            noExceptionThrown()
    }
}
