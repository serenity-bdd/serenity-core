package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import java.util.Properties;
import java.util.logging.Level;

/**
 * Define WebDriver logging levels using the webdriver.logprefs.* properties, e.g.
 * webdriver.logprefs.driver=ALL
 * webdriver.logprefs.browser=INFO
 */
public class AddLoggingPreferences {
    private final EnvironmentVariables environmentVariables;

    public AddLoggingPreferences(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static AddLoggingPreferences from(EnvironmentVariables environmentVariables) {
        return new AddLoggingPreferences(environmentVariables);
    }

    public void to(MutableCapabilities capabilities) {
        LoggingPreferences logPrefs = new LoggingPreferences();

        Properties logPrefProperties = environmentVariables.getPropertiesWithPrefix("webdriver.logprefs");
        if (!logPrefProperties.isEmpty()) {
            logPrefProperties.forEach((key, value) -> {
                String logType = unprefixed(key.toString()).toLowerCase();
                Level logLevel = Level.parse(value.toString().toUpperCase());
                logPrefs.enable(logType, logLevel);
            });
            if (capabilities instanceof ChromeOptions) {
                capabilities.setCapability(ChromeOptions.LOGGING_PREFS, logPrefs);
            } else if (capabilities instanceof EdgeOptions) {
                capabilities.setCapability(EdgeOptions.LOGGING_PREFS, logPrefs);
            }
        }
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace("webdriver.logprefs.", "");
    }

}
