package net.serenitybdd.screenplay.playwright;

import net.thucydides.model.util.EnvironmentVariables;

import java.util.Optional;

public enum PlayWrightConfigurationProperties {

    BROWSER_TYPE("playwright.browsertype"),

    /**
     * Whether to run browser in headless mode. Defaults to {@code true}.
     */
    HEADLESS("playwright.headless"),

    /**
     * Enable Playwright tracing for debugging with the trace viewer.
     */
    TRACING("playwright.tracing"),

    /**
     * Browser channel to use (e.g. "chrome", "msedge").
     */
    CHANNEL("playwright.channel"),

    /**
     * Base URL used to construct full URLs via {@code page.navigate("/path")}.
     */
    BASE_URL("playwright.baseurl"),

    /**
     * Slows down Playwright operations by the specified amount of milliseconds.
     * Useful for debugging.
     */
    SLOW_MO("playwright.slowmo");

    private final String property;

    PlayWrightConfigurationProperties(String property) {
        this.property = property;
    }

    public Optional<String> asStringFrom(EnvironmentVariables environmentVariables) {
        return environmentVariables.optionalProperty(property);
    }

    public Optional<Boolean> asBooleanFrom(EnvironmentVariables environmentVariables) {
        return environmentVariables.optionalProperty(property).map(Boolean::valueOf);
    }

    public Optional<Double> asDoubleFrom(EnvironmentVariables environmentVariables) {
        return environmentVariables.optionalProperty(property).map(Double::valueOf);
    }

}
