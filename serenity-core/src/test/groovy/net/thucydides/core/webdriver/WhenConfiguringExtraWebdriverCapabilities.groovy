package net.thucydides.core.webdriver

import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenConfiguringExtraWebdriverCapabilities extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should add capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","build:build-1234")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["build":"build-1234"]
    }


    def "should allow capabilities containing two colons"() {
        given:
        environmentVariables.setProperty("serenity.driver.capabilities","e34:token:ccccccccc")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["e34:token":"ccccccccc"]
    }

    def "should allow capabilities containing multiple colons"() {
        given:
        environmentVariables.setProperty("serenity.driver.capabilities","e34:token1:token2:ccccccccc")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["e34:token1:token2":"ccccccccc"]
    }

    def "should add no capabilities if the system property is not defined"() {

        given:
        environmentVariables.clearProperty("serenity.driver.capabilities")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == [:]
    }


    def "should add multiple capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","build:build-1234;version:2.8.0")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["build":"build-1234", "version":"2.8.0"]
    }

    def "should add integer capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","timeout:300")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["timeout":300]
    }

    def "should add boolean capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","disable-popup-handler:true")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["disable-popup-handler":true]
    }

    def "should add value-list capabilities defined in a system property"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","tags:[tag1,tag2,tag3]")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["tags":["tag1","tag2","tag3"]]
    }

    def "should support windows paths in capability values"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","app:D:/GoogleEx/GoogleEx.apk")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["app":"D:/GoogleEx/GoogleEx.apk"]
    }

    def "should not crash if drive only windows path is specified as capability"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","app:D:/")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["app":"D:/"]
    }

    def "should not crash if only windows path is specified as capability"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","D:/GoogleEx/GoogleEx.apk")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["D":"/GoogleEx/GoogleEx.apk"]
    }

    def "should add value-list capabilities with non-string types defined in a system property"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","numbers:[1,2,3]")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["numbers":[1,2,3]]
    }

    def "should add capabilities with white spaces"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","device:iPhone 5S")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["device":"iPhone 5S"]
    }

    def "should add value-map capabilities"() {

        given:
        environmentVariables.setProperty("serenity.driver.capabilities","moon:options:{\"enableVNC\": true}")
        when:
        def capabilitySet = new CapabilitySet(environmentVariables)
        then:
        capabilitySet.capabilities == ["moon:options": ["enableVNC": true]]
    }
}
