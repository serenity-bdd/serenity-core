package net.serenitybdd.core.webdriver;

import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

public class ConfigureFileDetector {
    public static void forDriver(WebDriver driver) {
        if (resolvedDriverFrom(driver) instanceof RemoteWebDriver) {
            try {
                ((RemoteWebDriver) resolvedDriverFrom(driver)).setFileDetector(new LocalFileDetector());
            } catch(WebDriverException e) {
                if (!e.getMessage().contains("only works on remote webdriver instances obtained via RemoteWebDriver")) {
                    throw e;
                }
            }
        }
    }

    private static WebDriver resolvedDriverFrom(WebDriver driver) {
        return (driver instanceof WebDriverFacade) ? ((WebDriverFacade) driver).getProxiedDriver() : driver;
    }
}
