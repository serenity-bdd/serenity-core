package net.serenitybdd.plugins.selenoid;

import net.serenitybdd.core.webdriver.driverproviders.AddCustomDriverCapabilities;
import net.thucydides.core.environment.MockEnvironmentVariables;
import net.thucydides.core.model.Story;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class WhenAddingSelenoidCapabilities {

    EnvironmentVariables environmentVariables = new MockEnvironmentVariables();

    @Test
    public void shouldAddTheNameOfTheTest() {

        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities();
        TestOutcome testOutcome = TestOutcome.forTestInStory("serenity_selenoid_test", Story.called("Sample story"));
        String name = SerenitySelenoidUtil.getName(testOutcome);

        AddCustomDriverCapabilities.from(environmentVariables)
                .withTestDetails(SupportedWebDriver.REMOTE, testOutcome)
                .to(capabilities);
        Object capability = capabilities.getCapability("selenoid:options");
        assertThat(((HashMap) capability).get("name")).isEqualTo(name);
    }

    @Test
    public void shouldAddZaleniumPropertiesFromTheEnvironmentConfiguration() {

        // Given
        DesiredCapabilities capabilities = new DesiredCapabilities();
        TestOutcome testOutcome = TestOutcome.forTestInStory("serenity_selenoid_test", Story.called("Sample story"));
        environmentVariables.setProperty("selenoid.enableVNC", "true");

        AddCustomDriverCapabilities.from(environmentVariables).withTestDetails(SupportedWebDriver.REMOTE, testOutcome).to(capabilities);
        Object capability = capabilities.getCapability("selenoid:options");
        assertThat(((HashMap) capability).get("enableVNC")).isEqualTo(true);

    }

}
