package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;

import java.io.File;

import static net.thucydides.core.ThucydidesSystemProperty.WEBDRIVER_EDGE_DRIVER;

public class EdgeServicePool extends DriverServicePool<EdgeDriverService> {

    @Override
    protected String serviceName(){ return "edge"; }

    @Override
    protected WebDriver newDriverInstance(Capabilities capabilities) {
        return new EdgeDriver(capabilities);
    }

    @Override
    protected EdgeDriverService newDriverService() {

        EdgeDriverService newService =  new EdgeDriverService.Builder()
                        .usingDriverExecutable(edgeDriverExecutable())
                        .usingAnyFreePort()
                        .build();

        DriverPathConfiguration.updateSystemProperty(WEBDRIVER_EDGE_DRIVER.getPropertyName())
                               .withExecutablePath(edgeDriverExecutable());

        Runtime.getRuntime().addShutdownHook(new StopServiceHook(newService));

        return newService;
    }

    private File edgeDriverExecutable() {
        return DriverServiceExecutable.called("MicrosoftWebDriver.exe")
                .withSystemProperty(WEBDRIVER_EDGE_DRIVER.getPropertyName())
                .usingEnvironmentVariables(environmentVariables)
                .reportMissingBinary()
                .downloadableFrom("https://www.microsoft.com/en-us/download/details.aspx?id=48212")
                .asAFile();
    }
}
