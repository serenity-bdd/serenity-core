package net.serenitybdd.zalenium;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingZaleniumCapabilities {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void shouldAddTheNameOfTheTest() {

        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

        AddCustomDriverCapabilities.from(environmentVariables)
                                   .withTestDetails(SupportedWebDriver.REMOTE, testOutcome)
                                   .to(capabilities);

        assertThat(capabilities.getCapability("name")).isEqualTo( "Sample story - Sample test");
    }

    @Test
    public void shouldAddZaleniumPropertiesFromTheEnvironmentConfiguration() {

        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));
        environmentVariables.setProperty("zalenium.screenResolution","1280x720");

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("screenResolution")).isEqualTo("1280x720");
    }

    @Test
    public void shouldAddZaleniumNumericalPropertiesFromTheEnvironmentConfiguration() {

        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));
        environmentVariables.setProperty("zalenium.idleTimeout","150");

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("idleTimeout")).isEqualTo( 150);
    }

}
