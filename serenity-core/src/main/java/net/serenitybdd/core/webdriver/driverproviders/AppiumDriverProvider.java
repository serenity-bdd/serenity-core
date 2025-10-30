package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.eventbus.Subscribe;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.windows.WindowsDriver;
import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.webdriver.appium.AppiumDevicePool;
import net.serenitybdd.core.webdriver.appium.AppiumServerPool;
import net.serenitybdd.model.buildinfo.DriverCapabilityRecord;
import net.thucydides.core.events.TestLifecycleEvents;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.steps.TestContext;
import net.thucydides.core.webdriver.*;
import net.thucydides.core.webdriver.appium.AppiumConfiguration;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import net.thucydides.model.ThucydidesSystemProperty;
import net.thucydides.model.util.EnvironmentVariables;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static net.thucydides.core.webdriver.SupportedWebDriver.*;
import static net.thucydides.model.ThucydidesSystemProperty.MANAGE_APPIUM_SERVERS;

public class AppiumDriverProvider implements DriverProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppiumDriverProvider.class);

    private final DriverCapabilityRecord driverProperties;

    private final FixtureProviderService fixtureProviderService;

    public AppiumDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = SerenityInfrastructure.getDriverCapabilityRecord();
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (MANAGE_APPIUM_SERVERS.booleanFrom(environmentVariables, false)) {
            return newDriverUsingManagedAppiumServers(options, environmentVariables);
        } else {
            return newDriverUsingExternalServer(options, environmentVariables);
        }
    }

    private WebDriver newDriverUsingExternalServer(String options, EnvironmentVariables environmentVariables) {
        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);

        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        switch (appiumTargetPlatform(environmentVariables)) {
            case ANDROID:
                AndroidDriver androidDriver = new AndroidDriver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables), ANDROID) );
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(androidDriver.getCapabilities()));
                return androidDriver;
            case IOS:
                IOSDriver iosDriver = new IOSDriver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables), IPHONE));
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(iosDriver.getCapabilities()));
                return iosDriver;
            case WINDOWS:
            	WindowsDriver windowsDriver = new WindowsDriver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables), REMOTE));
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(windowsDriver.getCapabilities()));
                return windowsDriver;
            case MAC:
                Mac2Driver macDriver = new Mac2Driver(appiumUrl(environmentVariables), enhancer.enhanced(appiumCapabilities(options,environmentVariables), REMOTE));
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(macDriver.getCapabilities()));
                return macDriver;
        }
        throw new DriverConfigurationError(appiumTargetPlatform(environmentVariables).name());
    }

    public WebDriver newDriverUsingManagedAppiumServers(String options, EnvironmentVariables environmentVariables) {

        LOGGER.info("Creating a new appium driver instance with options " + options);

        EnvironmentVariables testEnvironmentVariables = environmentVariables.copy();
        String deviceName = AppiumDevicePool.instance(testEnvironmentVariables).requestDevice();
        LOGGER.info("  - Using deviceName " + deviceName);

        URL appiumUrl = appiumUrl(testEnvironmentVariables, deviceName);
        LOGGER.info("  - Using appium server at " + appiumUrl);

        testEnvironmentVariables.setProperty(ThucydidesSystemProperty.APPIUM_DEVICE_NAME.getPropertyName(), deviceName);
        testEnvironmentVariables.setProperty(ThucydidesSystemProperty.APPIUM_UDID.getPropertyName(), deviceName);
        testEnvironmentVariables.clearProperty(ThucydidesSystemProperty.APPIUM_DEVICE_NAMES.getPropertyName());

        CapabilityEnhancer enhancer = new CapabilityEnhancer(testEnvironmentVariables, fixtureProviderService);

        if (StepEventBus.getParallelEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        switch (appiumTargetPlatform(testEnvironmentVariables)) {
            case ANDROID:
                LOGGER.info("  - Using android appium server at " + appiumUrl);
                AndroidDriver androidDriver = null;
                try {
                    MutableCapabilities enhancedOptions = enhancer.enhanced(appiumCapabilities(options, testEnvironmentVariables), ANDROID);
                    LOGGER.info("  - Using appium capabilities " +  enhancedOptions);
                    TestContext.forTheCurrentTest().recordBrowserConfiguration(enhancedOptions);
                    TestContext.forTheCurrentTest().recordCurrentPlatform();
                    androidDriver = new AndroidDriver(appiumUrl, enhancedOptions);
                } catch(Throwable e) {
                    LOGGER.error("Creating ANDROID Driver failed " + androidDriver, e);
                    throw e;
                }
                driverProperties.registerCapabilities("appium", capabilitiesToProperties(androidDriver.getCapabilities()));
                WebDriverInstanceEvents.bus().register(listenerFor(androidDriver, deviceName));
                LOGGER.info("  -> driver created" + androidDriver);
                return androidDriver;
            case IOS:
                LOGGER.info("  - Using ios appium server at " + appiumUrl);
                MutableCapabilities enhancedOptions = enhancer.enhanced(appiumCapabilities(options,testEnvironmentVariables), IPHONE);
                LOGGER.info("  - Using appium capabilities " +  enhancedOptions);
                TestContext.forTheCurrentTest().recordBrowserConfiguration(enhancedOptions);
                TestContext.forTheCurrentTest().recordCurrentPlatform();
                IOSDriver iosDriver = new IOSDriver(appiumUrl, enhancedOptions);

                driverProperties.registerCapabilities("appium", capabilitiesToProperties(iosDriver.getCapabilities()));
                WebDriverInstanceEvents.bus().register(listenerFor(iosDriver, deviceName));
                LOGGER.info("  -> driver created" + iosDriver);
                return iosDriver;
            case WINDOWS:
                LOGGER.info("  - Using windows appium server at " + appiumUrl);
                enhancedOptions = enhancer.enhanced(appiumCapabilities(options,testEnvironmentVariables), REMOTE);
                LOGGER.info("  - Using appium capabilities " +  enhancedOptions);
                TestContext.forTheCurrentTest().recordBrowserConfiguration(enhancedOptions);
                TestContext.forTheCurrentTest().recordCurrentPlatform();
                WindowsDriver windowsDriver = new WindowsDriver(appiumUrl, enhancedOptions);

                driverProperties.registerCapabilities("appium", capabilitiesToProperties(windowsDriver.getCapabilities()));
                WebDriverInstanceEvents.bus().register(listenerFor(windowsDriver, deviceName));
                LOGGER.info("  -> driver created" + windowsDriver);
                return windowsDriver;
            case MAC:
                LOGGER.info("  - Using mac appium server at " + appiumUrl);
                enhancedOptions = enhancer.enhanced(appiumCapabilities(options,testEnvironmentVariables), REMOTE);
                LOGGER.info("  - Using appium capabilities " +  enhancedOptions);
                TestContext.forTheCurrentTest().recordBrowserConfiguration(enhancedOptions);
                TestContext.forTheCurrentTest().recordCurrentPlatform();
                Mac2Driver macDriver = new Mac2Driver(appiumUrl, enhancedOptions);

                driverProperties.registerCapabilities("appium", capabilitiesToProperties(macDriver.getCapabilities()));
                WebDriverInstanceEvents.bus().register(listenerFor(macDriver, deviceName));
                LOGGER.info("  -> driver created" + macDriver);
                return macDriver;
        }
        throw new DriverConfigurationError(appiumTargetPlatform(testEnvironmentVariables).name());

    }

    private DesiredCapabilities appiumCapabilities(String options, EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getCapabilities(options);
    }

    private MobilePlatform appiumTargetPlatform(EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getTargetPlatform();
    }

    private URL appiumUrl(EnvironmentVariables environmentVariables, String deviceName) {
        return AppiumServerPool.instance(environmentVariables).urlFor(deviceName);
    }


    private URL appiumUrl(EnvironmentVariables environmentVariables) {
        return AppiumConfiguration.from(environmentVariables).getUrl();
    }

    private WebDriverInstanceEventListener listenerFor(WebDriver driver, String deviceName) {
        return new AppiumEventListener(driver, deviceName);
    }

    private static class AppiumEventListener implements WebDriverInstanceEventListener {
        private final String deviceName;
        private WebDriver appiumDriver;
        private final AppiumDevicePool devicePool;
        private final Thread currentThread;

        private AppiumEventListener(WebDriver appiumDriver, String deviceName, AppiumDevicePool devicePool) {
            this.appiumDriver = appiumDriver;
            this.deviceName = deviceName;
            this.devicePool = devicePool;
            this.currentThread = Thread.currentThread();

            TestLifecycleEvents.register(this);
        }

        @Subscribe
        public void testFinishes(TestLifecycleEvents.TestFinished testFinished) {
            LOGGER.info("Appium test finished - releasing all devices");
            releaseAllDevicesUsedInThread(Thread.currentThread());
        }

        @Subscribe
        public void testSuiteFinishes(TestLifecycleEvents.TestSuiteFinished testSuiteFinished) {
            LOGGER.info("Appium test suite finished - shutting down servers");
            shutdownAllAppiumServersUsedInThread(Thread.currentThread());
        }

        private AppiumEventListener(WebDriver appiumDriver, String deviceName) {
            this(appiumDriver, deviceName, AppiumDevicePool.instance());
        }

        @Override
        public void close(WebDriver driver) {
            quit(driver);
        }

        @Override
        public void quit(WebDriver driver) {
            if (appiumDriver == driver) {
                devicePool.freeDevice(deviceName);
                appiumDriver = null;
            }
        }

        void releaseAllDevicesUsedInThread(Thread thread) {
            if (appiumDriver != null && currentThread == thread) {
                devicePool.freeDevice(deviceName);
                appiumDriver = null;
            }
        }

        void shutdownAllAppiumServersUsedInThread(Thread thread) {
            AppiumServerPool.instance().shutdownAllServersRunningOnThread(thread);
        }
    }
}
