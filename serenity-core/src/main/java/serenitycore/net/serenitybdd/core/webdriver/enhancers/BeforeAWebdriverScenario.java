package serenitycore.net.serenitybdd.core.webdriver.enhancers;

import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface BeforeAWebdriverScenario {
    DesiredCapabilities apply(EnvironmentVariables environmentVariables,
                              SupportedWebDriver driver,
                              TestOutcome testOutcome,
                              DesiredCapabilities capabilities);
}
