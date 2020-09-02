package net.serenitybdd.core.webdriver.driverproviders;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.di.WebDriverInjectors;
import net.serenitybdd.core.time.InternalSystemClock;
import net.serenitybdd.core.webdriver.servicepools.DriverServiceExecutable;
import net.serenitybdd.core.webdriver.servicepools.DriverServicePool;
import net.serenitybdd.core.webdriver.servicepools.InternetExplorerServicePool;
import net.thucydides.core.fixtureservices.FixtureProviderService;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.*;
import static net.thucydides.core.webdriver.SupportedWebDriver.IEXPLORER;

public class InternetExplorerDriverProvider implements DriverProvider {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DriverCapabilityRecord driverProperties;
    private static final Logger LOGGER = LoggerFactory.getLogger(InternetExplorerDriverProvider.class);

    private final DriverServicePool driverServicePool = new InternetExplorerServicePool();
    private final EnvironmentVariables environmentVariables;

    private final FixtureProviderService fixtureProviderService;

    public InternetExplorerDriverProvider(FixtureProviderService fixtureProviderService) {
        this.fixtureProviderService = fixtureProviderService;
        this.driverProperties = WebDriverInjectors.getInjector().getInstance(DriverCapabilityRecord.class);
        this.environmentVariables = Injectors.getInjector().getInstance(EnvironmentVariables.class);
    }

    @Override
    public WebDriver newInstance(String options, EnvironmentVariables environmentVariables) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }

        if(isDriverAutomaticallyDownloaded(environmentVariables)) {
            logger.info("Using automatically driver download");
            WebDriverManager.iedriver().setup();
        } else {
            logger.info("Not using automatically driver download");
        }

        updateIEDriverBinaryIfSpecified();

        CapabilityEnhancer enhancer = new CapabilityEnhancer(environmentVariables, fixtureProviderService);
        DesiredCapabilities desiredCapabilities = enhancer.enhanced(recommendedDefaultInternetExplorerCapabilities(), IEXPLORER);

        SetProxyConfiguration.from(environmentVariables).in(desiredCapabilities);
        AddLoggingPreferences.from(environmentVariables).to(desiredCapabilities);

        driverProperties.registerCapabilities("iexplorer", capabilitiesToProperties(desiredCapabilities));

        return ProvideNewDriver.withConfiguration(environmentVariables,
                desiredCapabilities,
                driverServicePool,
                this::retryCreateDriverOnNoSuchSession,
                (pool, caps) -> new InternetExplorerDriver(new InternetExplorerOptions(caps))
        );
    }

    private WebDriver retryCreateDriverOnNoSuchSession(DriverServicePool pool, DesiredCapabilities desiredCapabilities) {
        return new TryAtMost(3).toStartNewDriverWith(pool, desiredCapabilities);
    }

    private class TryAtMost {
        private final int maxTries;

        private TryAtMost(int maxTries) {
            this.maxTries = maxTries;
        }

        public WebDriver toStartNewDriverWith(DriverServicePool pool, DesiredCapabilities desiredCapabilities) {
            try {
                return pool.newDriver(desiredCapabilities);
            } catch (NoSuchSessionException e) {
                if (maxTries == 0) {
                    throw e;
                }

                LOGGER.error(e.getClass().getCanonicalName() + " happened - retrying in 2 seconds");
                new InternalSystemClock().pauseFor(2000);
                return new TryAtMost(maxTries - 1).toStartNewDriverWith(pool, desiredCapabilities);
            }
        }
    }

    private DesiredCapabilities recommendedDefaultInternetExplorerCapabilities() {
        DesiredCapabilities defaults = DesiredCapabilities.internetExplorer();

        defaults.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING,
                               IE_OPTIONS_IGNORE_ZOOM_LEVEL.booleanFrom(environmentVariables, true));
        defaults.setCapability(InternetExplorerDriver.NATIVE_EVENTS,
                               IE_OPTIONS_ENABLE_NATIVE_EVENTS.booleanFrom(environmentVariables, true));
        defaults.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS,
                               IE_OPTIONS_REQUIRE_WINDOW_FOCUS.booleanFrom(environmentVariables, false));
        defaults.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        defaults.setJavascriptEnabled(true);


        /*
        IgnoreZoomLevel = true,
EnableNativeEvents = true, RequireWindowFocus = true};
         */
        defaults = AddEnvironmentSpecifiedDriverCapabilities.from(environmentVariables).forDriver(IEXPLORER).to(defaults);

        if (ACCEPT_INSECURE_CERTIFICATES.booleanFrom(environmentVariables, false)) {
            defaults.acceptInsecureCerts();
        }
        return defaults;
    }

    private void updateIEDriverBinaryIfSpecified() {

        File executable = DriverServiceExecutable.called("InternetExplorerDriver.exe")
                .withSystemProperty(WEBDRIVER_IE_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .reportMissingBinary()
                .downloadableFrom("https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver")
                .asAFile();

        if (executable != null && executable.exists()) {
            System.setProperty("webdriver.ie.driver", executable.getAbsolutePath());
        }
    }


}
