package net.serenitybdd.core.webdriver.enhancers;

import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SampleNamerCapabilityEnhancer implements BeforeAWebdriverScenario {

    @Override
    public DesiredCapabilities apply(EnvironmentVariables environmentVariables, SupportedWebDriver driver, TestOutcome testOutcome, DesiredCapabilities capabilities) {
        System.out.println("SampleNamerCapabilityEnhancer " + testOutcome);
        if (testOutcome != null) {
            capabilities.setCapability("name", testOutcome.getStoryTitle() + " - " + testOutcome.getTitle());
        }
        System.out.println("Enhanced capability name " + capabilities.getCapability("name") + " " + capabilities.hashCode());
        return capabilities;
    }
}