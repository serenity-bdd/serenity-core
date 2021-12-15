package net.thucydides.core.webdriver.appium

import net.thucydides.core.util.FileSeparatorUtil
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.util.PathProcessor
import net.thucydides.core.webdriver.MobilePlatform
import net.thucydides.core.webdriver.ThucydidesConfigurationException
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
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

    def "can alternatively derive the target platform from the context variable"() {
        given:
        environmentVariables.setProperty("context", value)
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.targetPlatform == expectedPlatform
        where:
        value     | expectedPlatform
        "IOS"     | MobilePlatform.IOS
        "android" | MobilePlatform.ANDROID
    }

    def "the context overrides the environment property for the target platform"() {
        given:
        environmentVariables.setProperty("appium.platformName", env)
        environmentVariables.setProperty("context", context)
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.targetPlatform == expectedPlatform
        where:
        context   | env       | expectedPlatform
        "IOS"     | "android" | MobilePlatform.IOS
        "android" | "IOS"     | MobilePlatform.ANDROID
    }

    def "invalid contexts fall back to the environment property"() {
        given:
        environmentVariables.setProperty("appium.platformName", env)
        environmentVariables.setProperty("context", context)
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.targetPlatform == expectedPlatform
        where:
        env       | context  | expectedPlatform
        "IOS"     | "Oreo"   | MobilePlatform.IOS
        "android" | "IOS6.0" | MobilePlatform.ANDROID
    }

    def "the platform may be defined by the driver capabilities"() {
        given:
        def driver = Stub(RemoteWebDriver)
        def caps = new DesiredCapabilities()
        caps.setCapability("platformName", value)
        driver.getCapabilities() >> caps
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        def definedPlatform = appiumConfiguration.getTargetPlatform(driver)
        then:
        definedPlatform == expectedPlatform
        where:
        value     | expectedPlatform
        "IOS"     | MobilePlatform.IOS
        "ANDROID" | MobilePlatform.ANDROID
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
        invalidConfiguration.message.contains("The browser under test or path to the app or (appPackage and appActivity) needs to be provided in the appium.app or appium.browserName property.")
    }

    def "should filter Appium properties that are not supported if 'appium.process.desired.capabilities' is enabled"() {
        given:
        environmentVariables.setProperty("appium.process.desired.capabilities", "true")
        environmentVariables.setProperty("appium.unknown", "value")
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        !appiumConfiguration.capabilities.getCapabilityNames().contains("unknown")
    }

    def "should not filter Appium properties that are not supported if 'appium.process.desired.capabilities' is disabled"() {
        given:
        environmentVariables.setProperty("appium.build", "value")
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.capabilities.getCapabilityNames().contains("build")
    }

    def "should add 'appium:' prefix if capability listed in 'appium.additional.capabilities and 'appium.process.desired.capabilities' enabled"() {
        given:
        environmentVariables.setProperty("appium.process.desired.capabilities", "true")
        environmentVariables.setProperty("appium.unknown", "value")
        environmentVariables.setProperty("appium.additional.capabilities", "unknown, ")
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.capabilities.getCapability("appium:unknown") == "value"
    }

    def "should get default environment specific properties when no environment is specified"() {
        given:
        environmentVariables.setProperty("environments.default.appium.showChromedriverLog", "true")
        environmentVariables.setProperty("environments.default.appium.showIOSLog", "true")
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.capabilities.getCapability("showChromedriverLog") == "true"
        appiumConfiguration.capabilities.getCapability("showIOSLog") == "true"
        appiumConfiguration.capabilities.capabilityNames.size() == 3
    }

    def "should get properties defined in all environments plus default environment properties"() {
        given:
        environmentVariables.setProperty("environments.default.appium.showChromedriverLog", "true")
        environmentVariables.setProperty("environments.all.appium.autoWebview", "true")
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.capabilities.getCapability("showChromedriverLog") == "true"
        appiumConfiguration.capabilities.getCapability("autoWebview") == "true"
        appiumConfiguration.capabilities.capabilityNames.size() == 3

    }

    def "should get only the environment defined properties when using specific environments"() {
        given:
        environmentVariables.setProperty("environments.android.appium.showChromedriverLog", "true")
        environmentVariables.setProperty("environments.ios.appium.showIOSLog", "true")
        environmentVariables.setProperty("environment", "android")
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.capabilities.getCapability("showChromedriverLog") == "true"
        !appiumConfiguration.capabilities.capabilityNames.contains("showIOSLog")

    }

    def "should get properties defined in all environments plus specific environment properties"() {
        given:
        environmentVariables.setProperty("environments.android.appium.showChromedriverLog", "true")
        environmentVariables.setProperty("environments.ios.appium.showIOSLog", "true")
        environmentVariables.setProperty("environments.all.appium.autoWebview", "true")
        environmentVariables.setProperty("environment", "ios")
        environmentVariables.setProperty("appium.app", 'classpath:/apps/dummy-app')
        when:
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        then:
        appiumConfiguration.capabilities.getCapability("showIOSLog") == "true"
        appiumConfiguration.capabilities.getCapability("autoWebview") == "true"
        !appiumConfiguration.capabilities.capabilityNames.contains("showChromedriverLog")
        appiumConfiguration.capabilities.capabilityNames.size() == 3
    }

    def "should be able to build appium mobile driver capabilities using only browserName without app, appPackage or appActivity"() {
        given:
        environmentVariables.setProperty("webdriver.driver", "appium")
        environmentVariables.setProperty("appium.platformName", "Android")
        environmentVariables.setProperty("appium.platformVersion", "9.0")
        environmentVariables.setProperty("appium.browserName", "Chrome")
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        appiumConfiguration.capabilities
        then:
        appiumConfiguration.capabilities != null
    }

    def "should be able to build appium mobile driver capabilities using only app without appPackage, appActivity or browserName"() {
        given:
        environmentVariables.setProperty("webdriver.driver", "appium")
        environmentVariables.setProperty("appium.platformName", "Android")
        environmentVariables.setProperty("appium.platformVersion", "9.0")
        environmentVariables.setProperty("appium.app", "MyCustomApp")
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        appiumConfiguration.capabilities
        then:
        appiumConfiguration.capabilities != null
    }

    def "should be able to build appium mobile driver capabilities using only appPackage and appActivity without app or browserName"() {
        given:
        environmentVariables.setProperty("webdriver.driver", "appium")
        environmentVariables.setProperty("appium.platformName", "Android")
        environmentVariables.setProperty("appium.platformVersion", "9.0")
        environmentVariables.setProperty("appium.appPackage", "com.example.android.myApp")
        environmentVariables.setProperty("appium.appActivity", "SplashActivity")
        def appiumConfiguration = AppiumConfiguration.from(environmentVariables)
        when:
        appiumConfiguration.capabilities
        then:
        appiumConfiguration.capabilities != null
    }
}