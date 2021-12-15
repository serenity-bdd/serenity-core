package net.serenitybdd.core.webdriver.driverproviders;

import com.vladsch.flexmark.util.Mutable;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Properties;
import java.util.logging.Level;

/**
 * Define WebDriver logging levels using the webdriver.logprefs.* properties, e.g.
 *  webdriver.logprefs.driver=ALL
 *  webdriver.logprefs.browser=INFO
 */
public class AddLoggingPreferences {
    private EnvironmentVariables environmentVariables;

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
            logPrefProperties.entrySet().forEach(
                    (entry) -> {
                        String logType = unprefixed(entry.getKey().toString()).toLowerCase();
                        Level logLevel = Level.parse(entry.getValue().toString().toUpperCase());
                        logPrefs.enable(logType, logLevel);
                    }
            );
            capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        }
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace("webdriver.logprefs.","");
    }

}
