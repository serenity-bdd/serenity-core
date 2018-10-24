package net.serenitybdd.core.webdriver.appium

import net.serenitybdd.core.webdriver.driverproviders.AppiumDriverProvider
import net.thucydides.core.annotations.Shared
import net.thucydides.core.events.TestLifecycleEvents
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.webdriver.WebDriverInstanceEvents
import net.thucydides.core.webdriver.WebDriverLifecycleEvent
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class WhenUsingAnAppiumDevicePool extends Specification {

    @Shared
    environmentVariables = new MockEnvironmentVariables()

    def "A list of available appium device names can be provided with the appium.device.names system property."() {
        given:
            environmentVariables.setProperty("appium.deviceNames","device1, device2, device3")
        when:
            def devicePool = new AppiumDevicePool(environmentVariables)
        then:
            devicePool.availableDevices == ["device1", "device2", "device3"]
    }

    def "If no device names are provided, a pool with just the appium.device.name will be created"() {
        given:
            environmentVariables.setProperty("appium.deviceName","device1")
        when:
            def devicePool = new AppiumDevicePool(environmentVariables)
        then:
            devicePool.availableDevices == ["device1"]
    }

    def "If no device name is provided, the pool should be empty"() {
        when:
        def devicePool = new AppiumDevicePool(environmentVariables)
        then:
        devicePool.availableDevices.isEmpty()
    }



    def "the pool should provide the first free device name"() {
        given:
            environmentVariables.setProperty("appium.deviceNames","device1, device2, device3")
        when:
            def devicePool = new AppiumDevicePool(environmentVariables)
        then:
            devicePool.requestDevice() == "device1"
            devicePool.requestDevice() == "device2"
            devicePool.requestDevice() == "device3"
    }

    def "clients can release a device name when they are finished"() {
        given:
            environmentVariables.setProperty("appium.deviceNames","device1, device2, device3")
        when:
            def devicePool = new AppiumDevicePool(environmentVariables)

            devicePool.requestDevice() == "device1"
            devicePool.freeDevice("device1")
        then:
            devicePool.availableDevices.contains("device1")
    }

    def driver = Mock(WebDriver)

    def "should be able to register for driver close and quit events"() {
        given:
            environmentVariables.setProperty("appium.deviceNames","device1, device2, device3")
            AppiumDevicePool devicePool = new AppiumDevicePool(environmentVariables)
        and:
            WebDriverInstanceEvents bus = new WebDriverInstanceEvents()
            bus.register(new AppiumDriverProvider.AppiumEventListener(driver, "device1", devicePool))
        when:
            bus.notifyOf(WebDriverLifecycleEvent.CLOSE).forDriver(driver)
        then:
            devicePool.availableDevices.contains("device1")
    }



    def driver2 = Mock(WebDriver)

    def "should ignore notifications for other drivers"() {
        given:
        environmentVariables.setProperty("appium.deviceNames","device1, device2, device3")
        AppiumDevicePool devicePool = new AppiumDevicePool(environmentVariables)
        and:
        WebDriverInstanceEvents bus = new WebDriverInstanceEvents()
        def claimedDevice = devicePool.requestDevice()
        bus.register(new AppiumDriverProvider.AppiumEventListener(driver, claimedDevice, devicePool))
        when:
        bus.notifyOf(WebDriverLifecycleEvent.CLOSE).forDriver(driver2)
        then:
        !devicePool.availableDevices.contains(claimedDevice)
    }


    def "Devices in-use cannot be claimed"() {
        given:
        environmentVariables.setProperty("appium.deviceNames","device1, device2, device3")
        AppiumDevicePool devicePool = new AppiumDevicePool(environmentVariables)
        and:
        WebDriverInstanceEvents bus = new WebDriverInstanceEvents()
        when:
        def claimedDevice = devicePool.requestDevice()
        bus.register(new AppiumDriverProvider.AppiumEventListener(driver, claimedDevice, devicePool))
        then:
        !devicePool.availableDevices.contains(claimedDevice)
    }

    def "should free all devices in the current thread at the end of the test"() {
        given:
            environmentVariables.setProperty("appium.deviceNames","device1, device2, device3")
            AppiumDevicePool devicePool = new AppiumDevicePool(environmentVariables)
        and:
            WebDriverInstanceEvents bus = new WebDriverInstanceEvents()
            bus.register(new AppiumDriverProvider.AppiumEventListener(driver, devicePool.requestDevice(), devicePool))
            bus.register(new AppiumDriverProvider.AppiumEventListener(driver, devicePool.requestDevice(), devicePool))
        when:
            TestLifecycleEvents.postEvent(TestLifecycleEvents.testFinished());
        then:
            devicePool.availableDevices.containsAll(["device1","device2","device3"])
    }


    def "should provide the default port if a device pool is not set up"() {
        given:
        environmentVariables.setProperty("appium.deviceName","device1")
        when:
        AppiumDevicePool devicePool = new AppiumDevicePool(environmentVariables)
        then:
        devicePool.portFor("device1") == 4273
    }

}