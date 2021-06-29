package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER;

public class ChromeServicePool extends DriverServicePool<ChromeDriverService> {

    @Override
    protected String serviceName(){ return "chrome"; }

    @Override
    protected WebDriver newDriverInstance(Capabilities capabilities) {
        return new ChromeDriver(capabilities);
    }

    @Override
    protected ChromeDriverService newDriverService() {
        boolean silent = environmentVariables.getPropertyAsBoolean("chrome.silent", true);
        boolean verbose = environmentVariables.getPropertyAsBoolean("chrome.verbose", false);
        ChromeDriverService newService = new ChromeDriverService.Builder()
                                                                .usingDriverExecutable(chromeDriverExecutable())
                                                                .usingAnyFreePort()
                                                                .withSilent(silent)
                                                                .withVerbose(verbose)
                                                                .build();

        DriverPathConfiguration.updateSystemProperty(WEBDRIVER_CHROME_DRIVER.getPropertyName())
                               .withExecutablePath(chromeDriverExecutable());

        Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));

        return newService;
    }

    private File chromeDriverExecutable() {

        File executable = DriverServiceExecutable.called("chromedriver")
                .withSystemProperty(WEBDRIVER_CHROME_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .reportMissingBinary()
                .downloadableFrom("https://sites.google.com/a/chromium.org/chromedriver/downloads")
                .asAFile();

        return executable;
    }
}
