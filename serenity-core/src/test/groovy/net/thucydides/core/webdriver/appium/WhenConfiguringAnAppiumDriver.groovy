package net.thucydides.core.webdriver.appium

import net.thucydides.core.util.FileSeparatorUtil
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.util.PathProcessor
import net.thucydides.core.webdriver.MobilePlatform
import net.thucydides.core.webdriver.ThucydidesConfigurationException
import spock.lang.Specification

/**
 * Created by Ben on 10/11/14.
 */
class WhenConfiguringAnAppiumDriver extends Specification {

    def environmentVariables = new MockEnvironmentVariables()

    def "should derive the target platform from the environment properties"() {
        given:
        environmentVariables.setProperty("appium.platformName", value)
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.targetPlatform == expectedPlatform
        where:
        value     | expectedPlatform
        "IOS"     | MobilePlatform.IOS
        "android" | MobilePlatform.ANDROID
    }

    def "should provide meaningful message if the platform is not specified"() {
        given:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        appiumConfiguration.targetPlatform
        then:
        ThucydidesConfigurationException invalidConfiguration = thrown()
        invalidConfiguration.message.contains("appium.platformName needs to be specified (either IOS or ANDROID)")
    }

    def "default URL should be 'http://127.0.0.1:4723/wd/hub'"() {
        given:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        def url = appiumConfiguration.url
        then:
        url == new URL('http://127.0.0.1:4723/wd/hub')
    }

    def "Hub URL should be specified in the environment properties"() {
        given:
        environmentVariables.setProperty("appium.hub", 'http://someserver:4723/wd/hub')
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        def url = appiumConfiguration.url
        then:
        url == new URL('http://someserver:4723/wd/hub')
    }

    def "Other Appium properties are defined with the 'appium' prefix"() {
        given:
        environmentVariables.setProperty("appium.hub", 'http://someserver:4723/wd/hub')
        environmentVariables.setProperty("appium.platformVersion", '7.1')
        environmentVariables.setProperty("appium.deviceName", 'iPhone Simulator')
        environmentVariables.setProperty("appium.browserName", '')
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.capabilities.getCapability("platformVersion") == "7.1"
        appiumConfiguration.capabilities.getCapability("deviceName") == "iPhone Simulator"
        appiumConfiguration.capabilities.getCapability("browserName") == ""
    }

    def "The Appium app can be specified as a full file path"() {
        given:
        def fullAppPath = new PathProcessor().normalize("classpath:apps/dummy-app")
        environmentVariables.setProperty("appium.app", fullAppPath)
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        def appPath = appiumConfiguration.capabilities.getCapability("app").toString()
        appPath == fullAppPath
    }

    def "The Appium path can be defined on the project class path"() {
        given:
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        def appPath = appiumConfiguration.capabilities.getCapability("app").toString()
        def expectedEnding = FileSeparatorUtil.changeSeparatorIfRequired("/apps/dummy-app")
        appPath.endsWith(expectedEnding)
    }

    def "should provide meaningful message if the app path is not specified"() {
        given:
        environmentVariables.setProperty("appium.platformName", "IOS")
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        appiumConfiguration.capabilities
        then:
        ThucydidesConfigurationException invalidConfiguration = thrown()
        invalidConfiguration.message.contains("The path to the app needs to be provided in the appium.app property.")
    }

}
