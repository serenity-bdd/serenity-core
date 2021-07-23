package net.serenitybdd.saucelabs;

import net.serenitybdd.junit.runners.SerenityRunner;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SerenityRunner.class)
public class WhenConfiguringSauceLabsCapabilities {

    EnvironmentVariables environmentVariables;

    @Before
    public void setupEnvironment() {
        environmentVariables = new MockEnvironmentVariables();
    }

    @Test
    public void theSaucelabsUrlCanBeDefinedInTheEnvironmentVariables() {
        final String SAUCELABS_URL = "http://myID:MY_AP_KEY@ondemand.saucelabs.com:80/wd/hub";

        environmentVariables.setProperty("saucelabs.url", SAUCELABS_URL);
        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);

        assertThat(capabilities.getUrl()).isEqualTo(SAUCELABS_URL);
    }

    @Test
    public void weCanInjectTheAPIKeyAndIDAsVariablesUsingTheHashNotation() {
        final String SAUCELABS_URL_TEMPLATE = "http://#{saucelabs.user.id}:#{saucelabs.access.key}@ondemand.saucelabs.com:80/wd/hub";
        final String SAUCELABS_URL = "http://MY_ID:MY_API_KEY@ondemand.saucelabs.com:80/wd/hub";

        environmentVariables.setProperty("saucelabs.url", SAUCELABS_URL_TEMPLATE);
        environmentVariables.setProperty("saucelabs.user.id", "MY_ID");
        environmentVariables.setProperty("saucelabs.access.key", "MY_API_KEY");
        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);

        assertThat(capabilities.getUrl()).isEqualTo(SAUCELABS_URL);
    }

    @Test
    public void theSaucelabsBrowserCanBeDefinedInTheSaucelabsVariables() {
        environmentVariables.setProperty("saucelabs.browserName", "edge");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        DesiredCapabilities saucelabsCapabilities = capabilities.getCapabilities(new DesiredCapabilities());

        assertThat(saucelabsCapabilities.getBrowserName()).isEqualTo("MicrosoftEdge");
    }

    @Test
    public void saucelabsOptionsCanBeDefinedInTheSerenityConfProperties() {
        environmentVariables.setProperty("saucelabs.screenResolution", "800x600");
        environmentVariables.setProperty("saucelabs.recordScreenshots", "false");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        DesiredCapabilities saucelabsCapabilities = capabilities.getCapabilities(new DesiredCapabilities());
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
        DesiredCapabilities saucelabsCapabilities = capabilities.getCapabilities(new DesiredCapabilities());

        assertThat(saucelabsCapabilities.getCapability("platformName")).isEqualTo("macOS 11");
        assertThat(saucelabsCapabilities.getCapability("browserVersion")).isEqualTo("14");
        assertThat(saucelabsCapabilities.getCapability("browserName")).isEqualTo("Safari");
    }

    @Test
    public void latestBrowserVersionsCanBeDefinedInTheSaucelabsConf() {
        environmentVariables.setProperty("saucelabs.platformName", "macOS 12");
        environmentVariables.setProperty("saucelabs.browserVersion", "latest");

        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        DesiredCapabilities saucelabsCapabilities = capabilities.getCapabilities(new DesiredCapabilities());
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
        DesiredCapabilities saucelabsCapabilities = capabilities.getCapabilities(new DesiredCapabilities());
        MutableCapabilities saucelabsOpts = (MutableCapabilities) saucelabsCapabilities.getCapability("sauce:options");

        assertThat(saucelabsOpts.getCapability("platformName")).isEqualTo("macOS 12");
        assertThat(saucelabsOpts.getCapability("browserVersion")).isEqualTo("latest");
        assertThat(saucelabsOpts.getCapability("screenResolution")).isEqualTo("800x600");
        assertThat(saucelabsOpts.getCapability("recordScreenshots")).isEqualTo("false");
    }

    @Test
    public void theSessionNameShouldBeTakenFromTheNameOfTheTest() {
        SaucelabsRemoteDriverCapabilities capabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        DesiredCapabilities saucelabsCapabilities = capabilities.getCapabilities(new DesiredCapabilities());
        MutableCapabilities saucelabsOpts = (MutableCapabilities) saucelabsCapabilities.getCapability("sauce:options");

        // Session name will be constructed from class name and method name, separated by a colon
        String sessionName = this.getClass().getSimpleName() + ":" +
                             Thread.currentThread().getStackTrace()[1].getMethodName();
        assertThat(saucelabsOpts.getCapability("name")).isEqualTo(sessionName);
    }
}
