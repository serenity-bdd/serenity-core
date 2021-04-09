package net.serenitybdd.zalenium;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingZaleniumCapabilities {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void shouldAddTheNameOfTheTest() {

        // Given
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        DesiredCapabilities capabilities = new DesiredCapabilities(chromeOptions);
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, testOutcome)
                .to(capabilities);

        assertThat(enhancedCapabilities.getCapability("name")).isEqualTo( "Sample story - Sample test");
    }

    @Test
    public void shouldAddZaleniumPropertiesFromTheEnvironmentConfiguration() {

        // Given
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        DesiredCapabilities capabilities = new DesiredCapabilities(chromeOptions);
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));
        environmentVariables.setProperty("zalenium.screenResolution","1280x720");

        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome)
                .to(capabilities);

        assertThat(enhancedCapabilities.getCapability("screenResolution")).isEqualTo("1280x720");
    }

    @Test
    public void shouldAddZaleniumNumericalPropertiesFromTheEnvironmentConfiguration() {

        // Given
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        DesiredCapabilities capabilities = new DesiredCapabilities(chromeOptions);
        TestOutcome testOutcome = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));
        environmentVariables.setProperty("zalenium.idleTimeout","150");

        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome)
                .to(capabilities);

        assertThat(enhancedCapabilities.getCapability("idleTimeout")).isEqualTo( 150);
    }

}
