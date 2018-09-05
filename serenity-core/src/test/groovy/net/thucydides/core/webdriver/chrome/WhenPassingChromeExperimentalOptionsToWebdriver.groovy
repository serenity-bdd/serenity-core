package net.thucydides.core.webdriver.chrome

import net.serenitybdd.core.webdriver.driverproviders.ChromeDriverCapabilities
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll

class WhenPassingChromeExperimentalOptionsToWebdriver extends Specification {

    @Unroll
    def "Chrome experimental options should be added to chrome Capabilities"() {
        given:
        System.setProperty(optionText, optionValue.toString());
        def chromeCap = new ChromeDriverCapabilities(MockEnvironmentVariables.fromSystemEnvironment(), "");
        when:
        def options = chromeCap.getCapabilities()
        then:
        def exp_opt = optionText.replaceAll("chrome_experimental_options.", "")
        ((TreeMap<String, Object>) options.getCapability("goog:chromeOptions")).containsKey(exp_opt) == shouldContain
        if (shouldContain) {
            ((TreeMap<String, Object>) options.getCapability("goog:chromeOptions")).get(exp_opt) == optionValue
        }
        where:
        optionText                                           | optionValue                   | shouldContain
        "chrome_experimental_options.useAutomationExtension" | false                         | true
        "chrome_experimental_options.test1"                  | "String"                      | true
        "chrome_experimental_options.test2"                  | "Other String that is bigger" | true
        "chrome_experimental_options.test3"                  | true                          | true
        "chrome_experimental_options.test4.test1"            | 4                             | true
        "chrome_preferences.should_not_be_added"             | false                         | false
    }


}