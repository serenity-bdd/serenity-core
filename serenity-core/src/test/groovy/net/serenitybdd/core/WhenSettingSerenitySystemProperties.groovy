package net.serenitybdd.core

import net.thucydides.model.ThucydidesSystemProperty
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenSettingSerenitySystemProperties extends Specification{

    def "should accept serenity.* system properties"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("serenity.take.screenshots","FOR_EACH_ACTION")
        then:
            ThucydidesSystemProperty.THUCYDIDES_TAKE_SCREENSHOTS.from(environmentVariables) == "FOR_EACH_ACTION"
    }

    def "should accept legacy thucydides.* system properties"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("thucydides.take.screenshots","FOR_EACH_ACTION")
        then:
            ThucydidesSystemProperty.THUCYDIDES_TAKE_SCREENSHOTS.from(environmentVariables) == "FOR_EACH_ACTION"
    }

    def "should accept numeric serenity.* system properties"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("serenity.batch.size","4")
        then:
            ThucydidesSystemProperty.THUCYDIDES_BATCH_SIZE.integerFrom(environmentVariables,0) == 4
    }

    def "should accept numeric legacy thucydides.* system properties"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("thucydides.batch.size","4")
        then:
            ThucydidesSystemProperty.THUCYDIDES_BATCH_SIZE.integerFrom(environmentVariables,0) == 4
    }

    def "serenity.* system properties should take priority over legacy thuycydides.* ones"() {
        given:
            def environmentVariables = new MockEnvironmentVariables()
        when:
            environmentVariables.setProperty("serenity.take.screenshots","FOR_EACH_ACTION")
            environmentVariables.setProperty("thucydides.take.screenshots","FOR_FAILURES")
        then:
            ThucydidesSystemProperty.THUCYDIDES_TAKE_SCREENSHOTS.from(environmentVariables) == "FOR_EACH_ACTION"
    }
}
