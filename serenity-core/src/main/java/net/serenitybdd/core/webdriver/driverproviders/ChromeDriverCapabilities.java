package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;

public class ChromeDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;
    private final String driverOptions;

    public ChromeDriverCapabilities(EnvironmentVariables environmentVariables, String driverOptions) {
        this.environmentVariables = environmentVariables;
        this.driverOptions = driverOptions;
    }

    @Override
    public MutableCapabilities getCapabilities() {
        MutableCapabilities capabilities = W3CCapabilities.definedIn(environmentVariables).withPrefix("webdriver.capabilities").asDesiredCapabilities();
        SetProxyConfiguration.from(environmentVariables).in(capabilities);
        return capabilities;
    }
}
