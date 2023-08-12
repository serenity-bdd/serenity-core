package net.serenitybdd.plugins.saucelabs;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.RemoteTestName;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.MutableCapabilities;

import java.util.*;

import static net.thucydides.model.ThucydidesSystemProperty.SAUCELABS_TEST_NAME;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class SaucelabsRemoteDriverCapabilities {

    private final EnvironmentVariables environmentVariables;

    private static final Map<String, String> SAUCELABS_BROWSER_NAMES = new HashMap<String, String>() {{
        put("iexplorer", "internet explorer");
        put("edge", "MicrosoftEdge");
    }};

    public SaucelabsRemoteDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    /**
     * Saucelabs capabilities are defined in the saucelabs environment configuration variables, e.g.
     * saucelabs {
     * screenResolution = "800x600"
     * recordScreenshots = false
     * }
     * <p>
     * These are added to the 'sauce:options' capability.
     */
    public MutableCapabilities getCapabilities(MutableCapabilities capabilities) {
//        MutableCapabilities saucelabsCapabilities = saucelabsCapabilitiesDefinedIn(environmentVariables);
//
//        MutableCapabilities w3cCapabilitiesInSaucelabsSection = W3CCapabilities.definedIn(environmentVariables).withPrefix("saucelabs").asDesiredCapabilities();
//
//        configureBrowserAndPlatformIfDefinedInSaucelabsBlock(w3cCapabilitiesInSaucelabsSection, capabilities);
//
//        addBuildNumberTo(saucelabsCapabilities);
//        configureTestName(saucelabsCapabilities);
//
//        capabilities.setCapability("sauce:options", saucelabsCapabilities);
//        capabilities.setJavascriptEnabled(true);

        return capabilities;
    }

    
    private MutableCapabilities saucelabsCapabilitiesDefinedIn(EnvironmentVariables environmentVariables) {
        Properties saucelabsProperties = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix("saucelabs.");
        MutableCapabilities sauceCaps = new MutableCapabilities();

        for (String propertyName : saucelabsProperties.stringPropertyNames()) {
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
        if (StringUtils.isNotBlank(sourceCapabilities.getBrowserName())) {
            String browserName = sourceCapabilities.getBrowserName();
            capabilities.setCapability("browserName", SAUCELABS_BROWSER_NAMES.getOrDefault(browserName, browserName));
        }
        if (StringUtils.isNotBlank(sourceCapabilities.getBrowserVersion())) {
            capabilities.setCapability("browserVersion", sourceCapabilities.getBrowserVersion());
        }
        if (sourceCapabilities.getCapability("platformName") != null && !sourceCapabilities.getCapability("platformName").toString().isEmpty()) {
            capabilities.setCapability("platformName", sourceCapabilities.getCapability("platformName"));
        }
    }

    private void configureTestName(MutableCapabilities capabilities) {
        String testName = SAUCELABS_TEST_NAME.from(environmentVariables);
        if (isNotEmpty(testName)) {
            capabilities.setCapability("name", testName);
        } else {
            Optional<String> guessedTestName;
            Optional<TestOutcome> latestOutcome = StepEventBus.getParallelEventBus().getBaseStepListener().latestTestOutcome();

            guessedTestName = latestOutcome.map(
                    testOutcome -> Optional.of(testOutcome.getStoryTitle() + ": " + testOutcome.getTitle())
            ).orElseGet(RemoteTestName::fromCurrentTest);

            guessedTestName.ifPresent(
                    name -> capabilities.setCapability("name", name)
            );
        }
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace("saucelabs.", "");
    }
}
