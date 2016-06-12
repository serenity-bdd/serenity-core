package net.thucydides.core.webdriver;

import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.WebDriver;

public class WebDriverType {
    public static boolean isMobile(WebDriver driver) {
        if(driver instanceof WebDriverFacade){
            return ((WebDriverFacade)driver).getDriverClass().isAssignableFrom(AppiumDriver.class);
        }
        return (driver.getClass().isAssignableFrom(AppiumDriver.class));
    }
}
