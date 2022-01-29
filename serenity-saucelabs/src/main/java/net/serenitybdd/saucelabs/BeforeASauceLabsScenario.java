package net.serenitybdd.saucelabs;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public class BeforeASauceLabsScenario implements BeforeAWebdriverScenario {
    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     MutableCapabilities capabilities) {

        return new SaucelabsRemoteDriverCapabilities(environmentVariables).getCapabilities(capabilities);
    }

    @Override
    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return !EnvironmentSpecificConfiguration.from(environmentVariables)
                .getPropertiesWithPrefix("saucelabs").isEmpty();
    }

}
