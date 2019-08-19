package net.thucydides.core.webdriver.chrome

import net.serenitybdd.core.webdriver.driverproviders.ChromeDriverCapabilities
import net.thucydides.core.util.EnvironmentVariables
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

class WhenPassingChromeExperimentalOptionsToWebdriver extends Specification {

    @Unroll
    def "Chrome experimental options should be added to chrome Capabilities"() {
        given:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty(optionText, optionValue.toString())
        def chromeCap = new ChromeDriverCapabilities(environmentVariables, "");
        when:
        def options = chromeCap.getCapabilities()
        then:
        def exp_opt = optionText.replaceAll("chrome_experimental_options.", "")
        options.getCapability("goog:chromeOptions")[exp_opt] == optionValue
        where:
        optionText                                           | optionValue
        "chrome_experimental_options.useAutomationExtension" | false
        "chrome_experimental_options.test1"                  | "String"
        "chrome_experimental_options.test2"                  | "Other String that is bigger"
        "chrome_experimental_options.test3"                  | true
        "chrome_experimental_options.test4.test1"            | 4
    }

    @Unroll
    def "Chrome experimental options should not be included in Chrome preference values"() {
        given:
        EnvironmentVariables environmentVariables = new MockEnvironmentVariables()
        environmentVariables.setProperty("chrome_preferences.should_not_be_added" , "someValue")
        def chromeCap = new ChromeDriverCapabilities(environmentVariables, "");
        when:
        def options = chromeCap.getCapabilities()
        then:
        !((TreeMap<String, Object>) options.getCapability("goog:chromeOptions")).containsKey("should_not_be_added")
    }

}