package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Implement this interface to add your own custom capabilities during driver creation.
 * Use the serenity.extension.packages property to define the package or packages to scan for your implementations.
 * These classes will be invoked before a driver is created.
 */
public interface BeforeAWebdriverScenario {
    MutableCapabilities apply(EnvironmentVariables environmentVariables,
                              SupportedWebDriver driver,
                              TestOutcome testOutcome,
                              MutableCapabilities capabilities);

    default boolean isActivated(EnvironmentVariables environmentVariables) { return true; }
}
