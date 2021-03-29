package serenitycore.net.serenitybdd.core.webdriver.enhancers;

import serenitycore.net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import serenitymodel.net.thucydides.core.model.Story;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import serenitymodel.net.thucydides.core.util.MockEnvironmentVariables;
import serenitycore.net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingCustomCapabilityEnhancers {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void customEnhancersCanAddExtraCapabilitiesWhenADriverIsCreated() {

        // Given
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.CHROME, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("name")).isEqualTo( "Sample story - Sample test");
    }
}
