package net.thucydides.core.webdriver.edge

import net.serenitybdd.core.webdriver.driverproviders.EdgeDriverCapabilities
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenConfiguringEdgeOptions extends Specification {
    def environmentVariables = new MockEnvironmentVariables()

    def "should add Edge-specific options"() {
        given:

        def edgeOptions = """["headless", 
                              "start-maximized", 
                              # A comment
                              "window-size=100,100"
                              "disable-gpu"
                              ]
        """
            environmentVariables.setProperty("edge.args", edgeOptions)
            environmentVariables.setProperty("edge.preferences.download.default_directory", "some-directory")

            EdgeDriverCapabilities capabilities = new EdgeDriverCapabilities(environmentVariables)
        when:
            def desiredCapabilities = capabilities.getCapabilities()
        then:
            desiredCapabilities.getCapability("ms:edgeOptions")["args"] == ["headless", "start-maximized", "window-size=100,100", "disable-gpu"]
            desiredCapabilities.getCapability("ms:edgeOptions")["prefs"]["download.default_directory"] == "some-directory"
    }
}
