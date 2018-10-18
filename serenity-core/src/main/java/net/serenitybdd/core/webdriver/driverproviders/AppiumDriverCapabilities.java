package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import org.openqa.selenium.MutableCapabilities;

public class AppiumDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;
    private final String options;

    public AppiumDriverCapabilities(EnvironmentVariables environmentVariables, String options) {
        this.environmentVariables = environmentVariables;
        this.options = options;
    }

    @Override
    public MutableCapabilities getCapabilities() {
        return AppiumConfiguration.from(environmentVariables).getCapabilities(options);
    }

}
