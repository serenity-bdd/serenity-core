package net.serenitybdd.core.i8n

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration
import net.serenitybdd.core.environment.UndefinedEnvironmentVariableException
import net.thucydides.core.ThucydidesSystemProperty
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenConfiguringInternationalisedLabels extends Specification {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    def "Localised labels can be configured in a simple HOCON format"() {

    }
}
