package net.serenitybdd.plugins.selenoid;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public class AfterASelenoidScenario implements AfterAWebdriverScenario {

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver))) {
            return;
        }

    }
}
