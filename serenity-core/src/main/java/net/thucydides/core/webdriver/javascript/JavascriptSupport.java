package net.thucydides.core.webdriver.javascript;

import net.thucydides.core.webdriver.WebDriverFacade;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class JavascriptSupport {
    public static boolean javascriptIsSupportedIn(Class<? extends WebDriver> driverClass) {
        return (isNotAMockedDriver(driverClass) && supportsJavascript(driverClass));
    }

    public static boolean javascriptIsSupportedIn(WebDriver driver) {
        if (driver == null) { return false; }
        if (driver instanceof WebDriverStub) { return false; }

        if (driver instanceof HtmlUnitDriver) {
            return ((HtmlUnitDriver) driver).isJavascriptEnabled();
        } else {
            Class<? extends WebDriver> driverClass = getRealDriverClass(driver);
            return javascriptIsSupportedIn(driverClass);
        }
    }

    private static Class<? extends WebDriver> getRealDriverClass(WebDriver driver) {
        if (WebDriverFacade.class.isAssignableFrom(driver.getClass())) {
            WebDriverFacade driverFacade = (WebDriverFacade) driver;
            return driverFacade.getDriverClass();
        } else {
            return driver.getClass();
        }
    }

    private static boolean supportsJavascript(Class<? extends WebDriver> driverClass) {
        return JavascriptExecutor.class.isAssignableFrom(driverClass);
    }


    private static boolean isNotAMockedDriver(Class<? extends WebDriver> driverClass) {
        return (driverClass != null) && !driverClass.getName().contains("Mock");
    }
}
