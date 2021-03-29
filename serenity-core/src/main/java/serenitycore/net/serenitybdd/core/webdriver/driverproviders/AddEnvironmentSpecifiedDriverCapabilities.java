package serenitycore.net.serenitybdd.core.webdriver.driverproviders;

import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.SupportedWebDriver;
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

    public DesiredCapabilities to(DesiredCapabilities capabilities) {
        Map<String, ?> customCapabilities = CustomCapabilities.forDriver(driver).from(environmentVariables);
        customCapabilities.forEach(
                capabilities::setCapability
        );
        return capabilities;
    }
}
