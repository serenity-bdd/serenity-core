package net.serenitybdd.core.webdriver.servicepools;

import net.serenitybdd.core.webdriver.FirefoxOptionsEnhancer;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

import static net.thucydides.model.ThucydidesSystemProperty.WEBDRIVER_GECKO_DRIVER;

public class GeckoServicePool extends DriverServicePool<GeckoDriverService> {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected String serviceName() {
        return "gecko";
    }

    @Override
    protected WebDriver newDriverInstance(Capabilities capabilities) {
        FirefoxOptions options = new FirefoxOptions(capabilities);

        FirefoxOptionsEnhancer.enhanceOptions(options).using(environmentVariables);

        return new FirefoxDriver(options);
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
