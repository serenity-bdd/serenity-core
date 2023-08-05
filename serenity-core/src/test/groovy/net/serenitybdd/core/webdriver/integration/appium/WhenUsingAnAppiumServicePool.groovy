package net.serenitybdd.core.webdriver.integration.appium

import net.serenitybdd.core.webdriver.appium.AppiumDevicePool
import net.serenitybdd.core.webdriver.appium.AppiumServerPool
import net.serenitybdd.annotations.Shared
import net.thucydides.model.environment.MockEnvironmentVariables
import spock.lang.Specification

class WhenUsingAnAppiumServicePool extends Specification {

    @Shared environmentVariables = new MockEnvironmentVariables()

    def setup() {
        AppiumDevicePool.clear()
    }

    def "If no appium.hub property is defined and a single device is defined, the default hub URL will be used"() {
        given:
            environmentVariables.clearProperty("appium.deviceNames")
            environmentVariables.clearProperty("appium.hub")
            environmentVariables.setProperty("appium.deviceName", "device1")
        when:
            def serverPool = new AppiumServerPool(environmentVariables)
        then:
            serverPool.urlFor("device1").getPort() == 4723
    }

    def "If the appium.hub property is defined with a single device, this will be used"() {
        given:
            environmentVariables.clearProperty("appium.deviceNames")
            environmentVariables.setProperty("appium.deviceName", "device1")
            environmentVariables.setProperty("appium.hub", "http://127.0.0.1:4725/wd/hub")
        when:
            def serverPool = new AppiumServerPool(environmentVariables)
        then:
            serverPool.urlFor("device1") == new URL("http://127.0.0.1:4725/wd/hub")
    }

    def "If the appium.hub property is defined with a list containing single device, this will be used"() {
        given:
        environmentVariables.clearProperty("appium.deviceName")
        environmentVariables.setProperty("appium.deviceNames", "device1")
        environmentVariables.setProperty("appium.hub", "http://127.0.0.1:4725/wd/hub")
        when:
        def serverPool = new AppiumServerPool(environmentVariables)
        then:
        serverPool.urlFor("device1") == new URL("http://127.0.0.1:4725/wd/hub")
    }

    def "If multiple devices are used, appium.hub will be ignored and a new URL will be provided for each device"() {
        given:
        environmentVariables.clearProperty("appium.deviceName")
        environmentVariables.clearProperty("appium.hub")
        environmentVariables.setProperty("appium.deviceNames", "device1, device2")
        when:
        def serverPool = new AppiumServerPool(environmentVariables)
        def device1Url = serverPool.urlFor("device1")
        def device2Url = serverPool.urlFor("device2")
        then:
        device2Url != device1Url
    }

    def "should start a new server for each new device name"() {
        given:
            environmentVariables.clearProperty("appium.deviceName")
            environmentVariables.clearProperty("appium.hub")
            environmentVariables.setProperty("appium.deviceNames", "device1, device2")
        when:
            def serverPool = new AppiumServerPool(environmentVariables)
            serverPool.urlFor("device1")
            serverPool.urlFor("device2")
            serverPool.urlFor("device1")
        then:
            serverPool.activeServersInCurrentThread.size() == 2
    }

    def "should shutdown all servers in the current thread at the end of the thread"() {
        given:
            environmentVariables.clearProperty("appium.deviceName")
            environmentVariables.clearProperty("appium.hub")
            environmentVariables.setProperty("appium.deviceNames", "device1, device2")
        when:
            def serverPool = new AppiumServerPool(environmentVariables)
            serverPool.urlFor("device1")
            serverPool.urlFor("device2")
        and:
            serverPool.shutdownAllServersRunningOnThread(Thread.currentThread())
        then:
            serverPool.activeServersInCurrentThread.isEmpty()
    }

}
