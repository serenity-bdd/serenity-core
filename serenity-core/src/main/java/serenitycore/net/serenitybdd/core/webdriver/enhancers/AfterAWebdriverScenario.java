package serenitycore.net.serenitybdd.core.webdriver.enhancers;

import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public interface AfterAWebdriverScenario {
    void apply(EnvironmentVariables environmentVariables,
               TestOutcome testOutcome,
               WebDriver driver);
}
