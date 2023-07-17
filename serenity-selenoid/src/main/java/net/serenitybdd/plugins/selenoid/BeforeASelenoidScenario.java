package net.serenitybdd.plugins.selenoid;

import net.serenitybdd.core.webdriver.driverproviders.CapabilityValue;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

import static net.serenitybdd.plugins.selenoid.SerenitySelenoidUtil.SELENOID;

public class BeforeASelenoidScenario implements BeforeAWebdriverScenario {

    public static final List<String> capability = Arrays.asList(CapabilityType.BROWSER_NAME, CapabilityType.BROWSER_VERSION, CapabilityType.PLATFORM_NAME);


    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables, SupportedWebDriver driver, TestOutcome testOutcome, MutableCapabilities capabilities) {

        if (driver != SupportedWebDriver.REMOTE) {
            return capabilities;
        }
        Map<String, Object> SELENOID_PROPERTIES_MAP = new HashMap<>();
        String name = SerenitySelenoidUtil.getName(testOutcome);
        SELENOID_PROPERTIES_MAP.put("name", name);
        SELENOID_PROPERTIES_MAP.put("videoName", SerenitySelenoidUtil.getVideoName(name));

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        Properties selenoidProperties = environmentVariables.getPropertiesWithPrefix(SELENOID);
        for (String propertyName : selenoidProperties.stringPropertyNames()) {
            String unPrefixedPropertyName = SerenitySelenoidUtil.unprefixed(propertyName);
            Object value = CapabilityValue.fromString(selenoidProperties.getProperty(propertyName));
            capability.stream().filter(k -> k.equalsIgnoreCase(unPrefixedPropertyName)).forEach(k -> desiredCapabilities.setCapability(k, value));
            SELENOID_PROPERTIES_MAP.put(unPrefixedPropertyName, value);
        }

        SerenitySelenoidUtil.linkVideoToSerenityReport(testOutcome, SerenitySelenoidUtil.getVideoName(name));


        capabilities.setCapability("selenoid:options", SELENOID_PROPERTIES_MAP);
        capabilities.merge(desiredCapabilities);
        return capabilities;
    }


}
