package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;

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

    /**
     * Return true if this fixture should be applied given the current environment variables.
     */
    default boolean isActivated(EnvironmentVariables environmentVariables) {
        return true;
    }
}
