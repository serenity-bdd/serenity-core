package net.serenitybdd.core.webdriver.configuration;

import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public enum RestartBrowserForEach {
    NEVER(0), FEATURE(1), STORY(1), SCENARIO(2), EXAMPLE(3);

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RestartBrowserForEach.class);

    RestartBrowserForEach(int order) {
        this.order = order;
    }

    private final int order;

    public boolean restartBrowserForANew(RestartBrowserForEach restartLevel) {
        return this.order >= restartLevel.order;
    }

    public static RestartBrowserForEach configuredIn(EnvironmentVariables environmentVariables) {
        String configuredLevel = ThucydidesSystemProperty.SERENITY_RESTART_BROWSER_FOR_EACH.from(environmentVariables,EXAMPLE.name()).toUpperCase();

        try {
            return RestartBrowserForEach.valueOf(configuredLevel);
        } catch (IllegalArgumentException invalidConfiguredValue) {
            LOGGER.warn("Invalid value `{}` for restart.browser.for.each: should be one of {}",
                    configuredLevel,
                    Arrays.toString(values()));
            return EXAMPLE;
        }
    }
}
