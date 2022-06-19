package net.thucydides.core.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class MyDriverSource implements DriverSource {

    @Override
    WebDriver newDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
//        return new ChromeDriver(options)
        return new HtmlUnitDriver();
    }

    @Override
    boolean takesScreenshots() {
        return true
    }
}
