package net.serenitybdd.lambdatest;

import net.serenitybdd.core.environment.EnvironmentSpecificConfiguration;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

public class BeforeALambdaTestScenario implements BeforeAWebdriverScenario {

    @Override
    public MutableCapabilities apply(EnvironmentVariables environmentVariables,
                                     SupportedWebDriver driver,
                                     TestOutcome testOutcome,
                                     MutableCapabilities capabilities) {
        if (!LambdaTestConfiguration.isActiveFor(environmentVariables)) {
            return capabilities;
        }
        capabilities.setCapability("build", testOutcome.getStoryTitle());
        capabilities.setCapability("name", testOutcome.getCompleteName());
        return capabilities;
    }

    public boolean isActivated(EnvironmentVariables environmentVariables) {
        return !EnvironmentSpecificConfiguration.from(environmentVariables)
                .getPropertiesWithPrefix("lambdatest").isEmpty();
    }

}
