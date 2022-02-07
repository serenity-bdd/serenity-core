package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.driverproviders.cache.PreScenarioFixtures;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;

public class AddCustomDriverCapabilities {

    private final EnvironmentVariables environmentVariables;
    private SupportedWebDriver driver;
    private TestOutcome testOutcome;

    private AddCustomDriverCapabilities(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public AddCustomDriverCapabilities withTestDetails(SupportedWebDriver driver, TestOutcome testOutcome) {
        this.driver = driver;
        this.testOutcome = testOutcome;
        return this;
    }

    public static AddCustomDriverCapabilities from(EnvironmentVariables environmentVariables) {
        return new AddCustomDriverCapabilities(environmentVariables);
    }

    public MutableCapabilities to(MutableCapabilities capabilities) {
        PreScenarioFixtures.executeBeforeAWebdriverScenario().stream()
                        .filter(beforeAWebdriverScenario -> beforeAWebdriverScenario.isActivated(environmentVariables))
                                .forEach(beforeAWebdriverScenario -> beforeAWebdriverScenario.apply(environmentVariables, driver, testOutcome, capabilities));
        return capabilities;
    }
}
