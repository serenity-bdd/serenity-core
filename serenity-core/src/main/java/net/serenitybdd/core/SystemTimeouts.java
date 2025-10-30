package net.serenitybdd.core;

import net.serenitybdd.core.pages.DefaultTimeouts;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;

import java.util.Optional;

public class SystemTimeouts {
    private final EnvironmentVariables environmentVariables;

    public SystemTimeouts(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static SystemTimeouts forTheCurrentTest() {
        return new SystemTimeouts(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public long getImplicitTimeout() {
        Optional<Long> configuredTimeout = webdriverCapabilitiesImplicitTimeoutFrom(environmentVariables);
        return configuredTimeout.orElse(DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT.toMillis());
    }

    public long getWaitForTimeout() {
        return ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT.longFrom(environmentVariables, DefaultTimeouts.DEFAULT_WAIT_FOR_TIMEOUT.toMillis());
    }

    public static Optional<Long> webdriverCapabilitiesImplicitTimeoutFrom(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration
                .from(environmentVariables)
                .getOptionalProperty("webdriver.capabilities.timeouts.implicit","webdriver.timeouts.implicitlywait")
                .map(Long::parseLong);
    }
}
