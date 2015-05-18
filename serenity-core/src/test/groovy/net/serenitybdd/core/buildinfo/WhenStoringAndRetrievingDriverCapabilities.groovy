package net.serenitybdd.core.buildinfo

import com.github.goldin.spock.extensions.tempdir.TempDir
import net.thucydides.core.webdriver.Configuration
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Specification

/**
 * Created by john on 12/02/15.
 */
class WhenStoringAndRetrievingDriverCapabilities extends Specification {

    File outputDirectory;

    @Rule
    TemporaryFolder temporaryFolder

    def configuration = Mock(Configuration)

    def setup() {
        outputDirectory = temporaryFolder.newFolder()
        configuration.getOutputDirectory() >> outputDirectory
    }

    def "should store driver capabilities as property files"() {
        given:
            def driverCapabilityRecord = new PropertyBasedDriverCapabilityRecord(configuration)
        when:
            driverCapabilityRecord.registerCapabilities("htmlUnit", DesiredCapabilities.htmlUnit());
            driverCapabilityRecord.registerCapabilities("firefox", DesiredCapabilities.firefox());
        then:
            outputDirectory.list().sort() == ["browser-firefox.properties","browser-htmlunit.properties"]
    }

    def "should read driver capabilities from stored properties files"() {
        given:
            def driverCapabilityRecord = new PropertyBasedDriverCapabilityRecord(configuration)
        and:
            driverCapabilityRecord.registerCapabilities("htmlUnit", DesiredCapabilities.htmlUnit());
            driverCapabilityRecord.registerCapabilities("firefox", DesiredCapabilities.firefox());
        when:
            def drivers = driverCapabilityRecord.drivers
            def capabilities = driverCapabilityRecord.driverCapabilities
        then:
            drivers.sort() == ["firefox","htmlunit"]
        and:
            capabilities["firefox"].getProperty("browserName") == "firefox"
            capabilities["htmlunit"].getProperty("browserName") == "htmlunit"

    }
}
