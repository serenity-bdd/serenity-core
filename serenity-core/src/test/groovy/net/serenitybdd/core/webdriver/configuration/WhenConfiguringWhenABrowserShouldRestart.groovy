package net.serenitybdd.core.webdriver.configuration

import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

import static net.serenitybdd.core.webdriver.configuration.RestartBrowserForEach.*

class WhenConfiguringWhenABrowserShouldRestart extends Specification {

    def "You can request a driver to remain open forever"() {
        expect:
            RestartBrowserForEach configuredLevel = NEVER
            configuredLevel.restartBrowserForANew(event) == shouldRestart
        where:
            event    | shouldRestart
            EXAMPLE  | false
            SCENARIO | false
            STORY    | false
            FEATURE  | false
    }
 
    def "You can restart the browser at most for each example in a data-driven test"() {
        expect:
        RestartBrowserForEach configuredLevel = EXAMPLE
        configuredLevel.restartBrowserForANew(event) == shouldRestart
        where:
        event    | shouldRestart
        EXAMPLE  | true
        SCENARIO | true
        STORY    | true
        FEATURE  | true
    }

    def "A driver can restart each scenaro but remain open for each example in a data-driven scenario"() {
        expect:
            RestartBrowserForEach configuredLevel = SCENARIO
            configuredLevel.restartBrowserForANew(event) == shouldRestart
        where:
            event    | shouldRestart
            EXAMPLE  | false
            SCENARIO | true
            STORY    | true
            FEATURE  | true
    }

    def "A driver can restart each feature but remain open for each scenario in a feature"() {
        expect:
            RestartBrowserForEach configuredLevel = FEATURE
            configuredLevel.restartBrowserForANew(event) == shouldRestart
        where:
            event    | shouldRestart
            EXAMPLE  | false
            SCENARIO | false
            STORY    | true
            FEATURE  | true
    }

    def "A driver can restart each story but remain open for each scenario in a story"() {
        expect:
            RestartBrowserForEach configuredLevel = STORY
            configuredLevel.restartBrowserForANew(event) == shouldRestart
        where:
            event    | shouldRestart
            EXAMPLE  | false
            SCENARIO | false
            STORY    | true
            FEATURE  | true
    }

    def "browser restart options are configured using the serenity.restart.browser.for.each property"() {
        given:
            def environmentProperties = new MockEnvironmentVariables()
        when:
            environmentProperties.setProperty("serenity.restart.browser.for.each","scenario")
        then:
            RestartBrowserForEach.configuredIn(environmentProperties) == SCENARIO
    }

    def "default browser restart options is 'example"() {
        given:
        def environmentProperties = new MockEnvironmentVariables()
        when:
        environmentProperties.setProperty("serenity.restart.browser.for.each","unknown")
        then:
        RestartBrowserForEach.configuredIn(environmentProperties) == EXAMPLE
    }

}
