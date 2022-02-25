package net.serenitybdd.saucelabs;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.MutableCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenConfiguringSauceLabsCapabilities {

    EnvironmentVariables environmentVariables;

    @Before
    public void setupEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
    }

    @Test
    public void theSaucelabsBrowserCanBeDefinedInTheSaucelabsVariables() {
        environmentVariables.setProperty("saucelabs.browserName", "edge");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        MutableCapabilities saucelabsCapabilities = capabilities.getCapabilities(new MutableCapabilities());

        assertThat(saucelabsCapabilities.getBrowserName()).isEqualTo("MicrosoftEdge");
    }

    @Test
    public void saucelabsOptionsCanBeDefinedInTheSerenityConfProperties() {
        environmentVariables.setProperty("saucelabs.screenResolution", "800x600");
        environmentVariables.setProperty("saucelabs.recordScreenshots", "false");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        MutableCapabilities saucelabsCapabilities = capabilities.getCapabilities(new MutableCapabilities());
        MutableCapabilities saucelabsOpts = (MutableCapabilities) saucelabsCapabilities.getCapability("sauce:options");

        assertThat(saucelabsOpts.getCapability("screenResolution")).isEqualTo("800x600");
        assertThat(saucelabsOpts.getCapability("recordScreenshots")).isEqualTo("false");
    }

    @Test
    public void platformAndBrowserVersionsCanBeDefinedInTheSaucelabsConf() {
        environmentVariables.setProperty("saucelabs.platformName", "macOS 11");
        environmentVariables.setProperty("saucelabs.browserVersion", "14");
        environmentVariables.setProperty("saucelabs.browserName", "Safari");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        MutableCapabilities saucelabsCapabilities = capabilities.getCapabilities(new MutableCapabilities());

        assertThat(saucelabsCapabilities.getCapability("platformName")).isEqualTo("macOS 11");
        assertThat(saucelabsCapabilities.getCapability("browserVersion")).isEqualTo("14");
        assertThat(saucelabsCapabilities.getCapability("browserName")).isEqualTo("Safari");
    }

    @Test
    public void latestBrowserVersionsCanBeDefinedInTheSaucelabsConf() {
        environmentVariables.setProperty("saucelabs.platformName", "macOS 12");
        environmentVariables.setProperty("saucelabs.browserVersion", "latest");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        MutableCapabilities saucelabsCapabilities = capabilities.getCapabilities(new MutableCapabilities());
        MutableCapabilities saucelabsOpts = (MutableCapabilities) saucelabsCapabilities.getCapability("sauce:options");

        assertThat(saucelabsOpts.getCapability("platformName")).isEqualTo("macOS 12");
        assertThat(saucelabsOpts.getCapability("browserVersion")).isEqualTo("latest");
    }

    @Test
    public void capabilitiesCanBeEnvironmentSpecific() {
        environmentVariables.setProperty("environments.mac.saucelabs.platformName", "macOS 12");
        environmentVariables.setProperty("environments.mac.saucelabs.browserVersion", "latest");
        environmentVariables.setProperty("environments.mac.saucelabs.screenResolution", "800x600");
        environmentVariables.setProperty("environments.mac.saucelabs.recordScreenshots", "false");
        environmentVariables.setProperty("environment", "mac");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        MutableCapabilities saucelabsCapabilities = capabilities.getCapabilities(new MutableCapabilities());
        MutableCapabilities saucelabsOpts = (MutableCapabilities) saucelabsCapabilities.getCapability("sauce:options");

        assertThat(saucelabsOpts.getCapability("platformName")).isEqualTo("macOS 12");
        assertThat(saucelabsOpts.getCapability("browserVersion")).isEqualTo("latest");
        assertThat(saucelabsOpts.getCapability("screenResolution")).isEqualTo("800x600");
        assertThat(saucelabsOpts.getCapability("recordScreenshots")).isEqualTo("false");
    }

    @Test
    public void theSessionNameShouldBeTakenFromTheNameOfTheTest() {
        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        MutableCapabilities saucelabsCapabilities = capabilities.getCapabilities(new MutableCapabilities());
        MutableCapabilities saucelabsOpts = (MutableCapabilities) saucelabsCapabilities.getCapability("sauce:options");

        // Session name will be constructed from class name and method name, separated by a colon
        assertThat(saucelabsOpts.getCapability("name")).isEqualTo("When configuring sauce labs capabilities: The session name should be taken from the name of the test");
    }

    @Test
    public void shouldBeAbleToObtainLinkToSauceLabsSessionVideo() {
        SauceLabsTestSession testSession = new SauceLabsTestSession("user", "key", "1234");
        assertThat(testSession.getTestLink()).isEqualTo("https://app.saucelabs.com/tests/1234?auth=fd3cb52fbcc6d14b40f64c978a06c18b");
    }
}
