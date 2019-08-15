package net.thucydides.core.webdriver.capabilities;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class ChromePreferences {
    private final String prefix;

    private ChromePreferences(String prefix) {
        this.prefix = prefix;
    }

    public static ChromePreferences startingWith(String prefix) {
        return new ChromePreferences(prefix);
    }

    public Map<String, Object> from(EnvironmentVariables environmentVariables) {
        List<String> propertiesWithPrefix =
            environmentVariables.getKeys()
                    .stream()
                    .filter( key -> key.startsWith(prefix))
                    .collect(Collectors.toList());

        Map<String, Object> preferences = new HashMap<>();

        for(String propertyKey : propertiesWithPrefix) {
            String preparedPropertyKey = getPreparedPropertyKey(propertyKey);
            String propertyValue = EnvironmentSpecificConfiguration.from(environmentVariables)
                    .getOptionalProperty(propertyKey)
                    .orElse(null);

            if (isNotEmpty(propertyValue)) {
                preferences.put(preparedPropertyKey, CapabilityValue.asObject(propertyValue));
            }
        }

        return preferences;
    }

//    private Object asObject(String propertyValue) {
//        try {
//            return Integer.parseInt(propertyValue);
//        } catch(NumberFormatException noBiggy) {}
//
//
//        if (propertyValue.equalsIgnoreCase("true") || propertyValue.equalsIgnoreCase("false")) {
//            return Boolean.parseBoolean(propertyValue);
//        }
//
//
//
//        return propertyValue;
//    }

    private String getPreparedPropertyKey(String propertyKey) {
        return propertyKey.replace(prefix,"");
    }


}
