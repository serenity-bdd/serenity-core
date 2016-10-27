package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import org.openqa.selenium.remote.DesiredCapabilities;

public class AppiumDriverCapabilities implements DriverCapabilitiesProvider {

    private final EnvironmentVariables environmentVariables;

    public AppiumDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    @Override
    public DesiredCapabilities getCapabilities() {
        return AppiumConfiguration.from(environmentVariables).getCapabilities();
    }

}
