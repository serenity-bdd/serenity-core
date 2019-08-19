package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.List;
import java.util.Map;

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

    WebDriver getCurrentDriver();

    String getDefaultDriverType();

    void overrideDefaultDriverType(String driverType);

    SessionId getSessionId();

    void closeDriver();

    void closeAllDrivers();

    void closeCurrentDrivers();

    WebdriverManager withProperty(String property, String value);

    void resetDriver();

    int getCurrentActiveWebdriverCount();

    int getActiveWebdriverCount();

    boolean hasAnInstantiatedDriver();

    void setCurrentDriver(WebDriver driver);

    void clearCurrentDriver();

    void registerDriver(WebDriver driver);

    List<WebDriver> getRegisteredDrivers();

    List<String> getActiveDriverTypes();

    void reset();

    WebdriverManager withOptions(String driverOptions);

    void overrideProperties(Map<String, String> propertyValues);
}