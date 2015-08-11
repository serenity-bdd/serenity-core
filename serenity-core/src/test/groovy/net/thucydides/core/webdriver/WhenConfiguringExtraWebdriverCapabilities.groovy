package net.thucydides.core.webdriver

import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenConfiguringExtraWebdriverCapabilities extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should add capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","build:build-1234")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["build":"build-1234"]
    }

    def "should add no capabilities if the system property is not defined"() {

        given:
        environmentVariables.clearProperty("thucydides.driver.capabilities")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == [:]
    }


    def "should add multiple capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","build:build-1234;version:2.8.0")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["build":"build-1234", "version":"2.8.0"]
    }

    def "should add integer capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","timeout:300")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["timeout":300]
    }

    def "should add boolean capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","disable-popup-handler:true")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["disable-popup-handler":true]
    }

    def "should add value-list capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","tags:[tag1,tag2,tag3]")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["tags":["tag1","tag2","tag3"]]
    }

    def "should support windows paths in capability values"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","app:D:/GoogleEx/GoogleEx.apk")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["app":"D:/GoogleEx/GoogleEx.apk"]
    }

    def "should add value-list capabilities with non-string types defined in a system property"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","numbers:[1,2,3]")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["numbers":[1,2,3]]
    }

    def "should add capabilities with white spaces"() {

        given:
        environmentVariables.setProperty("thucydides.driver.capabilities","device:iPhone 5S")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["device":"iPhone 5S"]
    }
}
