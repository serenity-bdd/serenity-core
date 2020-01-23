package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface BeforeAWebdriverScenario {
    DesiredCapabilities apply(EnvironmentVariables environmentVariables,
                              SupportedWebDriver driver,
                              TestOutcome testOutcome,
                              DesiredCapabilities capabilities);
}
