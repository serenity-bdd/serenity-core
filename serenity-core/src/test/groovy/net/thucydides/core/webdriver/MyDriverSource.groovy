package net.thucydides.core.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

public class MyDriverSource implements DriverSource {

    @Override
    public WebDriver newDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        return new ChromeDriver(chromeOptions)
    }

    @Override
    boolean takesScreenshots() {
        return true
    }
}
