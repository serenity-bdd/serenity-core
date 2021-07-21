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

import static net.thucydides.core.ThucydidesSystemProperty.*;
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
        return environmentVariables.injectSystemPropertiesInto(
                EnvironmentSpecificConfiguration.from(environmentVariables).getOptionalProperty(ThucydidesSystemProperty.SAUCELABS_URL)
                                                                           .orElseThrow(() -> new SaucelabsConfigurationException("The saucelabs.url variable needs to be defined to use Saucelabs"))
        );
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

        MutableCapabilities saucelabsCapabilities = saucelabsCapabilitiesDefinedIn(environmentVariables);

        MutableCapabilities w3cCapabilitiesInSaucelabsSection = W3CCapabilities.definedIn(environmentVariables).withPrefix("saucelabs");

        configureBrowserAndPlatformIfDefinedInSaucelabsBlock(w3cCapabilitiesInSaucelabsSection, capabilities);

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


    private void configureBrowserAndPlatformIfDefinedInSaucelabsBlock(MutableCapabilities sourceCapabilities, MutableCapabilities capabilities) {
        if (sourceCapabilities.getBrowserName() != null) {
            capabilities.setCapability("browserName", sourceCapabilities.getBrowserName());
        }
        if (sourceCapabilities.getVersion() != null) {
            capabilities.setCapability("browserVersion", sourceCapabilities.getVersion());
        }
        if (sourceCapabilities.getCapability("platformName") != null) {
            capabilities.setCapability("platformName", sourceCapabilities.getCapability("platformName"));
        }
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
