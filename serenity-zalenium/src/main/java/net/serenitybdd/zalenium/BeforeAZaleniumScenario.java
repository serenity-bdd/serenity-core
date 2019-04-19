package net.serenitybdd.zalenium;

import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Properties;

public class BeforeAZaleniumScenario implements BeforeAWebdriverScenario {

    public static final String ZALENIUM = "zalenium.";

    @Override
    public DesiredCapabilities apply(EnvironmentVariables environmentVariables, SupportedWebDriver driver, TestOutcome testOutcome, DesiredCapabilities capabilities) {
        if (driver != SupportedWebDriver.REMOTE) {
            return capabilities;
        }

        capabilities.setCapability("network.cookie.cookieBehavior","1");
        capabilities.setCapability("profile.default_content_settings.cookies","1");

        capabilities.setCapability("name", testOutcome.getStoryTitle() + " - " + testOutcome.getTitle());

        Properties zaleniumProperties = environmentVariables.getPropertiesWithPrefix(ZALENIUM);

        for(String propertyName : zaleniumProperties.stringPropertyNames()) {
            String unprefixedPropertyName = unprefixed(propertyName);
            capabilities.setCapability(unprefixedPropertyName, typed(zaleniumProperties.getProperty(propertyName)));
        }

        return capabilities;
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
        return propertyName.replace("zalenium.","");
    }

}