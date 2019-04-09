package net.thucydides.core.model

import net.thucydides.core.requirements.model.RequirementsConfiguration
import net.thucydides.core.util.MockEnvironmentVariables
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
