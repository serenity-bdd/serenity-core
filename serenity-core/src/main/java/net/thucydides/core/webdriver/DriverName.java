package net.thucydides.core.webdriver;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;

public class DriverName {
    private final EnvironmentVariables environmentVariables;

    public DriverName(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public String normalisedFormOf(String name) {
        if (StringUtils.isEmpty(name)) {
            return WebDriverFactory.DEFAULT_DRIVER;
        }

        if (!differentBrowserForEachActor()) {
            return coreDriverNameFrom(name);
        }

        return name.toLowerCase();
    }

    private String coreDriverNameFrom(String name) {
        return name.contains(":") ? name.substring(0, name.indexOf(":")).toLowerCase() : name.toLowerCase();
    }

    private Boolean differentBrowserForEachActor() {
        return ThucydidesSystemProperty.SERENITY_DIFFERENT_BROWSER_FOR_EACH_ACTOR.booleanFrom(environmentVariables, true);
    }
}
