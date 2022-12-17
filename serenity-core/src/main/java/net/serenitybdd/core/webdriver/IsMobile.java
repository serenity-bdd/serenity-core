package net.serenitybdd.core.webdriver;

import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;

public class IsMobile {
    public static boolean driver(WebDriver driver) {
        WebDriver proxiedDriver = (driver instanceof WebDriverFacade) ? ((WebDriverFacade) driver).getProxiedDriver() : driver;

        try {
            return Class.forName("io.appium.java_client.AppiumDriver").isAssignableFrom(proxiedDriver.getClass());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
