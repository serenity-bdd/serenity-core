package net.serenitybdd.core.webdriver

import net.thucydides.core.webdriver.SupportedWebDriver
import spock.lang.Specification


class WhenListingSupportedWebDrivers extends Specification {
    def "IE should be a synonym for IExplorer"() {
        expect:
            SupportedWebDriver.valueOrSynonymOf("IE") == SupportedWebDriver.IEXPLORER
    }

    def "Should identify web driver types regardless of case"() {
        expect:
        SupportedWebDriver.valueOrSynonymOf("iExplorer") == SupportedWebDriver.IEXPLORER
    }

    def "The list of supported drivers should include synonyms"() {
        expect:
        SupportedWebDriver.listOfSupportedDrivers().contains("IEXPLORER,") && SupportedWebDriver.listOfSupportedDrivers().contains("IE,")
    }

}