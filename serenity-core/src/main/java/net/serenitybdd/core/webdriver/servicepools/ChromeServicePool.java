package net.serenitybdd.core.webdriver.servicepools;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import net.thucydides.core.ThucydidesSystemProperty;
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
        ChromeDriverService newService = new ChromeDriverService.Builder()
                                                                .usingDriverExecutable(chromeDriverExecutable())
                                                                .usingAnyFreePort()
                                                                .build();

        DriverPathConfiguration.updateSystemProperty(WEBDRIVER_CHROME_DRIVER.getPropertyName())
                               .withExecutablePath(chromeDriverExecutable());

        Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));

        return newService;
    }

    private File chromeDriverExecutable() {

        if (ThucydidesSystemProperty.AUTOMATIC_DRIVER_DOWNLOAD.booleanFrom(environmentVariables, true)) {
            ChromeDriverManager.getInstance().setup();
        }
        File executable = DriverServiceExecutable.called("chromedriver")
                .withSystemProperty(WEBDRIVER_CHROME_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .andDownloadableFrom("https://sites.google.com/a/chromium.org/chromedriver/downloads")
                .asAFile();

        return executable;
    }
}
