package net.serenitybdd.core.webdriver.enhancers;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingCustomCapabilityEnhancers {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void customEnhancersCanAddExtraCapabilitiesWhenADriverIsCreated() {

        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.CHROME, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("name")).isEqualTo( "Sample story - Sample test");
    }
}
