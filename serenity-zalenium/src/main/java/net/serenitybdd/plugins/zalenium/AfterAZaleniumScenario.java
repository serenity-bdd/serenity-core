package net.serenitybdd.plugins.zalenium;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.thucydides.model.domain.TestOutcome;
import net.thucydides.model.util.EnvironmentVariables;
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
