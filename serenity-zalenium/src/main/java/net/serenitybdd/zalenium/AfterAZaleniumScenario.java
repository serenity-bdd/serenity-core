package net.serenitybdd.zalenium;

import net.serenitybdd.core.webdriver.RemoteDriver;
import net.serenitybdd.core.webdriver.enhancers.AfterAWebdriverScenario;
import net.serenitybdd.core.webdriver.enhancers.BeforeAWebdriverScenario;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.SupportedWebDriver;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Properties;

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