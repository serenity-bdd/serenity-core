package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public interface AfterAWebdriverScenario {
    void apply(EnvironmentVariables environmentVariables,
               TestOutcome testOutcome,
               WebDriver driver);
}
