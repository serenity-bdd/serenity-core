package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.List;

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

    WebdriverContext inContext(String context);

    WebDriver getWebdriver(String driver);
    WebDriver getWebdriverByName(String actorName);
    WebDriver getWebdriverByName(String actorName, String driver);

    String getCurrentDriverType();

    String getDefaultDriverType();
    void overrideDefaultDriverType(String driverType);

    SessionId getSessionId();

    void closeDriver();

    void closeAllCurrentDrivers();

    void closeAllDrivers();

    void resetDriver();

    int getCurrentActiveWebdriverCount();

    int getActiveWebdriverCount();

    boolean isDriverInstantiated();

    void setCurrentDriver(WebDriver driver);

    void clearCurrentDriver();

    void registerDriver(WebDriver driver);

    List<WebDriver> getRegisteredDrivers();
    public List<String> getActiveDriverTypes();
}