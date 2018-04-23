package net.thucydides.core.webdriver.capabilities;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Optional;
import java.util.Properties;

import static net.thucydides.core.ThucydidesSystemProperty.SAUCELABS_TEST_NAME;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Provide Sauce Labs specific capabilities
 *
 * @author Imran Khan
 */
public class SaucelabsRemoteDriverCapabilities implements RemoteDriverCapabilities {

    private final EnvironmentVariables environmentVariables;

    public SaucelabsRemoteDriverCapabilities(EnvironmentVariables environmentVariables){
        this.environmentVariables = environmentVariables;
    }

    @Override
    public String getUrl() {
        return environmentVariables.injectSystemPropertiesInto(ThucydidesSystemProperty.SAUCELABS_URL.from(environmentVariables));
    }

    @Override
    public DesiredCapabilities getCapabilities(DesiredCapabilities capabilities) {


        configureBrowserVersion(capabilities);
        configureTargetPlatform(capabilities);

        Properties saucelabsProperties = environmentVariables.getPropertiesWithPrefix("saucelabs.");

        for(String propertyName : saucelabsProperties.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(propertyName);
            capabilities.setCapability(propertyName, typed(saucelabsProperties.getProperty(propertyName)));
            capabilities.setCapability(unprefixedPropertyName, typed(saucelabsProperties.getProperty(propertyName)));
        }

        addBuildNumberTo(capabilities);

        configureTestName(capabilities);

        capabilities.setJavascriptEnabled(true);

        return capabilities;
    }

    private void addBuildNumberTo(DesiredCapabilities capabilities) {
        if (environmentVariables.getProperty("BUILD_NUMBER") != null) {
            capabilities.setCapability("build", environmentVariables.getProperty("BUILD_NUMBER"));
        }
    }


    private void configureBrowserVersion(DesiredCapabilities capabilities) {
        String driverVersion = ThucydidesSystemProperty.SAUCELABS_DRIVER_VERSION.from(environmentVariables);
        if (isNotEmpty(driverVersion)) {
            capabilities.setCapability("version", driverVersion);
        }
    }

    private void configureTargetPlatform(DesiredCapabilities capabilities) {
        SetAppropriateSaucelabsPlatformVersion.inCapabilities(capabilities).from(environmentVariables);

        String remotePlatform = environmentVariables.getProperty("remote.platform");
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

    private void configureTestName(DesiredCapabilities capabilities) {
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
