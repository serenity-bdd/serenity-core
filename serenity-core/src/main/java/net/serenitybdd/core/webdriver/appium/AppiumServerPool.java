package net.serenitybdd.core.webdriver.appium;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Appium Service pool is responsible for starting up and shutting down Appium servers.
 * A server is associated with a device.
 * <p>
 * - If a single deviceName is specified along with a hub, the specified hub will be used
 * - If only a single deviceName is specified, the default hub will be used
 * - If a set of device names are specified, a new service will be started for each device
 *
 * If the appium.hub property is defined, the service is assumed to be running before the test starts.
 * Otherwise, a new server will be started.
 */
public class AppiumServerPool {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppiumServerPool.class);

    private static AppiumServerPool pool;

    private final Map<String, DriverService> appiumServers = Collections.synchronizedMap(new HashMap<>());
    private final Map<Thread, Set<DriverService>> serversByThread = Collections.synchronizedMap(new HashMap<>());

    private Optional<String> defaultHubUrl = Optional.empty();

    protected AppiumServerPool(EnvironmentVariables environmentVariables) {
        Optional<String> configuredAppiumHub = EnvironmentSpecificConfiguration.from(environmentVariables)
                                                                               .getOptionalProperty("appium.hub");

//        Optional<String> configuredAppiumHub = Optional.ofNullable(environmentVariables.getProperty("appium.hub"));

        if (configuredAppiumHub.isPresent() && AppiumDevicePool.instance(environmentVariables).hasOnlyOneDevice()) {
            LOGGER.info("Using configured default hub url " + configuredAppiumHub.get());
            defaultHubUrl = Optional.of(configuredAppiumHub.get());
        } else if (AppiumDevicePool.instance(environmentVariables).hasOnlyOneDevice()) {
            String defaultUrl = startDefaultAppiumServer().toString();
            LOGGER.info("Using default hub url " + defaultUrl);
            defaultHubUrl = Optional.of(defaultUrl);
        }
    }

    private void logStatus() {
        synchronized(appiumServers) {
            appiumServers.values().forEach(
                    server -> LOGGER.info("Server status for " + server.getUrl() + " : is running = " + server.isRunning())
            );
        }
    }

    public synchronized static AppiumServerPool instance(EnvironmentVariables environmentVariables) {
        if (pool == null) {
            pool = new AppiumServerPool(environmentVariables);
        }
        return pool;
    }

    public synchronized static AppiumServerPool instance() {
        return instance(SystemEnvironmentVariables.currentEnvironmentVariables());
    }

    public URL urlFor(String deviceName) {
        logStatus();
        LOGGER.info("Finding URL for device " + deviceName);
        if (defaultHubUrl.isPresent()) {
            LOGGER.info("  -> Using default URL " + defaultHubUrl.get());
            return configuredAppiumUrl(defaultHubUrl.get());
        } else {
            URL localServerUrl = localServerUrlFor(deviceName);
            LOGGER.info("  -> Using local server URL " + localServerUrl);
            return localServerUrl;
        }
    }

    private URL localServerUrlFor(String deviceName) {
        LOGGER.info("Finding local appium server for " + deviceName);
        DriverService appiumDriverService = null;
        try {
            if (appiumServers.get(deviceName) != null) {
                appiumDriverService = appiumServers.get(deviceName);
                if(!appiumDriverService.isRunning()) {
                    LOGGER.info("  -> Restarting local appium server " + appiumDriverService.getUrl());
                    appiumDriverService.start();
                }
                return appiumDriverService.getUrl();
            }
            LOGGER.info("No local appium server found for " + deviceName + " - starting a new one");
            appiumDriverService = AppiumDriverLocalService.buildService(new AppiumServiceBuilder().withIPAddress("127.0.0.1").
                                    withArgument(GeneralServerFlag.SESSION_OVERRIDE).usingAnyFreePort());

            LOGGER.info("Starting service...");
            appiumDriverService.start();
            LOGGER.info("Service started: " + appiumDriverService.getUrl());
            appiumServers.put(deviceName, appiumDriverService);
            index(appiumDriverService);
            LOGGER.info("Local appium server for " + deviceName + " started on " + appiumDriverService.getUrl());
            logStatus();
            return appiumDriverService.getUrl();
        } catch (Throwable e) {
            LOGGER.info("Failed to start appium service on " + appiumDriverService.getUrl());
            e.printStackTrace();

            LOGGER.error("Failed to start appium service on " + appiumDriverService.getUrl());
            throw new WebDriverException("Failed to start appium service on " + appiumDriverService.getUrl(), e);
        }
    }


    private URL startDefaultAppiumServer() {
        LOGGER.info("Starting the default appium server");
        DriverService appiumDriverService = AppiumDriverLocalService.buildDefaultService();
        index(appiumDriverService);
        return appiumDriverService.getUrl();
    }


    private void index(DriverService appiumDriverService) {
        synchronized(serversByThread) {
            if (serversByThread.get(Thread.currentThread()) == null) {
                serversByThread.put(Thread.currentThread(), new HashSet<>());
            }
            serversByThread.get(Thread.currentThread()).add(appiumDriverService);
        }
    }

    private URL configuredAppiumUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid appium hub: " + url);
        }
    }

    public void shutdownAllServersRunningOnThread(Thread thread) {
        synchronized(serversByThread) {
            serversByThread.getOrDefault(thread, new HashSet<>()).forEach(
                    service -> {
                        LOGGER.info("Shutting down Appium server on " + service.getUrl());
                        if (service.isRunning()) {
                            service.stop();
                            LOGGER.info("Service stopped");
                        } else {
                            LOGGER.info("Service was already stopped");
                        }
                    }
            );
            serversByThread.remove(thread);
        }
    }

    public Set<URL> getActiveServersInCurrentThread() {
        synchronized(serversByThread) {
            return serversByThread.getOrDefault(Thread.currentThread(), new HashSet<>())
                    .stream()
                    .map(DriverService::getUrl)
                    .collect(Collectors.toSet());
        }
    }
}
