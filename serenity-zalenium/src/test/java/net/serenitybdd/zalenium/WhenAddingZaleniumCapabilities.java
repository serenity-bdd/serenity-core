package net.serenitybdd.zalenium;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingZaleniumCapabilities {

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
    public void shouldAddZaleniumPropertiesFromTheEnvironmentConfiguration() {

        // Given
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));
        environmentVariables.setProperty("zalenium.screenResolution","1280x720");

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("screenResolution")).isEqualTo("1280x720");
    }

    @Test
    public void shouldAddZaleniumNumericalPropertiesFromTheEnvironmentConfiguration() {

        // Given
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));
        environmentVariables.setProperty("zalenium.idleTimeout","150");

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome).to(capabilities);

        assertThat(capabilities.getCapability("idleTimeout")).isEqualTo( 150);
    }

}
