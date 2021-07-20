package net.thucydides.core.webdriver.capabilities;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Optional;
import java.util.Properties;

import static net.thucydides.core.ThucydidesSystemProperty.REMOTE_PLATFORM;
import static net.thucydides.core.ThucydidesSystemProperty.SAUCELABS_TEST_NAME;
import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Provide Sauce Labs specific capabilities
 */
public class SaucelabsRemoteDriverCapabilities implements RemoteDriverCapabilities {

    private final EnvironmentVariables environmentVariables;

    public SaucelabsRemoteDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    public static String getSaucelabsDriverFrom(EnvironmentVariables environmentVariables) {
        return EnvironmentSpecificConfiguration
                .from(environmentVariables)
                .getOptionalProperty(ThucydidesSystemProperty.SAUCELABS_BROWSERNAME)
                .orElse(getDriverFrom(environmentVariables));
    }

    /**
     * The Saucelabs URL, specified in the `saucelabs.url` property.
     * @return
     */
    @Override
    public String getUrl() {
        return environmentVariables.injectSystemPropertiesInto(ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables));
    }

    /**
     * Saucelabs capabilities are defined in the saucelabs environment configuration variables, e.g.
     * saucelabs {
     *     screenResolution = "800x600"
     *     recordScreenshots = false
     * }
     *
     * These are added to the 'sauce:options' capability.
     */
    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {

        configureBrowserVersion(capabilities);
        configureTargetPlatform(capabilities);

        MutableCapabilities saucelabsCapabilities = saucelabsCapabilitiesDefinedIn(environmentVariables);

        addBuildNumberTo(saucelabsCapabilities);
        configureTestName(saucelabsCapabilities);

        capabilities.setCapability("sauce:options", saucelabsCapabilities);
        capabilities.setJavascriptEnabled(true);

        return capabilities;
    }

    @NotNull
    private MutableCapabilities saucelabsCapabilitiesDefinedIn(EnvironmentVariables environmentVariables) {
        Properties saucelabsProperties = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix("saucelabs.");
        MutableCapabilities sauceCaps = new MutableCapabilities();

        for(String propertyName : saucelabsProperties.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(propertyName);
            sauceCaps.setCapability(unprefixedPropertyName, saucelabsProperties.getProperty(propertyName));
        }
        return sauceCaps;
    }

    private void addBuildNumberTo(MutableCapabilities capabilities) {

        if (environmentVariables.getProperty("BUILD_NUMBER") != null) {
            capabilities.setCapability("build", environmentVariables.getProperty("BUILD_NUMBER"));
        }
    }


    private void configureBrowserVersion(MutableCapabilities capabilities) {
        String browserVersion = ThucydidesSystemProperty.SAUCELABS_BROWSER_VERSION.from(environmentVariables);
        if (isNotEmpty(browserVersion)) {
            capabilities.setCapability("browserVersion", browserVersion);
        }
    }

    private void configureTargetPlatform(DesiredCapabilities capabilities) {
        SetAppropriateSaucelabsPlatformVersion.inCapabilities(capabilities).from(environmentVariables);

        String remotePlatform = REMOTE_PLATFORM.from(environmentVariables);

        if (isNotEmpty(remotePlatform)) {
            capabilities.setPlatform(Platform.valueOf(remotePlatform));
        }

    }

    private Object typed(String value) {
        if (isABoolean(value)) {
            return Boolean.parseBoolean(value);
        }
        if (isAnInteger(value)) {
            return Integer.parseInt(value);
        }
        return value;
    }

    private boolean isAnInteger(String value) {
        return StringUtils.isNumeric(value);
    }

    private boolean isABoolean(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false") ;
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace("saucelabs.","");
    }

    private void configureTestName(MutableCapabilities capabilities) {
        String testName = SAUCELABS_TEST_NAME.from(environmentVariables);
        if (isNotEmpty(testName)) {
            capabilities.setCapability("name", testName);
        } else {
            Optional<String> guessedTestName = RemoteTestName.fromCurrentTest();
            guessedTestName.ifPresent(
                    name -> capabilities.setCapability("name", name)
            );
        }
    }
}
