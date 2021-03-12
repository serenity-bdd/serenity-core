package net.thucydides.core.webdriver.edge

import net.serenitybdd.core.webdriver.driverproviders.EdgeDriverCapabilities
import net.thucydides.core.util.MockEnvironmentVariables
import spock.lang.Specification

class WhenConfiguringEdgeOptions extends Specification {
    def environmentVariables = new MockEnvironmentVariables()

    def "should add Edge-specific options"() {
        given:

        def edgeOptions = """
        {"args": ["headless", "start-maximized", "disable-gpu"]}
        """
            environmentVariables.setProperty("edge.options", edgeOptions)
        EdgeDriverCapabilities capabilities = new EdgeDriverCapabilities(environmentVariables)
        when:
            def desiredCapabilities = capabilities.getCapabilities()
        then:
            desiredCapabilities.getCapability("ms:edgeOptions")["args"] == ["headless", "start-maximized", "disable-gpu"]
    }
}
