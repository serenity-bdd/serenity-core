package net.thucydides.core.webdriver.capabilities;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityValue;

import java.util.*;

/**
 * Read browser preferences from a part of the serenity.conf file via the environment variables.
 */
public class BrowserPreferences {
    private final String prefix;

    private BrowserPreferences(String prefix) {
        this.prefix = prefix;
    }

    public static BrowserPreferences startingWith(String prefix) {
        return new BrowserPreferences(prefix);
    }

    public Map<String, Object> from(EnvironmentVariables environmentVariables) {

        Properties chromePrefs = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(prefix);
        Map<String, Object> preferences = new HashMap<>();

        for(String propertyName : chromePrefs.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(prefix,propertyName);
            Object propertyValue = CapabilityValue.asObject(chromePrefs.getProperty(propertyName));
            preferences.put(unprefixedPropertyName, propertyValue);
        }
        return preferences;
    }

    private String unprefixed(String prefix, String propertyName) {
        return propertyName.replace(prefix,"");
    }
}
