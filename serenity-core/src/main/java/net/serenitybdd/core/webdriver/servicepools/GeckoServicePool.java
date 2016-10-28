package net.serenitybdd.core.webdriver.servicepools;

import com.google.common.base.Optional;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_GECKO_DRIVER;

public class GeckoServicePool extends DriverServicePool<GeckoDriverService> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected String serviceName() {
        return "gecko";
    }

    @Override
    protected WebDriver newDriverInstance(Capabilities capabilities) {
        return new FirefoxDriver(capabilities);
    }

    public GeckoServicePool() {
        configureGeckoDriverBinaries();
    }

    @Override
    protected GeckoDriverService newDriverService() {
        return ThreadsafeGeckoDriverService.createThreadsafeService(environmentVariables);
    }

    private void configureGeckoDriverBinaries() {
        Optional<File> geckoBinary = GeckoDriverServiceExecutable.inEnvironment(environmentVariables);

        if (geckoBinary.isPresent()) {
            DriverPathConfiguration.updateSystemProperty(WEBDRIVER_GECKO_DRIVER.getPropertyName())
                    .withExecutablePath(geckoBinary.get());
        }
    }
}
