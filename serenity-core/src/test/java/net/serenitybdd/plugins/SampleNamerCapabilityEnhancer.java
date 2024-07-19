package net.serenitybdd.plugins;

import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public class SampleNamerCapabilityEnhancer implements BeforeAWebdriverScenario {

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables, SupportedWebDriver driver, TestOutcome testOutcome, MutableCapabilities capabilities) {
        if (testOutcome != null) {
            capabilities.setCapability("name", testOutcome.getStoryTitle() + " - " + testOutcome.getTitle());
        }
        return capabilities;
    }
}
