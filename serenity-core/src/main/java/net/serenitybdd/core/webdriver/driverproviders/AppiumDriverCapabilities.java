package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AppiumDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;
    private final String options;

    public AppiumDriverCapabilities(EnvironmentVariables environmentVariables, String options) {
        this.environmentVariables = environmentVariables;
        this.options = options;
    }

    @Override
    public DesiredCapabilities getCapabilities() {
        return AppiumConfiguration.from(environmentVariables).getCapabilities(options);
    }

}
