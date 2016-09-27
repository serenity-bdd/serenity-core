package net.thucydides.core.webdriver;

import io.appium.java_client.MobileDriver;
import org.openqa.selenium.WebDriver;

public class WebDriverType {
    public static boolean isMobile(WebDriver driver) {
        if(driver instanceof WebDriverFacade){
            return (MobileDriver.class.isAssignableFrom(((WebDriverFacade) driver).getDriverClass()));
        }
        return (driver instanceof MobileDriver);
    }
}
