package net.serenitybdd.browserstack;

import net.serenitybdd.core.webdriver.driverproviders.CapabilityValue;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class BeforeABrowserStackScenario implements BeforeAWebdriverScenario {

    public static final String BROWSERSTACK = "browserstack.";

    @Override
    public DesiredCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     DesiredCapabilities capabilities) {

        if (driver != SupportedWebDriver.REMOTE) {
            return capabilities;
        }

        String remotePlatform = environmentVariables.getProperty("remote.platform");
        if (isNotEmpty(remotePlatform)) {
            capabilities.setPlatform(Platform.valueOf(remotePlatform));
        }

        capabilities.setCapability("name", testOutcome.getStoryTitle() + " - " + testOutcome.getTitle());

        Properties browserStackProperties = environmentVariables.getPropertiesWithPrefix(BROWSERSTACK);

        for(String propertyName : browserStackProperties.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(propertyName);
            capabilities.setCapability(unprefixedPropertyName, CapabilityValue.fromString(browserStackProperties.getProperty(propertyName)));
        }

        return capabilities;
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace(BROWSERSTACK,"");
    }

}