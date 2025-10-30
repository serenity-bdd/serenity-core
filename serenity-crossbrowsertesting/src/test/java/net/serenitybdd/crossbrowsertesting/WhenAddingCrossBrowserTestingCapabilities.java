package net.serenitybdd.crossbrowsertesting;

import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.model.domain.Story;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.environment.MockEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingCrossBrowserTestingCapabilities {
    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    private static final TestOutcome SAMPLE_TEST_OUTCOME = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

    @BeforeEach
    public void prepareSession() {
        OverrideDriverCapabilities.clear();
    }

    @Test
    public void thePlatformShouldBeAddedToTheCapability() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("remote.platform","android");

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(capabilities.getPlatformName()).isEqualTo(Platform.ANDROID);
    }

    @Test
    public void theBuildNameCanBeSpecifiedInTheCrossBrowserTestingConfiguration() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("crossbrowsertesting.build","sample build");
        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(cbtOptionsFrom(capabilities).get("build")).isEqualTo("sample build");
    }

    @Test
    public void theSessionNameShouldBeTakenFromTheNameOfTheTest() {

        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities();

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(cbtOptionsFrom(capabilities).get("name")).isEqualTo("Sample story - Sample test");
    }

    @Test
    public void theBuildCanBeOverridenAtRunTime() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("crossbrowsertesting.build","sample build");
        OverrideDriverCapabilities.withProperty("crossbrowsertesting.build").setTo("overridden build");

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(cbtOptionsFrom(capabilities).get("build")).isEqualTo("overridden build");
    }

    private Map<String,String> cbtOptionsFrom(DesiredCapabilities capabilities) {
        return (Map<String, String>) capabilities.getCapability("cbt:options");
    }
}
