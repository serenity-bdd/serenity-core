package net.serenitybdd.saucelabs;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

import static net.thucydides.core.ThucydidesSystemProperty.REMOTE_PLATFORM;
import static net.thucydides.core.ThucydidesSystemProperty.SAUCELABS_BROWSERNAME;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class BeforeASauceLabsScenario implements BeforeAWebdriverScenario {

    private static final String SAUCELABS = "saucelabs.";

    private static final List<String> NON_SAUCE_PROPERTIES = Arrays.asList(
            "browserName",
            "browserVersion",
            "platformName",
            "platformVersion",
            "datacenter",
            "url"
    );

    private static final Map<String, String> SAUCELABS_BROWSER_NAMES = new HashMap<String, String>() {{
        put("iexplorer", "internet explorer");
        put("edge", "MicrosoftEdge");
    }};

    @Override
    public DesiredCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     DesiredCapabilities capabilities) {

        // Skip setting up capabilities if it's not SauceLabs execution
        if (!ThucydidesSystemProperty.SAUCELABS_URL.isDefinedIn(environmentVariables)) {
            return capabilities;
        }

        Properties sauceLabsProperties = EnvironmentSpecificConfiguration
                .from(environmentVariables)
                .getPropertiesWithPrefix(SAUCELABS);

        Properties sauceLabsPropertiesWithOverrides = caterForOverridesIn(sauceLabsProperties);
        OverrideDriverCapabilities.getProperties()
                .forEach((key, value) -> sauceLabsPropertiesWithOverrides.setProperty(key, value.toString()));

        setNonW3CCapabilities(capabilities, sauceLabsPropertiesWithOverrides);

        Map<String, Object> saucelabsOptions = w3CPropertyMapFrom(sauceLabsPropertiesWithOverrides);

        String testName = testOutcome.getStoryTitle() + " - " + testOutcome.getTitle();
        saucelabsOptions.put("name", testName);

        capabilities.setCapability("sauce:options", saucelabsOptions);

        configureTargetPlatform(capabilities, environmentVariables);
        configureTargetBrowser(capabilities, environmentVariables);

        return capabilities;
    }

    private Properties caterForOverridesIn(Properties sauceLabsProperties) {
        Properties propertiesWithOverrides = new Properties();
        sauceLabsProperties
                .stringPropertyNames()
                .stream()
                .filter(this::shouldNotOverride)
                .forEach(
                        name -> propertiesWithOverrides.put(name, sauceLabsProperties.getProperty(name))
                );

        return propertiesWithOverrides;
    }

    private void setNonW3CCapabilities(DesiredCapabilities capabilities, Properties sauceLabsProperties) {
        sauceLabsProperties.stringPropertyNames()
                .stream()
                .filter(this::isNonW3CProperty)
                .forEach(
                        key -> capabilities.setCapability(this.unprefixed(key), sauceLabsProperties.getProperty(key))
                );
    }

    private boolean shouldNotOverride(String propertyName) {
        return !OverrideDriverCapabilities.shouldOverrideDefaults();
    }

    private Map<String, Object> w3CPropertyMapFrom(Properties properties) {
        Map<String, Object> w3cOptions = new HashMap<>();
        Map<String, Map<String, Object>> nestedOptions = new HashMap<>();

        properties.stringPropertyNames()
                .stream()
                .filter(this::isW3CProperty)
                .forEach(
                        key -> {
                            String unprefixedKey = unprefixed(key);
                            if (unprefixedKey.contains(".")) {
                                String parentKey = StringUtils.split(unprefixedKey, ".")[0];
                                String childKey = StringUtils.split(unprefixedKey, ".")[1];
                                Map<String, Object> nestedProperties = nestedOptions.getOrDefault(parentKey, new HashMap<>());
                                nestedProperties.put(childKey, properties.getProperty(key));
                                nestedOptions.put(parentKey, nestedProperties);
                            } else {
                                w3cOptions.put(unprefixedKey, properties.getProperty(key));
                            }
                        }
                );
        w3cOptions.putAll(nestedOptions);
        return w3cOptions;
    }

    private boolean isNonW3CProperty(String key) {
        return (NON_SAUCE_PROPERTIES.contains(unprefixed(key)) || NON_SAUCE_PROPERTIES.contains(key));
    }

    private boolean isW3CProperty(String key) {
        return !isNonW3CProperty(key);
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace(SAUCELABS, "");
    }

    private void configureTargetPlatform(DesiredCapabilities capabilities, EnvironmentVariables environmentVariables) {
        SetAppropriateSaucelabsPlatformVersion.inCapabilities(capabilities).from(environmentVariables);

        String remotePlatform = REMOTE_PLATFORM.from(environmentVariables);

        if (isNotEmpty(remotePlatform)) {
            capabilities.setPlatform(Platform.valueOf(remotePlatform));
        }
    }

    private void configureTargetBrowser(DesiredCapabilities capabilities, EnvironmentVariables environmentVariables) {
        String remoteBrowser = SAUCELABS_BROWSERNAME.from(environmentVariables);

        if (isNotEmpty(remoteBrowser)) {
            capabilities.setBrowserName(SAUCELABS_BROWSER_NAMES.getOrDefault(remoteBrowser, remoteBrowser));
        }
    }
}
