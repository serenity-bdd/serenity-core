package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;

import java.util.Map;
import java.util.Properties;

import static java.util.stream.Collectors.toMap;

/**
 * CustomCapabilities are use to define both general and browser-specific capabilities for drivers in a generic way.
 * Browser capabilities can be either general (applied to all drivers) or driver-specific. General capabilities have
 * the prefix "driver_capabilities.common.", e.g.
 *
 * driver_capabilities.common.takesScreenshot = false
 *
 * Driver-specific capabilities are prefixed by the name of the driver in lower case, e.g.
 *
 * driver_capabilities.iexplorer.ie.ensureCleanSession = true
 *
 */
public class CustomCapabilities {

    private final static String COMMON_PREFIX = "driver_capabilities.common";

    private final SupportedWebDriver driver;

    private CustomCapabilities(SupportedWebDriver driver) {
        this.driver = driver;
    }

    public Map<String, ?> from(EnvironmentVariables environmentVariables) {
        Map<String, Object> capabilities = mapFrom(environmentVariables.getPropertiesWithPrefix(COMMON_PREFIX));

        Map<String, Object> driverSpecificProperties = mapFrom(environmentVariables.getPropertiesWithPrefix(driverPrefix()));

        capabilities.putAll(driverSpecificProperties);

        return capabilities;
    }

    private Map<String, Object> mapFrom(Properties driverSpecificProperties) {
        return driverSpecificProperties.entrySet()
                .stream()
                .collect(toMap(
                        entry -> stripPrefixFrom(entry.getKey().toString()),
                        entry -> stringOrBooleanFrom(entry.getValue())
//                        entry -> CapabilityValue.fromString(entry.getValue())
                        )
                );
    }

    private String stripPrefixFrom(String key) {
        return key.replace(driverPrefix() + ".", "")
                  .replace(COMMON_PREFIX + ".","");
    }

    private String driverPrefix() {
        return "driver_capabilities." + driver.toString().toLowerCase();
    }
    private static Object stringOrBooleanFrom(Object value) {
        if (value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false")) {
            return Boolean.valueOf(value.toString());
        }
        return value.toString();
    }

    public static CustomCapabilities forDriver(SupportedWebDriver driver) {
        return new CustomCapabilities(driver);
    }

}
