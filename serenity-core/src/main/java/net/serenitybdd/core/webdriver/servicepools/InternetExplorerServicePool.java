package net.serenitybdd.core.webdriver.servicepools;

import io.github.bonigarcia.wdm.InternetExplorerDriverManager;
import net.thucydides.core.ThucydidesSystemProperty;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_IE_DRIVER;

public class InternetExplorerServicePool extends DriverServicePool<InternetExplorerDriverService> {

    @Override
    protected String serviceName(){ return "iexplorer"; }

    @Override
    protected WebDriver newDriverInstance(Capabilities capabilities) {
        return new InternetExplorerDriver(capabilities);
    }

    @Override
    protected InternetExplorerDriverService newDriverService() {

        InternetExplorerDriverService newService = new InternetExplorerDriverService.Builder()
                        .usingDriverExecutable(driverExecutable())
                        .usingAnyFreePort()
                        .build();

        DriverPathConfiguration.updateSystemProperty(WEBDRIVER_IE_DRIVER.getPropertyName())
                               .withExecutablePath(driverExecutable());

        Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));

        return newService;
    }

    private File driverExecutable() {
        if (ThucydidesSystemProperty.AUTOMATIC_DRIVER_DOWNLOAD.booleanFrom(environmentVariables, true)) {
            InternetExplorerDriverManager.getInstance().setup();
        }

        return DriverServiceExecutable.called("IEDriverServer.exe")
                .withSystemProperty(WEBDRIVER_IE_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .andDownloadableFrom("https://github.com/SeleniumHQ/selenium/wiki/InternetExplorerDriver")
                .asAFile();
    }
}
