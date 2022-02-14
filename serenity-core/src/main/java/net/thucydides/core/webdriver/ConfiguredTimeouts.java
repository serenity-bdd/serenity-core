package net.thucydides.core.webdriver;

import net.serenitybdd.core.pages.DefaultTimeouts;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

import java.time.Duration;

public class ConfiguredTimeouts {
    public static Duration implicitWait() {
        EnvironmentVariables environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
        int configuredWaitForTimeoutInMilliseconds =
                ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT
                        .integerFrom(environmentVariables, (int) DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT.toMillis());
        return Duration.ofMillis(configuredWaitForTimeoutInMilliseconds);
    }
}
