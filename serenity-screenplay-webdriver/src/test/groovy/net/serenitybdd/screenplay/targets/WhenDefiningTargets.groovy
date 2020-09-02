package net.serenitybdd.screenplay.targets

import io.appium.java_client.AppiumDriver
import io.appium.java_client.MobileBy
import net.serenitybdd.screenplay.Actor
import net.serenitybdd.screenplay.abilities.BrowseTheWeb
import net.thucydides.core.webdriver.ThucydidesConfigurationException
import org.mockito.Mock
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import spock.lang.Specification

import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

class WhenDefiningTargets extends Specification {

    @Mock
    AppiumDriver appiumDriver

    @Mock
    WebDriver driver

    @Mock
    RemoteWebDriver remoteWebDriver

    DesiredCapabilities dc

    def BY_TARGET = Target.the("By Target").located(By.name("Simple locator"))
    def CROSS_PLATFORM_BY_TARGET = Target.the("Cross Platform By Target")
                                         .locatedForAndroid(MobileBy.id("android_element_id"))
                                         .locatedForIOS(MobileBy.id("iOS_element_id"))

    def setup() {
        initMocks(this)
        dc = new DesiredCapabilities()
        dc.setCapability("app", "mock-app")
    }

    def "targets can be specified for iOS or Android"() {
        given:
        dc.setCapability("platformName", configuredPlatform)
        when(appiumDriver.getCapabilities()).thenReturn(dc)
        Actor dana = new Actor("Dana")
        dana.can(BrowseTheWeb.with(appiumDriver))
        when:
        def description = CROSS_PLATFORM_BY_TARGET.resolveFor(dana).toString()
        then:
        description.contains(expectedElementString)
        where:
        configuredPlatform     | expectedElementString
        "android"              | "android_element_id"
        "IOS"                  | "iOS_element_id"
    }

    def "an exception is thrown when trying to use a Cross Platform target in a non-mobile test"() {
        given:
        dc.setCapability("platformName", "android")
        Actor dana = new Actor("Dana")
        dana.can(BrowseTheWeb.with(driver))
        when:
        CROSS_PLATFORM_BY_TARGET.resolveFor(dana).toString()
        then:
        ThucydidesConfigurationException e = thrown()
        e.message.contains("The configured driver ")
    }

    def "an exception is thrown for invalid platforms"() {
        given:
        dc.setCapability("platformName", "Windows")
        when(remoteWebDriver.getCapabilities()).thenReturn(dc)
        Actor dana = new Actor("Dana")
        dana.can(BrowseTheWeb.with(remoteWebDriver))
        when:
        CROSS_PLATFORM_BY_TARGET.resolveFor(dana).toString()
        then:
        ThucydidesConfigurationException e = thrown()
        e.message.contains("is not a valid platform for Cross Platform Mobile targets")
    }

    def "targets can have a single locator"() {
        given:
        dc.setCapability("platformName", configuredPlatform)
        when(appiumDriver.getCapabilities()).thenReturn(dc)
        Actor dana = new Actor("Dana")
        dana.can(BrowseTheWeb.with(appiumDriver))
        when:
        def description = BY_TARGET.resolveFor(dana).toString()
        then:
        description.contains(expectedElementString)
        where:
        configuredPlatform     | expectedElementString
        "IOS"                  | "Simple locator"
        "android"              | "Simple locator"
    }
}
