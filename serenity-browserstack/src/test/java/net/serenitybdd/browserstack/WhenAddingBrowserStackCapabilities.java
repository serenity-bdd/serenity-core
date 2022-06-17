
package net.serenitybdd.browserstack;

import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.environment.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
//import org.junit.Before;
//import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingBrowserStackCapabilities {

    EnvironmentVariables environmentVariables;

    private static final TestOutcome SAMPLE_TEST_OUTCOME = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

    BeforeABrowserStackScenario beforeABrowserStackScenario;
    DesiredCapabilities capabilities;

    @BeforeEach
    public void prepareSession() {
        OverrideDriverCapabilities.clear();
        beforeABrowserStackScenario = new BeforeABrowserStackScenario();
        capabilities = new DesiredCapabilities();
        environmentVariables = new MockEnvironmentVariables();
    }


    @Test
    public void theBrowserNameShouldBeAddedDirectlyToTheCapability() {

        environmentVariables.setProperty("webdriver.remote.url","https://hub-cloud.browserstack.com/wd/hub");
        environmentVariables.setProperty("browserstack.browser","Edge");

        beforeABrowserStackScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(capabilities.getBrowserName()).isEqualTo("Edge");
    }

    @Test
    public void theRemoteUrlMustContainBrowserstack() {

        environmentVariables.setProperty("webdriver.remote.url","https://hub-cloud.saucelabs.com/wd/hub");
        environmentVariables.setProperty("browserstack.browser","Edge");

        beforeABrowserStackScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(capabilities.getBrowserName()).isEqualTo("");
    }

    @Test
    public void theBrowserVersionCanBeSpecifiedInTheBrowserStackConfiguration() {

        environmentVariables.setProperty("webdriver.remote.url","https://hub-cloud.browserstack.com/wd/hub");
        environmentVariables.setProperty("browserstack.browser_version","11.0");

        beforeABrowserStackScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(capabilities.getCapability("browserVersion")).isEqualTo("11.0");
    }

    @Test
    public void osNameIsAssignedToBrowserStackSection() {
        environmentVariables.setProperty("webdriver.remote.url","https://hub-cloud.browserstack.com/wd/hub");
        environmentVariables.setProperty("browserstack.os","Windows");

        beforeABrowserStackScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(bstackOptionsFrom(capabilities).get("os")).isEqualTo("Windows");
    }

    @Test
    public void theSessionNameShouldBeTakenFromTheNameOfTheTest() {
        environmentVariables.setProperty("webdriver.remote.url","https://hub-cloud.browserstack.com/wd/hub");
        environmentVariables.setProperty("browserstack.os","Windows");

        beforeABrowserStackScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(bstackOptionsFrom(capabilities).get("sessionName")).isEqualTo("Sample story - Sample test");
    }

    private Map<String,String> bstackOptionsFrom(DesiredCapabilities capabilities) {
        return (Map<String, String>) capabilities.getCapability("bstack:options");
    }

}
