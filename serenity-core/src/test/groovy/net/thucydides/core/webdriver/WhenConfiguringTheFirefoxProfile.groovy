package net.thucydides.core.webdriver

import net.serenitybdd.core.Serenity
import net.thucydides.core.util.MockEnvironmentVariables
import net.thucydides.core.configuration.SystemPropertiesConfiguration
import org.openqa.selenium.firefox.FirefoxProfile
import spock.lang.Ignore
import spock.lang.Specification

// TODO: Refactor tests for new architecture

@Ignore
class WhenConfiguringTheFirefoxProfile extends Specification {

    def environmentVariables = new MockEnvironmentVariables()
    WebDriverFactory webDriverFactory

    def setup() {
        webDriverFactory = new WebDriverFactory(environmentVariables)
    }

    def "a firefox instance will not assume untrusted certificates if specifically requested not to"() {
        when: "we don't want firefox to assume untrusted certificates"
            environmentVariables.setProperty(SystemPropertiesConfiguration.REFUSE_UNTRUSTED_CERTIFICATES, "false");
            def profile = webDriverFactory.buildFirefoxProfile()
        then: "the firefox profile will not assume them"
            profile.acceptUntrustedCerts
    }


    def "the firefox profile can be set manually"() {
        given: "we want to provide our own Firefox profile"
            def providedProfile = new FirefoxProfile()
            Serenity.useFirefoxProfile(providedProfile)
        when:
            def usedProfile = webDriverFactory.buildFirefoxProfile()
        then:
            usedProfile == providedProfile
    }


    def "a firefox instance will assume untrusted certificates by default"() {
        when:
            FirefoxProfile profile = webDriverFactory.buildFirefoxProfile()
        then:
            profile.untrustedCertIssuer
    }

    def "should add custom preferences to the profile"() {

        given: "the user provides custom firefox preference values"
            environmentVariables.setProperty("firefox.preferences","browser.download.dir=c:\\downloads")
        when:
            FirefoxProfile profile = webDriverFactory.buildFirefoxProfile()
        then:
            profile.additionalPrefs.allPrefs["browser.download.dir"] == "c:\\downloads"
    }

    def "should convert custom preferences to the correct types"() {

        given: "the user provides custom firefox preference values"
            environmentVariables.setProperty("firefox.preferences",
                "browser.download.folderList=2;browser.download.manager.showWhenStarting=false;browser.download.dir=c:\\downloads")
        when:
            FirefoxProfile profile = webDriverFactory.buildFirefoxProfile()
        then:
            profile.additionalPrefs.allPrefs["browser.download.folderList"] == 2 &&
            profile.additionalPrefs.allPrefs["browser.download.manager.showWhenStarting"] == false

    }


    def "should assume that a preference field without a value is a boolean"() {

        given: "the user provides a boolean firefox preference value"
            environmentVariables.setProperty("firefox.preferences",
                    "app.update.silent")
        when:
            FirefoxProfile profile = webDriverFactory.buildFirefoxProfile()
        then:
            profile.additionalPrefs.allPrefs["app.update.silent"] == true

    }

    def "badly-formed preferences should be ignored"() {

        given: "the user provides a boolean firefox preference value"
            environmentVariables.setProperty("firefox.preferences", "some=illegal=value")
        when:
            FirefoxProfile profile = webDriverFactory.buildFirefoxProfile()
        then:
            profile.additionalPrefs.allPrefs["some=illegal=value"] == null

    }

    def "should accept boolean preference fields in any case"() {

        given: "the user provides a boolean firefox preference value"
            environmentVariables.setProperty("firefox.preferences", "app.update.silent=" +value)
        when:
            FirefoxProfile profile = webDriverFactory.buildFirefoxProfile()
        then:
            profile.additionalPrefs.allPrefs["app.update.silent"] == expectedPreference
        where:
         value  | expectedPreference
         "true" | true
         "TRUE" | true
         "True" | true
         "false"| false
         "FALSE"| false
         "False"| false

    }

}
