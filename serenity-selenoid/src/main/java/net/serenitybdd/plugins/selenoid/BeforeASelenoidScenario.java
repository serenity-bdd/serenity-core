package net.serenitybdd.plugins.selenoid;

import net.serenitybdd.core.webdriver.driverproviders.CapabilityValue;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.capabilities.W3CCapabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.CapabilityType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static net.serenitybdd.plugins.selenoid.SerenitySelenoidUtil.*;

public class BeforeASelenoidScenario implements BeforeAWebdriverScenario {

    public static final List<String> capability = Arrays.asList(CapabilityType.BROWSER_NAME, CapabilityType.BROWSER_VERSION, CapabilityType.PLATFORM_NAME);


    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables, SupportedWebDriver driver, TestOutcome testOutcome, MutableCapabilities capabilities) {

        if (driver != SupportedWebDriver.REMOTE) {
            return capabilities;
        }
        Map<String, Object> selenoidOptionsMap = SerenitySelenoidUtil.getSelenoidOptionsMap(testOutcome);

        // Set video link to serenity report
        SerenitySelenoidUtil.linkVideoToSerenityReport(testOutcome);


        // get properties from serenity.properties
        Properties selenoidProperties = environmentVariables.getPropertiesWithPrefix(SELENOID);
        for (String propertyName : selenoidProperties.stringPropertyNames()) {
            String unPrefixedPropertyName = SerenitySelenoidUtil.unprefixed(propertyName);
            Object value = CapabilityValue.fromString(selenoidProperties.getProperty(propertyName));
            capability.stream().filter(k -> k.equalsIgnoreCase(unPrefixedPropertyName)).forEach(k -> capabilities.setCapability(k, value));
            if (propertyName.startsWith(SELENOID_OPTIONS)) {
                selenoidOptionsMap.put(propertyName.replace(SELENOID_OPTIONS, ""), value);
            }
        }

        // get properties from serenity.conf
        MutableCapabilities mutableCapabilities = W3CCapabilities.definedIn(SystemEnvironmentVariables.currentEnvironmentVariables()).withPrefix("selenoid").forDriver(driver);
        capabilities.merge(mutableCapabilities);
        Object o = mutableCapabilities.asMap().get(SELENOID_OPTIONS_CONFIG);
        if (o instanceof Map) {
            selenoidOptionsMap.putAll((Map) o);
        }
        capabilities.setCapability(SELENOID_OPTIONS_CONFIG, selenoidOptionsMap);
        return capabilities;
    }


}
