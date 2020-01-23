package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static net.thucydides.core.webdriver.WebDriverFactory.getDriverFrom;

class DefaultRemoteDriver extends RemoteDriverBuilder {
    private final DriverCapabilities remoteDriverCapabilities;

    DefaultRemoteDriver(EnvironmentVariables environmentVariables, DriverCapabilities remoteDriverCapabilities) {
        super(environmentVariables);
        this.remoteDriverCapabilities = remoteDriverCapabilities;
    }

    WebDriver buildWithOptions(String options) throws MalformedURLException {
        String remoteUrl = ThucydidesSystemProperty.WEBDRIVER_REMOTE_URL.from(environmentVariables);
        Capabilities capabilities = buildRemoteCapabilities(options);
        return newRemoteDriver(new URL(remoteUrl), capabilities, options);
    }

    private Capabilities buildRemoteCapabilities(String options) {
        String driver = ThucydidesSystemProperty.WEBDRIVER_REMOTE_DRIVER.from(environmentVariables);
        if (driver == null) {
            driver = getDriverFrom(environmentVariables);
        }
        return remoteDriverCapabilities.forDriver(driver, options);
    }

}
