package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ProvidedDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public ProvidedDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.merge(W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities());
        return capabilities;
    }

}
