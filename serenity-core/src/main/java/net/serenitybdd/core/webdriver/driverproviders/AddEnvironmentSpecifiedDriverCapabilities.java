package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

public class AddEnvironmentSpecifiedDriverCapabilities {

    private final EnvironmentVariables environmentVariables;
    private SupportedWebDriver driver;

    private AddEnvironmentSpecifiedDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public AddEnvironmentSpecifiedDriverCapabilities forDriver(SupportedWebDriver driver) {
        this.driver = driver;
        return this;
    }

    public static AddEnvironmentSpecifiedDriverCapabilities from(EnvironmentVariables environmentVariables) {
        return new AddEnvironmentSpecifiedDriverCapabilities(environmentVariables);
    }

    public MutableCapabilities to(MutableCapabilities capabilities) {
        Map<String, ?> customCapabilities = CustomCapabilities.forDriver(driver).from(environmentVariables);
        customCapabilities.forEach(capabilities::setCapability);
        return capabilities;
    }
}
