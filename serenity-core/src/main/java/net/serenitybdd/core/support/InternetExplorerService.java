package net.serenitybdd.core.support;

import net.thucydides.core.util.EnvironmentVariables;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_IE_DRIVER;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class InternetExplorerService extends ManagedDriverService{
    public InternetExplorerService(EnvironmentVariables environmentVariables) {
        super(
                isNotEmpty(environmentVariables.getProperty(WEBDRIVER_IE_DRIVER)) ?
                        new InternetExplorerDriverService.Builder().usingAnyFreePort()
                                .usingDriverExecutable(new File(environmentVariables.getProperty(WEBDRIVER_IE_DRIVER)))
                                .build()
                        : new InternetExplorerDriverService.Builder()
                                .usingAnyFreePort()
                                .build()
        );
    }

    @Override
    public WebDriver newDriver(Capabilities capabilities) {
        return new InternetExplorerDriver((InternetExplorerDriverService) driverService, capabilities);
    }
}
