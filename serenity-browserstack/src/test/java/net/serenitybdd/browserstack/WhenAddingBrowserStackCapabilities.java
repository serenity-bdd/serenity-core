package net.serenitybdd.browserstack;

import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingBrowserStackCapabilities {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void shouldAddTheNameOfTheTest() {

        // Given
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

        AddCustomDriverCapabilities.from(environmentVariables)
                                   .withTestDetails(SupportedWebDriver.REMOTE, testOutcome)
                                   .to(capabilities);

        assertThat(capabilities.getCapability("name")).isEqualTo( "Sample story - Sample test");
    }

    @Test
    public void shouldAddBrowserStackPropertiesFromTheEnvironmentConfiguration() {

        // Given
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));
        environmentVariables.setProperty("browserstack.browser","edge");

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("browser")).isEqualTo("edge");
    }
}
