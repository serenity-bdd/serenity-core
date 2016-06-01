package net.thucydides.core.webdriver

import spock.lang.Specification

/**
 * Created by john on 27/05/2016.
 */
class WhenManagingWebdriverSupportInOtherFrameworks extends Specification {

    def setup() {
        ThucydidesWebDriverSupport.clearDefaultDriver()
        ThucydidesWebDriverSupport.closeAllDrivers()
        ThucydidesWebDriverSupport.initialize()
    }

    def cleanup() {
        ThucydidesWebDriverSupport.closeAllDrivers()
    }

    def "should be able to declare a default driver"() {
        when:
            ThucydidesWebDriverSupport.useDefaultDriver("htmlunit")
            ThucydidesWebDriverSupport.getDriver().manage()
        then:
            ThucydidesWebDriverSupport.getCurrentDriverName() == "htmlunit"
    }

}
