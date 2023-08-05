package net.serenitybdd.plugins.selenoid;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriver;

public class AfterASelenoidScenario implements AfterAWebdriverScenario {

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver))) {
        }

    }
}
