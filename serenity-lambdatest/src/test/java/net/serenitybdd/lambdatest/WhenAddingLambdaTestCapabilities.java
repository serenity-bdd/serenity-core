
package net.serenitybdd.lambdatest;

import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.environment.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingLambdaTestCapabilities {

    EnvironmentVariables environmentVariables;

    private static final TestOutcome SAMPLE_TEST_OUTCOME = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

    BeforeALambdaTestScenario beforeAScenario;
    DesiredCapabilities capabilities;

    @BeforeEach
    public void prepareSession() {
        OverrideDriverCapabilities.clear();
        beforeAScenario = new BeforeALambdaTestScenario();
        capabilities = new DesiredCapabilities();
        environmentVariables = new MockEnvironmentVariables();
    }


    @Test
    public void theBrowserNameShouldBeAddedDirectlyToTheCapability() {

        environmentVariables.setProperty("webdriver.remote.url", "https://hub-cloud.lambdatest.com/wd/hub");

        beforeAScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(capabilities.getCapability("name")).isEqualTo("Sample story:sample_test");
    }

    @Test
    public void theRemoteUrlMustContainLambdaTest() {

        environmentVariables.setProperty("webdriver.remote.url", "https://hub-cloud.saucelabs.com/wd/hub");

        beforeAScenario.apply(environmentVariables, SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME, capabilities);

        assertThat(capabilities.getCapability("name")).isNull();
    }

    @Test
    public void theAuthTokenUsesTheUsernameAndAPIKey() {

        environmentVariables.setProperty("lambdatest.user", "my.username");
        environmentVariables.setProperty("lambdatest.key", "XXXXXXXXXXXX");
        String expectedDigest = DigestUtils.md5Hex("my.username:XXXXXXXXXXXX".getBytes());

        String authToken = LambdaTestAuthToken.usingCredentialsFrom(environmentVariables);

        assertThat(authToken).isEqualTo(expectedDigest);
    }

    @Test
    public void theVideoLinkCombinesTheSessionIdAndTheAuthToken() {

        environmentVariables.setProperty("lambdatest.user", "my.username");
        environmentVariables.setProperty("lambdatest.key", "XXXXXXXXXXXX");

        String sessionId = "HJKXM-RHZL1-SVPWY-AB8X6";
        String digest = DigestUtils.md5Hex("my.username:XXXXXXXXXXXX".getBytes());

        String expectedUrl = "https://automation.lambdatest.com/public/video?testID=HJKXM-RHZL1-SVPWY-AB8X6&auth=" + digest;

        assertThat(LambdaTestVideoLink.forEnvironment(environmentVariables).videoUrlForSession(sessionId))
                .isEqualTo(expectedUrl);
    }
}
