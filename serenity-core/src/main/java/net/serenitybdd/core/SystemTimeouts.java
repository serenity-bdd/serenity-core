package net.serenitybdd.core;

import net.serenitybdd.core.pages.DefaultTimeouts;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;

public class SystemTimeouts {
    private EnvironmentVariables environmentVariables;

    public SystemTimeouts(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static SystemTimeouts forTheCurrentTest() {
        return new SystemTimeouts(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public long getImplicitTimeout() {
        return ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.longFrom(environmentVariables, DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT.toMillis());
    }

    public long getWaitForTimeout() {
        return ThucydidesSystemProperty.WEBDRIVER_WAIT_FOR_TIMEOUT.longFrom(environmentVariables, DefaultTimeouts.DEFAULT_WAIT_FOR_TIMEOUT.toMillis());
    }

}
