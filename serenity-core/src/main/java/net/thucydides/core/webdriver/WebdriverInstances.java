package net.thucydides.core.webdriver;

import net.serenitybdd.core.pages.DefaultTimeouts;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Duration;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    public boolean isDriverInstantiated() {
        if (getCurrentDriver() instanceof WebDriverFacade) {
            return ((WebDriverFacade) getCurrentDriver()).isInstantiated();
        } else {
            return (getCurrentDriver() != null);
        }
    }

    public Duration getCurrentImplicitTimeout() {
        if (getCurrentDriver() instanceof ConfigurableTimeouts) {
            return ((ConfigurableTimeouts) getCurrentDriver()).getCurrentImplicitTimeout();
        } else {
            return getDefaultImplicitTimeout();
        }
    }

    protected Duration getDefaultImplicitTimeout() {
        Integer configuredTimeout = ThucydidesSystemProperty.WEBDRIVER_TIMEOUTS_IMPLICITLYWAIT.integerFrom(environmentVariables, -1);
        return (configuredTimeout >= 0) ? new Duration(configuredTimeout, TimeUnit.MILLISECONDS)
                : DefaultTimeouts.DEFAULT_IMPLICIT_WAIT_TIMEOUT;
    }

    public void setCurrentDriverTo(WebDriver driver) {
        currentDriver = driverNameFor(driver);
    }

    private String driverNameFor(WebDriver driver) {
        for(String driverName : driverMap.keySet()) {
            if (driverMap.get(driverName) == driver) {
                return driverName;
            }
        }
        throw new IllegalStateException("No matching driver found in this thread");
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
