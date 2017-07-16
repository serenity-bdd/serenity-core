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
    private final EnvironmentVariables environmentVariables;
    private final DriverCapabilities remoteDriverCapabilities;

    SaucelabsDriverBuilder(EnvironmentVariables environmentVariables,
                                  DriverCapabilities remoteDriverCapabilities) {
        this.environmentVariables = environmentVariables;
        this.saucelabsRemoteDriverCapabilities = new SaucelabsRemoteDriverCapabilities(environmentVariables);
        this.remoteDriverCapabilities = remoteDriverCapabilities;
    }


    WebDriver buildWithOptions(String options) throws MalformedURLException {
        String saucelabsUrl = saucelabsRemoteDriverCapabilities.getUrl();
        WebDriver driver = newRemoteDriver(new URL(saucelabsUrl), findSaucelabsCapabilities(options));

        if (isNotEmpty(ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.from(environmentVariables))) {
            int implicitWait = ThucydidesSystemProperty.SAUCELABS_IMPLICIT_TIMEOUT.integerFrom(environmentVariables, 30);

            driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
        }
        return driver;
    }

    private Capabilities findSaucelabsCapabilities(String options) {

        String driver = getSaucelabsDriverFrom(environmentVariables);
        DesiredCapabilities capabilities = remoteDriverCapabilities.forDriver(driver, options);

        return saucelabsRemoteDriverCapabilities.getCapabilities(capabilities);
    }

}
