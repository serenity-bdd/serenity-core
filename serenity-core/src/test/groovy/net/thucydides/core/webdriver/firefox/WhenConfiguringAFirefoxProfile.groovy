package net.thucydides.core.webdriver.firefox

import net.thucydides.core.util.MockEnvironmentVariables
import org.openqa.selenium.firefox.FirefoxProfile
import spock.lang.Specification

class WhenConfiguringAFirefoxProfile extends Specification {
    def environmentVariables = new MockEnvironmentVariables()

    def profileEnhancer = new FirefoxProfileEnhancer(environmentVariables)
    def profile = Mock(FirefoxProfile)

    def "should know when to activate firebugs based on system properties"() {
        when:
            environmentVariables.setProperty("thucydides.activate.firebugs","true")
        then:
            profileEnhancer.shouldActivateFirebugs()
    }

    def "should not activate firebugs by default"() {
        expect:
           !profileEnhancer.shouldActivateFirebugs()
    }

    def "should configure java support if requested"() {
        given:
            environmentVariables.setProperty("security.enable_java","true")
        when:
            profileEnhancer.configureJavaSupport(profile)
        then:
            1 * profile.setPreference("security.enable_java", true)

    }

    def "should add firebugs to profile"() {
        when:
            profileEnhancer.addFirebugsTo(profile)
        then:
            1 * profile.addExtension(_, {it.contains("firebug")});
        and:
            1 * profile.addExtension(_, {it.contains("firefinder")});
    }

    def "should activate firefox proxy"() {
        when:
            profileEnhancer.activateProxy(profile,"http://my.proxy","8080")
        then:
            1 * profile.setPreference("network.proxy.http", "http://my.proxy");
        and:
            1 * profile.setPreference("network.proxy.http_port", "8080");
        and:
            1 * profile.setPreference("network.proxy.type", "1");
    }

    def "should add firefox preferences"() {
        given:
            environmentVariables.setProperty("firefox.preferences","browser.download.dir=c:\\downloads")
        when:
            profileEnhancer.addPreferences(profile)
        then:
            1 * profile.setPreference("browser.download.dir", "c:\\downloads")

    }


}
