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

    /**
     * Return the type of the webdriver being proxied.
     * Helps Serenity do internal stuff better.
     */
    default Class<? extends WebDriver> driverType() { return UnknownDriver.class; }

    /**
     * Used to indicate that the provided driver has not been specified by the driverType() method.
     */
    interface UnknownDriver extends WebDriver {}
}