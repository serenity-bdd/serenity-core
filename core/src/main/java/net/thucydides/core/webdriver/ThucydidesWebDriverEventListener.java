package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

/**
 * Listener class used to know when a new Webdriver instance has been created.
 */
public interface ThucydidesWebDriverEventListener {
    void driverCreatedIn(WebDriver driver);
}
