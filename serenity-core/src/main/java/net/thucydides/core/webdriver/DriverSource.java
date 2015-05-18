package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

/**
 * You can implement this class to provide your own driver instance.
 */
public interface DriverSource {

    /**
     * Return a new instance of a webdriver
     */
    WebDriver newDriver();

    /**
     * Return true if the driver is configured to take screenshots.
     */
    boolean takesScreenshots();

}