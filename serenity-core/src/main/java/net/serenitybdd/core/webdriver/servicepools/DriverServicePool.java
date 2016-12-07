package net.serenitybdd.core.webdriver.servicepools;

import com.google.common.collect.Maps;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public abstract class DriverServicePool<T extends DriverService> {

    private static final ConcurrentMap<String, DriverService> DRIVER_SERVICES = Maps.newConcurrentMap();

    protected final EnvironmentVariables environmentVariables;

    protected abstract T newDriverService();

    protected abstract String serviceName();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Used as a fallback if the driver service cannot be used for some reason.
     *
     * @param capabilities
     */
    protected abstract WebDriver newDriverInstance(Capabilities capabilities);

    public DriverServicePool() {
        this(ConfiguredEnvironment.getEnvironmentVariables());
    }

    public DriverServicePool(EnvironmentVariables environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    protected T getDriverService() {
        return driverServiceWithName(serviceName());
    }

    private T driverServiceWithName(String serviceName) {

        if (DRIVER_SERVICES.get(serviceName) != null) {
            return (T) DRIVER_SERVICES.get(serviceName);
        }

        DRIVER_SERVICES.putIfAbsent(serviceName, newDriverService());

        return (T) DRIVER_SERVICES.get(serviceName);
    }

    public synchronized void start() throws IOException {
        if (!getDriverService().isRunning()) {
            getDriverService().start();
        }
    }

    public synchronized void shutdown() {
        if (getDriverService().isRunning()) {
            getDriverService().stop();
        }
    }

    public WebDriver newDriver(Capabilities capabilities) throws IOException {
        try {
            logger.debug("Creating new driver instance with capabilities: {}", capabilities);
            return new RemoteWebDriver(getDriverService().getUrl(), capabilities);
        } catch (WebDriverException couldNotReachDriverService) {
            logger.warn("Remote driver creation failed ({}), falling back on default driver creation", couldNotReachDriverService.getMessage().split("\n")[0]);
            return newDriverInstance(capabilities);
        }
    }

    public boolean isRunning() {
        return getDriverService().isRunning();
    }

    public void ensureServiceIsRunning() throws IOException {
        start();
    }
}