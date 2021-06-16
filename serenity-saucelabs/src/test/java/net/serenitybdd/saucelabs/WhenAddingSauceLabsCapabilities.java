
package net.serenitybdd.saucelabs;

import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
//import org.junit.Before;
//import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingSauceLabsCapabilities {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    private static final TestOutcome SAMPLE_TEST_OUTCOME = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

    @BeforeEach
    public void prepareSession() {
        OverrideDriverCapabilities.clear();
    }

    @Test
    public void theBrowserNameShouldBeAddedDirectlyToTheCapability() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getBrowserName()).isEqualTo("chrome");
    }

    @Test
    public void theBrowserNameCanBeSpecifiedInTheSauceLabsConfiguration() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("saucelabs.browserName","IE");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getBrowserName()).isEqualTo("IE");
    }

    @Test
    public void theBrowserVersionCanBeSpecifiedInTheSauceLabsConfiguration() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("saucelabs.browserVersion","11.0");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getCapability("browserVersion")).isEqualTo("11.0");
    }

    @Test
    public void theBrowserNameAndVersionCanBeOverridenAtRunTime() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("saucelabs.browserName","IE");
        environmentVariables.setProperty("saucelabs.browserVersion","11.0");
        OverrideDriverCapabilities.withProperty("saucelabs.browserName").setTo("Chrome");
        OverrideDriverCapabilities.withProperty("saucelabs.browserVersion").setTo("78");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getBrowserName()).isEqualTo("Chrome");
        assertThat(capabilities.getCapability("browserVersion")).isEqualTo("78");
    }

    @Test
    public void osNameIsAssignedToSauceLabsSection() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("saucelabs.os","Windows");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(sauceOptionsFrom(capabilities).get("os")).isEqualTo("Windows");
    }

    @Test
    public void osNameCanBeOverridenAtRuntime() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("saucelabs.os","Windows");
        OverrideDriverCapabilities.withProperty("saucelabs.os").setTo("OS X");

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(sauceOptionsFrom(capabilities).get("os")).isEqualTo("OS X");
    }


    @Test
    public void theSessionNameShouldBeTakenFromTheNameOfTheTest() {
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(sauceOptionsFrom(capabilities).get("name")).isEqualTo("Sample story - Sample test");
    }

    private Map<String,String> sauceOptionsFrom(DesiredCapabilities capabilities) {
        return (Map<String, String>) capabilities.getCapability("sauce:options");
    }

}
