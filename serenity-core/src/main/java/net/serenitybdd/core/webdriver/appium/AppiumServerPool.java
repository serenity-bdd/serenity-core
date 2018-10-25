package net.serenitybdd.core.webdriver.appium;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    private static final String DEFAULT_URL = "http://127.0.0.1:4723/wd/hub";

    private static AppiumServerPool pool;

    private Map<String, DriverService> appiumServers = new HashMap<>();
    private Map<Thread, Set<DriverService>> serversByThread = new HashMap<>();

    private Optional<String> defaultHubUrl = Optional.empty();

    protected AppiumServerPool(EnvironmentVariables environmentVariables) {
        Optional<String> configuredAppiumHub = Optional.ofNullable(environmentVariables.getProperty("appium.hub"));

        if (configuredAppiumHub.isPresent() && AppiumDevicePool.instance(environmentVariables).hasOnlyOneDevice()) {
            System.out.println("Using configured default hub url " + configuredAppiumHub.get());
            defaultHubUrl = Optional.of(configuredAppiumHub.get());
        } else if (AppiumDevicePool.instance(environmentVariables).hasOnlyOneDevice()) {
            String defaultUrl = startDefaultAppiumServer().toString();
            System.out.println("Using default hub url " + defaultUrl);
            defaultHubUrl = Optional.of(defaultUrl);
        }
    }

    public synchronized static AppiumServerPool instance(EnvironmentVariables environmentVariables) {
        if (pool == null) {
            pool = new AppiumServerPool(environmentVariables);
        }
        return pool;
    }

    public synchronized static AppiumServerPool instance() {
        return instance(Injectors.getInjector().getInstance(EnvironmentVariables.class));
    }

    public URL urlFor(String deviceName) {
        System.out.println("Finding URL for device " + deviceName);
        if (defaultHubUrl.isPresent()) {
            System.out.println("  -> Using default URL " + defaultHubUrl.get());
            return configuredAppiumUrl(defaultHubUrl.get());
        } else {
            URL localServerUrl = localServerUrlFor(deviceName);
            System.out.println("  -> Using local server URL " + localServerUrl);
            return localServerUrl;
        }
    }

    private URL localServerUrlFor(String deviceName) {
        System.out.println("Finding local appium server for " + deviceName);
        if (appiumServers.get(deviceName) != null) {
            return appiumServers.get(deviceName).getUrl();
        }

        System.out.println("No local appium server found for " + deviceName + " - starting a new one");
        DriverService appiumDriverService = AppiumDriverLocalService.buildService(new AppiumServiceBuilder().usingAnyFreePort());
        try {
            System.out.print("Starting service...");
            appiumDriverService.start();
            System.out.println("Service started");
            appiumServers.put(deviceName, appiumDriverService);
            index(appiumDriverService);
            System.out.println("Local appium server for " + deviceName + " started on " + appiumDriverService.getUrl());
            return appiumDriverService.getUrl();
        } catch (Throwable e) {
            System.out.println("Failed to start appium service on " + appiumDriverService.getUrl());
            e.printStackTrace();

            LOGGER.error("Failed to start appium service on " + appiumDriverService.getUrl());
            throw new WebDriverException("Failed to start appium service on " + appiumDriverService.getUrl(), e);
        }
    }


    private URL startDefaultAppiumServer() {
        System.out.println("Starting the default appium server");
        DriverService appiumDriverService = AppiumDriverLocalService.buildDefaultService();
        index(appiumDriverService);
        return appiumDriverService.getUrl();
    }


    private void index(DriverService appiumDriverService) {
        serversByThread.putIfAbsent(Thread.currentThread(), new HashSet<>());
        serversByThread.get(Thread.currentThread()).add(appiumDriverService);
    }

    private URL configuredAppiumUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid appium hub: " + url);
        }
    }

    public void shutdownAllServersRunningOnThread(Thread thread) {
        serversByThread.getOrDefault(thread, new HashSet<>()).forEach(
                service -> {
                    LOGGER.info("Shutting down Appium server on " + service.getUrl());
                    System.out.println("Shutting down Appium server on " + service.getUrl());
                    if (service.isRunning()) {
                        service.stop();
                        System.out.println("Service stopped");
                    } else {
                        System.out.println("Service was already stopped");
                    }
                }
        );
        serversByThread.remove(thread);
    }

    public Set<URL> getActiveServersInCurrentThread() {
        return serversByThread.getOrDefault(Thread.currentThread(), new HashSet<>())
                              .stream()
                               .map(DriverService::getUrl)
                               .collect(Collectors.toSet());
    }
}
