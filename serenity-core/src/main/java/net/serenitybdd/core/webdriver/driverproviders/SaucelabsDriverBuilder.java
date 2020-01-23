package net.serenitybdd.core.webdriver.driverproviders;

import net.thucydides.core.ThucydidesSystemProperty;
import net.thucydides.core.util.EnvironmentVariables;
import net.thucydides.core.webdriver.capabilities.SaucelabsRemoteDriverCapabilities;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static net.thucydides.core.webdriver.WebDriverFactory.getSaucelabsDriverFrom;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

class SaucelabsDriverBuilder extends RemoteDriverBuilder {

    private final SaucelabsRemoteDriverCapabilities saucelabsRemoteDriverCapabilities;
    private final DriverCapabilities remoteDriverCapabilities;

    SaucelabsDriverBuilder(EnvironmentVariables environmentVariables,
                           DriverCapabilities remoteDriverCapabilities) {
        super(environmentVariables);
        this.saucelabsRemoteDriverCapabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        this.remoteDriverCapabilities = remoteDriverCapabilities;
    }


    WebDriver buildWithOptions(String options) throws MalformedURLException {
        String saucelabsUrl = saucelabsRemoteDriverCapabilities.getUrl();

        WebDriver driver = newRemoteDriver(new URL(saucelabsUrl), findSaucelabsCapabilities(options), options);

        setImplicitTimeoutsFor(driver);

        return driver;
    }

    private void setImplicitTimeoutsFor(WebDriver driver) {
        if (isNotEmpty(ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.from(environmentVariables))) {
            int implicitWait = ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.integerFrom(environmentVariables, 30);

            driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        }
    }

    private Capabilities findSaucelabsCapabilities(String options) {

        String driver = getSaucelabsDriverFrom(environmentVariables);
        DesiredCapabilities capabilities = remoteDriverCapabilities.forDriver(driver, options);

        SetProxyConfiguration.from(environmentVariables).in(capabilities);
        AddLoggingPreferences.from(environmentVariables).to(capabilities);

        return saucelabsRemoteDriverCapabilities.getCapabilities(capabilities);
    }

}
