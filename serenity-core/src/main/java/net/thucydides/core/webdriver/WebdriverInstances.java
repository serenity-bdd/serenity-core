package net.thucydides.core.webdriver;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import org.openqa.selenium.WebDriver;

import java.util.*;


/**
 * One or more WebDriver drivers that are being used in a test.
 */
public class WebdriverInstances {

    private final Map<String, WebDriver> driverMap;
    private final ThreadLocal<Set<String>> driversUsedInCurrentThread;
    private final DriverName driverNamer;

    private String currentDriver;

    public WebdriverInstances() {
        this.driverMap = new HashMap<>();
        this.driversUsedInCurrentThread = new ThreadLocal<>();
        this.driversUsedInCurrentThread.set(Sets.<String>newHashSet());
        this.driverNamer = new DriverName(ConfiguredEnvironment.getEnvironmentVariables());
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
        if (currentDriverIsMocked()) {
            return currentMockedDriverType();
        }
        return "";
    }

    private boolean currentDriverIsMocked() {
        return getCurrentDriver().getClass().getName().contains("Mockito");
    }

    private String currentMockedDriverType() {
        String className = getCurrentDriver().getClass().getName();
        if (className.contains("WebDriver")) {
            return "firefox";
        }
        return SupportedWebDriver.forClass(getCurrentDriver().getClass().getSuperclass()).name().toLowerCase();
    }

    public WebDriver closeCurrentDriver() {
        WebDriver closedDriver = null;
        if (getCurrentDriver() != null) {
            closedDriver = getCurrentDriver();
            closeAndQuit(closedDriver);
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
        return driverMap.containsKey(driverNamer.normalisedFormOf(driverName));
    }

    public WebDriver useDriver(final String driverName) {
        driversUsedInCurrentThread.get().add(driverNamer.normalisedFormOf(driverName));
        return driverMap.get(driverNamer.normalisedFormOf(driverName));
    }

    public Set<WebDriver> closeAllDrivers() {
        Collection<WebDriver> openDrivers = driverMap.values();
        Set<WebDriver> closedDrivers = new HashSet<>(openDrivers);
        for (WebDriver driver : openDrivers) {
            closeAndQuit(driver);
        }
        driverMap.clear();
        clearDriversInCurrentThread();
        currentDriver = null;
        return closedDrivers;
    }

    private void clearDriversInCurrentThread() {
        driversUsedInCurrentThread.get().clear();
    }

    public void closeCurrentDrivers() {
        closeCurrentDriver();
        for(String driverName : driversUsedInCurrentThread.get()) {
            WebDriver openDriver = driverMap.get(driverName);
            if (isInstantiated(openDriver)) {
                closeAndQuit(openDriver);
            }
        }
        currentDriver = null;
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
        if (driverName == null) {
            throw new IllegalStateException("No matching driver found for " + driverName + " in this thread");
        }
        return driverName;
    }

    private boolean matchingDriver(WebDriver mappedDriver, WebDriver driver) {
        if (mappedDriver == driver) {
            return true;
        }

        return ((driver instanceof WebDriverFacade) && (mappedDriver == ((WebDriverFacade) driver).proxiedWebDriver));
    }


    public List<WebDriver> getActiveDrivers() {
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
            this.driverName = driverNamer.normalisedFormOf(driverName);
        }


        public void forDriver(final WebDriver driver) {
            if (!driverMap.values().contains(driver)) {
                driverMap.put(driverName, driver);
            }
        }
    }

    public InstanceRegistration registerDriverCalled(final String driverName) {
        return new InstanceRegistration(driverNamer.normalisedFormOf(driverName));
    }

}
