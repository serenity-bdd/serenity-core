package net.serenitybdd.core.webdriver.enhancers;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingCustomCapabilityEnhancers {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void customEnhancersCanAddExtraCapabilitiesWhenADriverIsCreated() {

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities(chromeOptions);
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.CHROME, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("name")).isEqualTo( "Sample story - Sample test");
    }
}
