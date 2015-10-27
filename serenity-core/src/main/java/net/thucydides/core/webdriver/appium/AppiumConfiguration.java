package net.thucydides.core.webdriver.appium;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.PathProcessor;
import net.thucydides.core.webdriver.MobilePlatform;
import net.thucydides.core.webdriver.ThucydidesConfigurationException;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import static ch.lambdaj.Lambda.filter;
import static org.hamcrest.CoreMatchers.startsWith;

/**
 * Created by john on 28/10/2014.
 */
public class AppiumConfiguration {

    private final String DEFAULT_URL = "http://127.0.0.1:4723/wd/hub";
    private final EnvironmentVariables environmentVariables;

    public AppiumConfiguration(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public static AppiumConfiguration from(EnvironmentVariables environmentVariables) {
        return new AppiumConfiguration(environmentVariables);
    }

    public MobilePlatform getTargetPlatform() {
        String targetPlatform = environmentVariables.getProperty("appium.platformName", "UNDEFINED");
        MobilePlatform platform = null;
        try {
            platform = MobilePlatform.valueOf(targetPlatform.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ThucydidesConfigurationException("The appium.platformName needs to be specified (either IOS or ANDROID)");
        }
        return platform;
    }

    public URL getUrl() {
        String url = environmentVariables.getProperty("appium.hub", DEFAULT_URL);
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new ThucydidesConfigurationException("The appium.hub URL needs to be specified");
        }
    }

    public DesiredCapabilities getCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        Properties appiumProperties = getProperties();
        for (Object key : appiumProperties.keySet()) {
            capabilities.setCapability(key.toString(), appiumProperties.getProperty(key.toString()));
            capabilities.asMap();
        }
        return capabilities;
    }

    public Properties getProperties() {
        return appiumPropertiesFrom(environmentVariables);
    }

    private Properties appiumPropertiesFrom(EnvironmentVariables environmentVariables) {
        Properties appiumProperties = new Properties();
        List<String> appiumKeys = filter(startsWith("appium."), environmentVariables.getKeys());
        for (String key : appiumKeys) {
            String value = isAppProperty(key) ? appPathFrom(environmentVariables.getProperty(key)) : environmentVariables.getProperty(key);
            String simplifiedKey = key.replace("appium.", "");
            appiumProperties.setProperty(simplifiedKey, value.trim());
        }
        ensureAppPathDefinedIn(appiumProperties);
        return appiumProperties;
    }

    private void ensureAppPathDefinedIn(Properties appiumProperties) {
        if (!appiumProperties.containsKey("app")) {
            throw new ThucydidesConfigurationException("The path to the app needs to be provided in the appium.app property.");
        }
    }

    private String appPathFrom(String propertyValue) {
        return new PathProcessor().normalize(propertyValue);
    }

    private boolean isAppProperty(String key) {
        return key.equals("appium.app");
    }
}
