package net.thucydides.core.webdriver;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.util.*;

/**
 * One or more WebDriver drivers that are being used in a test.
 */
public class WebdriverInstances {

    private final Map<String, WebDriver> driverMap;

    private String currentDriver;

    public WebdriverInstances() {
        this.driverMap = new HashMap<String, WebDriver>();
    }

    public WebDriver getCurrentDriver() {
        if (driverMap.containsKey(currentDriver)) {
            return driverMap.get(currentDriver);
        } else {
            return null;
        }
    }

    public String getCurrentDriverName() {
        return currentDriver;
    }

    public WebDriver closeCurrentDriver() {
        WebDriver closedDriver = null;
        if (getCurrentDriver() != null) {
            closedDriver = getCurrentDriver();
            closeAndQuit(closedDriver);
            driverMap.remove(currentDriver);
            currentDriver  = null;
        }
        return closedDriver;
    }

    private void closeAndQuit(WebDriver driver) {
    	//close is not necessary when quitting
        driver.quit();
    }

    public void resetCurrentDriver() {
        if (getCurrentDriver() != null) {
            WebDriver driver = getCurrentDriver();
            if (WebDriverFacade.class.isAssignableFrom(driver.getClass())) {
                ((WebDriverFacade) driver).reset();
            }
        }

    }

    public boolean driverIsRegisteredFor(String driverName) {
        return driverMap.containsKey(normalized(driverName));
    }

    public WebDriver useDriver(final String driverName) {
        this.currentDriver = normalized(driverName);
        return driverMap.get(currentDriver);
    }

    public Set<WebDriver> closeAllDrivers() {
        Collection<WebDriver> openDrivers = driverMap.values();
        Set<WebDriver> closedDrivers = new HashSet<WebDriver>(openDrivers);
        for(WebDriver driver : openDrivers) {
            closeAndQuit(driver);
        }
        driverMap.clear();
        currentDriver = null;
        return closedDrivers;
    }

    public int getActiveWebdriverCount() {
        return driverMap.size();
    }

    public final class InstanceRegistration {
        private final String driverName;

        public InstanceRegistration(final String driverName) {
            this.driverName = normalized(driverName);
        }


        public void forDriver(final WebDriver driver) {
            driverMap.put(normalized(driverName), driver);
            currentDriver = normalized(driverName);
        }
    }

    public InstanceRegistration registerDriverCalled(final String driverName) {
        return new InstanceRegistration(normalized(driverName));
    }

    private String normalized(String name) {
        if (StringUtils.isEmpty(name)) {
            return WebDriverFactory.DEFAULT_DRIVER;
        } else {
            return name.toLowerCase();
        }
    }
}
