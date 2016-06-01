package net.thucydides.core.webdriver;

import com.google.common.collect.Lists;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.util.*;


/**
 * One or more WebDriver drivers that are being used in a test.
 */
public class WebdriverInstances {

    private final Map<String, WebDriver> driverMap;

    private String currentDriver;

    private EnvironmentVariables environmentVariables;

    public WebdriverInstances() {
        this.driverMap = new HashMap<>();
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    public WebDriver getCurrentDriver() {
        if (driverMap.containsKey(currentDriver)) {
            return driverMap.get(currentDriver);
        } else {
            return null;
        }
    }

    public String getCurrentDriverName() {
        return currentDriver == null ? "" : currentDriver;
    }

    public String getCurrentDriverType() {
        if (getCurrentDriver() == null) {
            return "";
        }
        if (getCurrentDriver() instanceof WebDriverFacade) {
            return ((WebDriverFacade) getCurrentDriver()).getDriverName();
        }
        if (getCurrentDriver().getClass().getName().contains("Mockito")) {
            return SupportedWebDriver.forClass(getCurrentDriver().getClass().getSuperclass()).name().toLowerCase();
        }
        return "";
    }

    public WebDriver closeCurrentDriver() {
        WebDriver closedDriver = null;
        if (getCurrentDriver() != null) {
            closedDriver = getCurrentDriver();
            closeAndQuit(closedDriver);
            driverMap.remove(currentDriver);
            currentDriver = null;
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
        // this.currentDriver = normalized(driverName);
        return driverMap.get(normalized(driverName));
    }

    public Set<WebDriver> closeAllDrivers() {
        Collection<WebDriver> openDrivers = driverMap.values();
        Set<WebDriver> closedDrivers = new HashSet<WebDriver>(openDrivers);
        for (WebDriver driver : openDrivers) {
            closeAndQuit(driver);
        }
        driverMap.clear();
        currentDriver = null;
        return closedDrivers;
    }

    public int getActiveWebdriverCount() {
        return driverMap.size();
    }

    public boolean hasAnInstantiatedDriver() {
        for (WebDriver driver : driverMap.values()) {
            if (isInstantiated(driver)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInstantiated(WebDriver driver) {

        if (driver instanceof WebDriverFacade) {
            return (((WebDriverFacade) driver).isInstantiated());
        }
        return (driver != null);
    }

    public void setCurrentDriverTo(WebDriver driver) {
        if (registeredDriverNameFor(driver) == null) {
            ThucydidesWebDriverSupport.initialize();
            ThucydidesWebDriverSupport.getWebdriverManager().registerDriver(driver);
        }
        currentDriver = driverNameFor(driver);
    }

    private String registeredDriverNameFor(WebDriver driver) {
        for (String driverName : driverMap.keySet()) {
            WebDriver mappedDriver = driverMap.get(driverName);
            if (matchingDriver(mappedDriver, driver)) {
                return driverName;
            }
        }
        return null;
    }

    private String driverNameFor(WebDriver driver) {
        String driverName = registeredDriverNameFor(driver);
        if (driverName != null) {
            return driverName;
        }

        throw new IllegalStateException("No matching driver found for " + driverName + " in this thread");
    }

    private boolean matchingDriver(WebDriver mappedDriver, WebDriver driver) {
        if (mappedDriver == driver) {
            return true;
        }

        return ((driver instanceof WebDriverFacade) && (mappedDriver == ((WebDriverFacade) driver).proxiedWebDriver));
    }


    public List<
            WebDriver> getActiveDrivers() {
        List<WebDriver> activeDrivers = Lists.newArrayList();
        for (WebDriver webDriver : driverMap.values()) {
            if (!(webDriver instanceof WebDriverFacade)) {
                activeDrivers.add(webDriver);
                continue;
            }

            if (((WebDriverFacade) webDriver).isInstantiated()) {
                activeDrivers.add(webDriver);
            }
        }
        return activeDrivers;
    }

    public List<String> getActiveDriverTypes() {
        List<String> activeDrivers = Lists.newArrayList();
        for (WebDriver webDriver : driverMap.values()) {
            if (!(webDriver instanceof WebDriverFacade)) {
                activeDrivers.add(driverNameFor(webDriver));
                continue;
            }

            if (((WebDriverFacade) webDriver).isInstantiated()) {
                activeDrivers.add(driverNameFor(webDriver));
            }
        }
        return activeDrivers;
    }

    public final class InstanceRegistration {
        private final String driverName;

        public InstanceRegistration(final String driverName) {
            this.driverName = normalized(driverName);
        }


        public void forDriver(final WebDriver driver) {
            driverMap.put(normalized(driverName), driver);
//            currentDriver = normalized(driverName);
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
