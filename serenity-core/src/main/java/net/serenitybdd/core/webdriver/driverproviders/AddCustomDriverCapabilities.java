package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

public class AddCustomDriverCapabilities {

    private final EnvironmentVariables environmentVariables;
    private SupportedWebDriver driver;

    private AddCustomDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public AddCustomDriverCapabilities forDriver(SupportedWebDriver driver) {
        this.driver = driver;
        return this;
    }

    public static AddCustomDriverCapabilities from(EnvironmentVariables environmentVariables) {
        return new AddCustomDriverCapabilities(environmentVariables);
    }

    public DesiredCapabilities to(DesiredCapabilities capabilities) {
        Map<String, ?> customCapabilities = CustomCapabilities.forDriver(driver).from(environmentVariables);
        customCapabilities.forEach(
                capabilities::setCapability
        );
        return capabilities;
    }
}
