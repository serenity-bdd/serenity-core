
package net.serenitybdd.browserstack;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.webdriver.OverrideDriverCapabilities;
import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.util.MockEnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingBrowserStackCapabilities {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    private static final TestOutcome SAMPLE_TEST_OUTCOME = TestOutcome.forTestInStory("sample_test", Story.called("Sample story"));

    @BeforeEach
    public void prepareSession() {
        OverrideDriverCapabilities.clear();
    }
    @Test
    public void theBrowserNameShouldBeAddedDirectlyToTheCapability() {

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        DesiredCapabilities capabilities = new DesiredCapabilities(options);

        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(enhancedCapabilities.getBrowserName()).isEqualTo("chrome");
    }

    @Test
    public void theBrowserNameCanBeSpecifiedInTheBrowserStackConfiguration() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.browserName","IE");
        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(enhancedCapabilities.getBrowserName()).isEqualTo("IE");
    }

    @Test
    public void theBrowserVersionCanBeSpecifiedInTheBrowserStackConfiguration() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.browserVersion","11.0");
        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(enhancedCapabilities.getCapability("browserVersion")).isEqualTo("11.0");
    }

    @Test
    public void theBrowserNameAndVersionCanBeOverridenAtRunTime() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.browserName","IE");
        environmentVariables.setProperty("browserstack.browserVersion","11.0");
        OverrideDriverCapabilities.withProperty("browserstack.browserName").setTo("Chrome");
        OverrideDriverCapabilities.withProperty("browserstack.browserVersion").setTo("78");
        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(enhancedCapabilities.getBrowserName()).isEqualTo("Chrome");
        assertThat(enhancedCapabilities.getCapability("browserVersion")).isEqualTo("78");
    }


    @Test
    public void osNameIsAssignedToBrowserStackSection() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.os","Windows");
        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(bstackOptionsFrom(enhancedCapabilities).get("os")).isEqualTo("Windows");
    }

    @Test
    public void osNameCanBeOverridenAtRuntime() {
        DesiredCapabilities capabilities = new DesiredCapabilities();

        environmentVariables.setProperty("browserstack.os","Windows");
        OverrideDriverCapabilities.withProperty("browserstack.os").setTo("OS X");

        Capabilities mutableCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(bstackOptionsFrom(mutableCapabilities).get("os")).isEqualTo("OS X");
    }


    @Test
    public void theSessionNameShouldBeTakenFromTheNameOfTheTest() {

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200");
        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities(options);

        Capabilities enhancedCapabilities = AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, SAMPLE_TEST_OUTCOME)
                .to(capabilities);

        assertThat(bstackOptionsFrom(enhancedCapabilities).get("sessionName")).isEqualTo("Sample story - Sample test");
    }

    private Map<String,String> bstackOptionsFrom(Capabilities capabilities) {
        return (Map<String, String>) capabilities.getCapability("bstack:options");
    }

}
