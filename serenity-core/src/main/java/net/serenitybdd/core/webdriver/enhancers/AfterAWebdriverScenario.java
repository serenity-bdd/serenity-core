package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public interface AfterAWebdriverScenario {
    void apply(EnvironmentVariables environmentVariables,
               TestOutcome testOutcome,
               WebDriver driver);

    default boolean isActivated(EnvironmentVariables environmentVariables) { return true; }
}
