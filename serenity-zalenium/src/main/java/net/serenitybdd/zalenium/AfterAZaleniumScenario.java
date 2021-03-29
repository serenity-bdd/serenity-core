package net.serenitybdd.zalenium;

import serenitycore.net.serenitybdd.core.webdriver.RemoteDriver;
import serenitycore.net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import serenitymodel.net.thucydides.core.model.TestOutcome;
import serenitymodel.net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class AfterAZaleniumScenario implements AfterAWebdriverScenario {

    @Override
    public void apply(EnvironmentVariables environmentVariables, TestOutcome testOutcome, WebDriver driver) {
        if ((driver == null) || (!RemoteDriver.isARemoteDriver(driver))) {
            return;
        }

        Cookie cookie = new Cookie("zaleniumTestPassed",
                                    testOutcome.isFailure() || testOutcome.isError() || testOutcome.isCompromised() ? "false" : "true");
        driver.manage().addCookie(cookie);
    }
}