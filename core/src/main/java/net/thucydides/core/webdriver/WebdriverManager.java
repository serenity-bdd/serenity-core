package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

/**
 * Manage WebDriver instances.
 * It instantiates browser drivers, based on the test configuration, and manages them for the
 * duration of the tests.
 * 
 * @author johnsmart
 *
 */
public interface WebdriverManager {

    WebDriver getWebdriver();

    WebDriver getWebdriver(final String driver);

    SessionId getSessionId();

    void closeDriver();

    void closeAllCurrentDrivers();

    void closeAllDrivers();

    void resetDriver();

    int getCurrentActiveWebdriverCount();

    int getActiveWebdriverCount();
}