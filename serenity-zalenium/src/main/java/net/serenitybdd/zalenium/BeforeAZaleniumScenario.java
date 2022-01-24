package net.serenitybdd.zalenium;

import net.serenitybdd.core.webdriver.driverproviders.CapabilityValue;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;

import java.util.Properties;

public class BeforeAZaleniumScenario implements BeforeAWebdriverScenario {

    public static final String ZALENIUM = "zalenium.";

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables, SupportedWebDriver driver, TestOutcome testOutcome, MutableCapabilities capabilities) {
        if (driver != SupportedWebDriver.REMOTE) {
            return capabilities;
        }

        capabilities.setCapability("network.cookie.cookieBehavior","1");
        capabilities.setCapability("profile.default_content_settings.cookies","1");

        capabilities.setCapability("name", testOutcome.getStoryTitle() + " - " + testOutcome.getTitle());

        Properties zaleniumProperties = environmentVariables.getPropertiesWithPrefix(ZALENIUM);

        for(String propertyName : zaleniumProperties.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(propertyName);
            capabilities.setCapability(unprefixedPropertyName, CapabilityValue.fromString(zaleniumProperties.getProperty(propertyName)));
        }

        return capabilities;
    }

    private String unprefixed(String propertyName) {
        return propertyName.replace("zalenium.","");
    }

}