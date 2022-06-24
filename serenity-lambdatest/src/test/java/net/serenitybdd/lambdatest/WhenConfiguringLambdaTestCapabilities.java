package net.serenitybdd.lambdatest;

import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.thucydides.core.environment.MockEnvironmentVariables;
import net.thucydides.core.environment.SystemEnvironmentVariables;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("When configuring the LambdaTest plugin in the serenity.conf file")
class WhenConfiguringLambdaTestCapabilities {

    MockEnvironmentVariables environmentVariables;

    private static final TestOutcome SAMPLE_TEST_OUTCOME = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

    BeforeALambdaTestScenario beforeAScenario;
    DesiredCapabilities capabilities;

    @BeforeEach
    void prepareSession() {
        OverrideDriverCapabilities.clear();
        beforeAScenario = new BeforeALambdaTestScenario();
        capabilities = new DesiredCapabilities();
        environmentVariables = new MockEnvironmentVariables();
        environmentVariables.setValue("LT_USERNAME", "some.user");
        environmentVariables.setValue("LT_ACCESS_KEY", "XXXXXXXX");
    }


    @DisplayName("The test name is derived from the test outcome")
    @Test
    void testName() {

        environmentVariables.setProperty("lambdatest.platformName", "Windows 11");

        beforeAScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(capabilities.getCapability("LT:Options")).isNotNull();
        Map<String, Object> ltOptions = (Map<String, Object>) capabilities.getCapability("LT:Options");
        assertThat(ltOptions.get("name")).isEqualTo("Sample story - Sample test");
    }

    @DisplayName("The build name")
    @Nested
    class BuildName {

        @DisplayName("is derived from the build.name configuration property")
        @Test
        void fromConfigFile() {

            environmentVariables.setProperty("lambdatest.platformName", "Windows 11");
            environmentVariables.setProperty("lambdatest.build", "My build");

            beforeAScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

            assertThat(capabilities.getCapability("LT:Options")).isNotNull();
            Map<String, Object> ltOptions = (Map<String, Object>) capabilities.getCapability("LT:Options");
            assertThat(ltOptions.get("build")).isEqualTo("My build");
        }


    }

    @Nested
    @DisplayName("LambdaTest properties are defined in the lambdatest section")
    class LambdaTestProperties {

        @DisplayName("Defining String properties")
        @Test
        void stringProperty() {
            environmentVariables.setProperty("lambdatest.platformName", "Windows 11");
            environmentVariables.setProperty("lambdatest.geoLocation", "AU");
            environmentVariables.setProperty("lambdatest.resolution", "1280x800");

            beforeAScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

            assertThat(capabilities.getCapability("LT:Options")).isNotNull();

            Map<String, Object> ltOptions = (Map<String, Object>) capabilities.getCapability("LT:Options");
            assertThat(ltOptions.get("platformName")).isEqualTo("Windows 11");
            assertThat(ltOptions.get("geoLocation")).isEqualTo("AU");
            assertThat(ltOptions.get("resolution")).isEqualTo("1280x800");
        }

        @DisplayName("Defining boolean properties")
        @Test
        void booleanProperty() {
            environmentVariables.setProperty("lambdatest.headless", "true");
            environmentVariables.setProperty("lambdatest.visual", "true");
            environmentVariables.setProperty("lambdatest.network", "false");

            beforeAScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

            assertThat(capabilities.getCapability("LT:Options")).isNotNull();

            Map<String, Object> ltOptions = (Map<String, Object>) capabilities.getCapability("LT:Options");
            assertThat(ltOptions.get("headless")).isEqualTo(true);
            assertThat(ltOptions.get("visual")).isEqualTo(true);
            assertThat(ltOptions.get("network")).isEqualTo(false);
        }
    }

    @Nested
    @DisplayName("W3C properties can be defined in the w3c section")
    class W3CTestProperties {

    }
}
