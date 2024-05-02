package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

/**
 * Implement this interface to add your own custom actions after a WebDriver scenario.
 * Use the serenity.extension.packages property to define the package or packages to scan for your implementations.
 * These classes will be invoked after a WebDriver scenario is completed.
 */
public interface AfterAWebdriverScenario {
    void apply(EnvironmentVariables environmentVariables,
               TestOutcome testOutcome,
               WebDriver driver);

    default boolean isActivated(EnvironmentVariables environmentVariables) { return true; }
}
