package net.serenitybdd.core.webdriver;

import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteDriver {
    public static boolean isStubbed(WebDriver driver) {
        return (driver instanceof WebDriverStub);
    }

    public static RemoteWebDriver of(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            return (RemoteWebDriver) driver;
        }
        if (driver instanceof WebDriverFacade) {
            return RemoteDriver.of((((WebDriverFacade) driver).getProxiedDriver()));
        }
        throw new UnexpectedDriverExpected("Expected a remote web driver instance but found " + driver.getClass());
    }

    public static boolean isARemoteDriver(WebDriver driver) {
        return RemoteWebDriver.class.isAssignableFrom(((WebDriverFacade) driver).getDriverClass());
    }
}
