package net.thucydides.core.webdriver.capabilities;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityValue;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class W3CCapabilities {

    private final EnvironmentVariables environmentVariables;

    public W3CCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static W3CCapabilities definedIn(EnvironmentVariables environmentVariables) {
        return new W3CCapabilities(environmentVariables);
    }

    public DesiredCapabilities withPrefix(String prefix) {
        Properties w3cProperties = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(prefix);

        w3cProperties = deleteNonW3CPropertiesIn(w3cProperties, prefix);
        String browser = w3cProperties.getProperty(prefix + "." + "browserName");
        String version = w3cProperties.getProperty(prefix + "." + "browserVersion");
        String platformName = w3cProperties.getProperty(prefix + "." + "platformName");

        DesiredCapabilities capabilities = new DesiredCapabilities();

        if (browser != null) {
            capabilities.setBrowserName(browser);
        }
        if (version != null) {
            capabilities.setVersion(version);
        }
        if (platformName != null) {
            capabilities.setCapability("platformName", platformName);
            try {
                capabilities.setPlatform(Platform.fromString(platformName));
            } catch (WebDriverException unknownPlatformValueSoLetsSetItAsAStringAndHopeForTheBest) {}
        }

        for(String propertyName : w3cProperties.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(prefix,propertyName);
            Object propertyValue = CapabilityValue.asObject(w3cProperties.getProperty(propertyName));
            capabilities.setCapability(unprefixedPropertyName, typedPropertyFrom(unprefixedPropertyName, propertyValue));
        }
        return capabilities;
    }


    private final static List<String> EXCLUDED_PROPERTIES = Arrays.stream(ThucydidesSystemProperty.values())
            .filter(prop -> prop.getPropertyName().startsWith("webdriver."))
            .map(ThucydidesSystemProperty::getPropertyName)
            .collect(Collectors.toList());

    private Properties deleteNonW3CPropertiesIn(Properties properties, String prefix) {
        Properties filteredProperties = new Properties();

        properties.forEach(
                (key, value) -> {
                    if (!EXCLUDED_PROPERTIES.contains(key)) {
                        filteredProperties.put(key, value);
                    }
                }
        );
        return filteredProperties;
    }

    private Object typedPropertyFrom(String propertyName, Object value) {
        if (propertyName.equals("proxy") && value instanceof Map) {
            return new Proxy((Map)value);
        }
        return value;
    }

    private String unprefixed(String prefix, String propertyName) {
        return propertyName.replace(prefix + ".","");
    }


}
