package net.serenitybdd.core.webdriver

import net.serenitybdd.core.webdriver.driverproviders.AddEnvironmentSpecifiedDriverCapabilities
import net.serenitybdd.core.webdriver.driverproviders.CustomCapabilities
import net.thucydides.model.environment.MockEnvironmentVariables
import net.thucydides.model.util.EnvironmentVariables
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static net.thucydides.core.webdriver.SupportedWebDriver.IEXPLORER

/**
 * Created by john on 12/05/2016.
 */
class WhenDefiningCustomDriverPropertiesInTheSystemProperties extends Specification {

    @Shared
    EnvironmentVariables environmentVariables = new MockEnvironmentVariables()

    @Unroll
    def "should read custom IE capabilities from the environment"() {
        when:
        environmentVariables.setProperty(configueredProperty, configuredValue)
        then:
        CustomCapabilities.forDriver(driver).from(environmentVariables)[expectedProperty] == expectedValue

        where:
        driver    | configueredProperty                                   | configuredValue   | expectedProperty                               | expectedValue
        IEXPLORER | "driver_capabilities.iexplorer.ie.ensureCleanSession" | "true"            | InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION | true
        IEXPLORER | "driver_capabilities.iexplorer.initialBrowserUrl"     | "http://some.url" | InternetExplorerDriver.INITIAL_BROWSER_URL     | "http://some.url"
    }

    @Unroll
    def "configured driver capabilities properties from the environment"() {
        when:
        environmentVariables.setProperty(configueredProperty, configuredValue)
        then:
        CustomCapabilities.forDriver(driver).from(environmentVariables)[expectedProperty] == expectedValue

        where:
        driver    | configueredProperty                          | configuredValue | expectedProperty                | expectedValue
        IEXPLORER | "driver_capabilities.common.acceptInsecureCerts" | "false"         | CapabilityType.ACCEPT_INSECURE_CERTS | false
    }

    def "configured driver capabilities should not include capabilities from other drivers"() {
        when:
        environmentVariables.setProperty("driver_capabilities.common.takesScreenshot", "true")
        environmentVariables.setProperty("driver_capabilities.iexplorer.ie.ensureCleanSession", "true")
        environmentVariables.setProperty("driver_capabilities.firefox.someFirefoxValue", "true")
        then:
        CustomCapabilities.forDriver(IEXPLORER).from(environmentVariables)["takesScreenshot"] == true &&
                CustomCapabilities.forDriver(IEXPLORER).from(environmentVariables)["ie.ensureCleanSession"] == true &&
                CustomCapabilities.forDriver(IEXPLORER).from(environmentVariables).containsKey("someFirefoxValue") == false
    }

    def "should add custom capabilities to a Capabiities object"() {
        given:
        environmentVariables.setProperty("driver_capabilities.common.takesScreenshot", "true")
        environmentVariables.setProperty("driver_capabilities.iexplorer.ie.ensureCleanSession", "true")
        and:
        DesiredCapabilities capabilities = new DesiredCapabilities()
        when:
        capabilities = AddEnvironmentSpecifiedDriverCapabilities.from(environmentVariables).forDriver(IEXPLORER).to(capabilities)
        then:
        capabilities.getCapability("takesScreenshot") == true &&
        capabilities.getCapability("ie.ensureCleanSession") == true
    }
}
