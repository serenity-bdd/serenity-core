package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.BrowserStackRemoteDriverCapabilities;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

import static net.thucydides.core.webdriver.WebDriverFactory.getBrowserStackDriverFrom;

class BrowserStackDriverBuilder extends RemoteDriverBuilder {
    private final DriverCapabilities remoteDriverCapabilities;

    private final BrowserStackRemoteDriverCapabilities browserStackRemoteDriverCapabilities;

    BrowserStackDriverBuilder(EnvironmentVariables environmentVariables, DriverCapabilities remoteDriverCapabilities) {
        super(environmentVariables);
        this.remoteDriverCapabilities = remoteDriverCapabilities;
        this.browserStackRemoteDriverCapabilities = new BrowserStackRemoteDriverCapabilities(environmentVariables);
    }

    WebDriver buildWithOptions(String options) throws MalformedURLException {
        String browserStackUrl = browserStackRemoteDriverCapabilities.getUrl();
        return newRemoteDriver(new URL(browserStackUrl), findbrowserStackCapabilities(options), options);
    }

    private Capabilities findbrowserStackCapabilities(String options) {

        String driver = getBrowserStackDriverFrom(environmentVariables);
        DesiredCapabilities capabilities = remoteDriverCapabilities.forDriver(driver, options);

        return browserStackRemoteDriverCapabilities.getCapabilities(capabilities);

    }
}
