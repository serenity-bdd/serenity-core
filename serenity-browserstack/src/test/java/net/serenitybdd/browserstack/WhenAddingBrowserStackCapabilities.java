package net.serenitybdd.browserstack;

import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingBrowserStackCapabilities {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    private static final TestOutcome SAMPLE_TEST_OUTCOME = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

    @Before
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
    public void theBrowserNameCanBeSpecifiedInTheBrowserStackConfiguration() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.browserName","IE");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getBrowserName()).isEqualTo("IE");
    }

    @Test
    public void theBrowserVersionCanBeSpecifiedInTheBrowserStackConfiguration() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.browserVersion","11.0");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getCapability("browserVersion")).isEqualTo("11.0");
    }

    @Test
    public void theBrowserNameAndVersionCanBeOverridenAtRunTime() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.browserName","IE");
        environmentVariables.setProperty("browserstack.browserVersion","11.0");
        OverrideDriverCapabilities.withProperty("browserstack.browserName").setTo("Chrome");
        OverrideDriverCapabilities.withProperty("browserstack.browserVersion").setTo("78");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getBrowserName()).isEqualTo("Chrome");
        assertThat(capabilities.getCapability("browserVersion")).isEqualTo("78");
    }


    @Test
    public void osNameIsAssignedToBrowserStackSection() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.os","Windows");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(bstackOptionsFrom(capabilities).get("os")).isEqualTo("Windows");
    }

    @Test
    public void osNameCanBeOverridenAtRuntime() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.os","Windows");
        OverrideDriverCapabilities.withProperty("browserstack.os").setTo("OS X");

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(bstackOptionsFrom(capabilities).get("os")).isEqualTo("OS X");
    }


    @Test
    public void theSessionNameShouldBeTakenFromTheNameOfTheTest() {

        // Given
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(bstackOptionsFrom(capabilities).get("sessionName")).isEqualTo("Sample story - Sample test");
    }

    private Map<String,String> bstackOptionsFrom(DesiredCapabilities capabilities) {
        return (Map<String, String>) capabilities.getCapability("bstack:options");
    }

}
