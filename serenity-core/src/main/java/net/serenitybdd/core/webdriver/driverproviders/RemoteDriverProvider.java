package net.serenitybdd.core.webdriver.driverproviders;

import com.google.common.base.Splitter;
import net.serenitybdd.core.buildinfo.DriverCapabilityRecord;
import net.serenitybdd.core.exceptions.SerenityManagedException;
import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.steps.StepEventBus;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.CapabilityEnhancer;
import net.thucydides.core.webdriver.capabilities.BrowserStackRemoteDriverCapabilities;
import net.thucydides.core.webdriver.capabilities.SauceRemoteDriverCapabilities;
import net.thucydides.core.webdriver.stubs.WebDriverStub;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.WebDriverFactory.getBrowserStackDriverFrom;
import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;
import static net.thucydides.core.webdriver.WebDriverFactory.getSaucelabsDriverFrom;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class RemoteDriverProvider implements DriverProvider {

    private final EnvironmentVariables environmentVariables;
    private final CapabilityEnhancer enhancer;
    private final DriverCapabilityRecord driverProperties;

    private final BrowserStackRemoteDriverCapabilities browserStackRemoteDriverCapabilities;
    private final SauceRemoteDriverCapabilities sauceRemoteDriverCapabilities;
    private final DriverCapabilities remoteDriverCapabilities;

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteDriverProvider.class);


    public RemoteDriverProvider(EnvironmentVariables environmentVariables, CapabilityEnhancer enhancer) {
        this.environmentVariables = environmentVariables;
        this.enhancer = enhancer;
        this.driverProperties = Injectors.getInjector().getInstance(DriverCapabilityRecord.class);

        this.browserStackRemoteDriverCapabilities = new BrowserStackRemoteDriverCapabilities(environmentVariables);
        this.sauceRemoteDriverCapabilities = new SauceRemoteDriverCapabilities(environmentVariables);
        this.remoteDriverCapabilities = new DriverCapabilities(environmentVariables,enhancer);
    }

    @Override
    public WebDriver newInstance() throws MalformedURLException {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }
        WebDriver driver;

        if (saucelabsUrlIsDefined()) {
            driver = buildSaucelabsDriver();
        } else if (browserStackUrlIsDefined()){
            driver = buildBrowserStackDriver();
        } else {
            driver = buildRemoteDriver();
        }
        Augmenter augmenter = new Augmenter();
        return augmenter.augment(driver);
    }

    private boolean saucelabsUrlIsDefined() {
        return StringUtils.isNotEmpty(sauceRemoteDriverCapabilities.getUrl());
    }

    private boolean browserStackUrlIsDefined() {
        return StringUtils.isNotEmpty(browserStackRemoteDriverCapabilities.getUrl());
    }

    private WebDriver buildSaucelabsDriver() throws MalformedURLException {
        String saucelabsUrl = sauceRemoteDriverCapabilities.getUrl();
        WebDriver driver = newRemoteDriver(new URL(saucelabsUrl), findSaucelabsCapabilities());

        if (isNotEmpty(ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.from(environmentVariables))) {
            int implicitWait = ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.integerFrom(environmentVariables, 30);

            driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        }
        return driver;
    }

    private WebDriver buildBrowserStackDriver() throws MalformedURLException{
        String browserStackUrl = browserStackRemoteDriverCapabilities.getUrl();
        WebDriver driver = newRemoteDriver(new URL(browserStackUrl), findbrowserStackCapabilities());
        return driver;
    }

    private WebDriver buildRemoteDriver() throws MalformedURLException {
        String remoteUrl = ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.from(environmentVariables);
        Capabilities capabilities = buildRemoteCapabilities();
        LOGGER.info("Building remote driver with capabilitites " + capabilities);
        return newRemoteDriver(new URL(remoteUrl), capabilities);
    }

    private Capabilities findSaucelabsCapabilities() {

        String driver = getSaucelabsDriverFrom(environmentVariables);
        DesiredCapabilities capabilities = remoteDriverCapabilities.forDriver(driver);

        return sauceRemoteDriverCapabilities.getCapabilities(capabilities);
    }


    private Capabilities findbrowserStackCapabilities() {

        String driver = getBrowserStackDriverFrom(environmentVariables);
        DesiredCapabilities capabilities = remoteDriverCapabilities.forDriver(driver);

        return browserStackRemoteDriverCapabilities.getCapabilities(capabilities);

    }

    private Capabilities buildRemoteCapabilities() {
        String driver = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables);
        if (driver == null) {
            driver = getDriverFrom(environmentVariables);
        }
        return remoteDriverCapabilities.forDriver(driver);
    }

    public WebDriver newRemoteDriver(URL remoteUrl, Capabilities capabilities) {
        if (StepEventBus.getEventBus().webdriverCallsAreSuspended()) {
            return new WebDriverStub();
        }


        try {
            ensureHostIsAvailableAt(remoteUrl);

            RemoteWebDriver driver = new RemoteWebDriver(remoteUrl, capabilities);
            driverProperties.registerCapabilities(capabilities.getBrowserName(), driver.getCapabilities());
            return driver;
        } catch (UnreachableBrowserException unreachableBrowser) {
            String errorMessage = unreachableBrowserErrorMessage(unreachableBrowser);
            throw new SerenityManagedException(errorMessage, unreachableBrowser);
        } catch (UnknownHostException unknownHost) {
            throw new SerenityManagedException(unknownHost.getMessage(), unknownHost);
        }
    }

    private void ensureHostIsAvailableAt(URL remoteUrl) throws UnknownHostException {
        InetAddress.getByName(remoteUrl.getHost());
    }

    private String unreachableBrowserErrorMessage(Exception unreachableBrowser) {
        List<String> errorLines =  Splitter.onPattern("\n").splitToList(unreachableBrowser.getLocalizedMessage());
        Throwable cause = unreachableBrowser.getCause();
        String errorCause = ((cause == null) ? "" :
                System.lineSeparator() + cause.getClass().getSimpleName() + " - " + cause.getLocalizedMessage());
        return errorLines.get(0) + errorCause;
    }

}
