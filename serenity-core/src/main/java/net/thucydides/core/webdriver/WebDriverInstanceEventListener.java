package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

public interface WebDriverInstanceEventListener {
    void close(WebDriver driver);
    void quit(WebDriver driver);
}
