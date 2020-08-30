package net.thucydides.core.webdriver.firefox

import net.serenitybdd.core.webdriver.driverproviders.FirefoxDriverCapabilities
import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.firefox.FirefoxProfile
import spock.lang.Specification

class WhenConfiguringAFirefoxProfile extends Specification {
    def environmentVariables = new MockEnvironmentVariables()

    def profileEnhancer = new FirefoxProfileEnhancer(environmentVariables)
    def profile = Mock(FirefoxProfile)

    def "should configure java support if requested"() {
        given:
            environmentVariables.setProperty("security.enable_java","true")
        when:
            profileEnhancer.configureJavaSupport(profile)
        then:
            1 * profile.setPreference("security.enable_java", true)

    }

    def "should activate firefox proxy"() {
        when:
            profileEnhancer.activateProxy(profile,"http://my.proxy","8080")
        then:
            1 * profile.setPreference("network.proxy.http", "http://my.proxy");
        and:
            1 * profile.setPreference("network.proxy.http_port", 8080);
        and:
            1 * profile.setPreference("network.proxy.type", 1);
    }

    def "should add firefox preferences"() {
        given:
            environmentVariables.setProperty("firefox.preferences","browser.download.dir=c:\\downloads")
        when:
            profileEnhancer.addPreferences(profile)
        then:
            1 * profile.setPreference("browser.download.dir", "c:\\downloads")

    }

    def "should add complex user agent preferences to the profile"() {

        given: "the user provides a user agent firefox preference values"
            environmentVariables.setProperty("firefox.preferences","general.useragent.override=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36&browser.download.dir=c:\\downloads")
            environmentVariables.setProperty("firefox.preference.separator","&")
        when:
            profileEnhancer.addPreferences(profile)
        then:
            1 * profile.setPreference("browser.download.dir","c:\\downloads")
        and:
            1 * profile.setPreference("general.useragent.override","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36")
    }



    def "should add Firefox-specific options"() {
        given:

        def firefoxOptions = """
        {"binary": "/Applications/Firefox-49.1.app/Contents/MacOS/firefox-bin","log":{"level":"debug"}}
        """
            environmentVariables.setProperty("gecko.firefox.options",firefoxOptions)
            FirefoxDriverCapabilities capabilities = new FirefoxDriverCapabilities(environmentVariables)
        when:
            def desiredCapabilities = capabilities.getCapabilities()
        then:
            desiredCapabilities.getCapability("moz:firefoxOptions")["binary"] == "/Applications/Firefox-49.1.app/Contents/MacOS/firefox-bin"
        and:
            desiredCapabilities.getCapability("moz:firefoxOptions")["log"]["level"] == "debug"
    }



}
