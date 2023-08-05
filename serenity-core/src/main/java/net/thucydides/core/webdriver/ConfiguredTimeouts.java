package net.thucydides.core.webdriver;

import net.serenitybdd.core.SystemTimeouts;
import net.serenitybdd.core.pages.DefaultTimeouts;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

import java.time.Duration;

public class ConfiguredTimeouts {
    public static Duration implicitWait() {
        EnvironmentVariables environmentVariables = SystemEnvironmentVariables.currentEnvironmentVariables();
        long configuredWaitForTimeoutInMilliseconds = new SystemTimeouts(environmentVariables).getImplicitTimeout();
        return Duration.ofMillis(configuredWaitForTimeoutInMilliseconds);
    }
}
