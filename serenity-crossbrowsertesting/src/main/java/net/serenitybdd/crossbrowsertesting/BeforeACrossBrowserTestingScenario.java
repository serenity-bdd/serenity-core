package net.serenitybdd.crossbrowsertesting;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class BeforeACrossBrowserTestingScenario implements BeforeAWebdriverScenario {
    private static final String CROSS_BROWSER_TESTING = "crossbrowsertesting.";
    private static final List<String> NON_CBT_PROPERTIES =
            Arrays.asList(
                    "user",
                    "key",
                    "browserName",
                    "browserVersion",
                    "platformName"
            );

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables, SupportedWebDriver driver, TestOutcome testOutcome, MutableCapabilities capabilities) {
        if (driver != SupportedWebDriver.REMOTE) {
            return capabilities;
        }

        String remotePlatform = EnvironmentSpecificConfiguration.from(environmentVariables)
                .getOptionalProperty("remote.platform")
                .orElse(null);

        if (isNotEmpty(remotePlatform) && (capabilities instanceof DesiredCapabilities)) {
            ((DesiredCapabilities)capabilities).setPlatform(Platform.valueOf(remotePlatform.toUpperCase()));
        }

        Properties cbtProperties = EnvironmentSpecificConfiguration.from(environmentVariables).getPropertiesWithPrefix(CROSS_BROWSER_TESTING);

        OverrideDriverCapabilities.getProperties().forEach((key, value) -> cbtProperties.setProperty(key, value.toString()));

        setNonW3CCapabilities(capabilities, cbtProperties);

        Map<String, Object> cbtOptions = w3CPropertyMapFrom(cbtProperties);
        String testName = testOutcome.getStoryTitle() + " - " + testOutcome.getTitle();
        cbtOptions.put("name", testName);

        capabilities.setCapability("cbt:options", cbtOptions);
        return capabilities;
    }

    private void setNonW3CCapabilities(MutableCapabilities capabilities, Properties cbtProperties) {
        cbtProperties.stringPropertyNames()
                .stream()
                .filter(this::isNonW3CProperty)
                .forEach(
                        key -> capabilities.setCapability(unprefixed(key), cbtProperties.getProperty(key))
                );
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
                            w3cOptions.put(unprefixedKey, properties.getProperty(key));
                        }
                );
        w3cOptions.putAll(nestedOptions);
        return w3cOptions;
    }

    private boolean isNonW3CProperty(String key) {
        return NON_CBT_PROPERTIES.contains(unprefixed(key));
    }

    private boolean isW3CProperty(String key) {
        return !isNonW3CProperty(key);
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace(CROSS_BROWSER_TESTING, "");
    }


}
