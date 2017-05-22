package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.xpath.operations.Bool;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.*;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.hamcrest.Matchers.startsWith;

public class ChromePreferences {
    private final String prefix;

    public ChromePreferences(String prefix) {
        this.prefix = prefix;
    }

    public static ChromePreferences startingWith(String prefix) {
        return new ChromePreferences(prefix);
    }

    public Map<String, Object> from(EnvironmentVariables environmentVariables) {
        List<String> propertiesWithPrefix = filter(having(on(String.class), startsWith(prefix)), environmentVariables.getKeys());

        Map<String, Object> preferences = new HashMap<>();


        for(String propertyKey : propertiesWithPrefix) {
            String preparedPropertyKey = getPreparedPropertyKey(propertyKey);
            String propertyValue = environmentVariables.getProperty(propertyKey);
            if (isNotEmpty(propertyValue)) {
                preferences.put(preparedPropertyKey, asObject(propertyValue));
            }
        }

        return preferences;
    }

    private Object asObject(String propertyValue) {
        try {
            Integer integerValue = Integer.parseInt(propertyValue);
            return integerValue;
        } catch(NumberFormatException noBiggy) {}


        if (propertyValue.equalsIgnoreCase("true") || propertyValue.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(propertyValue);
        }

        return propertyValue;
    }

    private String getPreparedPropertyKey(String propertyKey) {
        return propertyKey.replace(prefix + ".","");
    }


}
