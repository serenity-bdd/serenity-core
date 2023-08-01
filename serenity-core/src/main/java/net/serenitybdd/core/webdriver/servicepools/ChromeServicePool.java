package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriverLogLevel;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_CHROME_DRIVER;

public class ChromeServicePool extends DriverServicePool<ChromeDriverService> {

    @Override
    protected String serviceName(){ return "chrome"; }

    @Override
    protected WebDriver newDriverInstance(Capabilities capabilities) {
        ChromeOptions options = new ChromeOptions();
        options.merge(capabilities);
        return new ChromeDriver(options);
    }

    @Override
    protected ChromeDriverService newDriverService() {

        String driver = environmentVariables.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, chromeDriverExecutable().getAbsolutePath());
        String logFile = environmentVariables.getProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY);
        String logLevel = environmentVariables.getProperty("webdriver.chrome.logLevel");
        boolean appendLog = environmentVariables.getPropertyAsBoolean(ChromeDriverService.CHROME_DRIVER_APPEND_LOG_PROPERTY, false);
        boolean disableBuildCheck = environmentVariables.getPropertyAsBoolean(ChromeDriverService.CHROME_DRIVER_DISABLE_BUILD_CHECK, false);
        boolean verbose = environmentVariables.getPropertyAsBoolean(ChromeDriverService.CHROME_DRIVER_VERBOSE_LOG_PROPERTY, false);
        boolean silent = environmentVariables.getPropertyAsBoolean(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, false);
        String whitelist = environmentVariables.getProperty(ChromeDriverService.CHROME_DRIVER_ALLOWED_IPS_PROPERTY,"");

        ChromeDriverService.Builder builder = new ChromeDriverService.Builder()
                .usingDriverExecutable(chromeDriverExecutable())
                .usingAnyFreePort()
                .withSilent(silent)
                .withAppendLog(appendLog)
                .withVerbose(verbose);

        if (!whitelist.isEmpty()) {
            builder.withWhitelistedIps(whitelist);
        }
        if (!logLevel.isEmpty()) {
            builder.withLogLevel(ChromiumDriverLogLevel.fromString(logLevel));
        }
        if (disableBuildCheck) {
            System.setProperty(ChromeDriverService.CHROME_DRIVER_DISABLE_BUILD_CHECK, "true");
        }
        if (!logFile.isEmpty()) {
            System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, logFile);
        }
        if (!driver.isEmpty()) {
            builder.usingDriverExecutable(new File(driver));
        }

        ChromeDriverService newService = builder.build();

//        DriverPathConfiguration.updateSystemProperty(WEBDRIVER_CHROME_DRIVER.getPropertyName())
//                               .withExecutablePath(chromeDriverExecutable());

        Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));

        return newService;
    }

    private File chromeDriverExecutable() {
        return DriverServiceExecutable.called("chromedriver")
                .withSystemProperty(WEBDRIVER_CHROME_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .reportMissingBinary()
                .downloadableFrom("https://sites.google.com/a/chromium.org/chromedriver/downloads")
                .asAFile();
    }
}
