package net.serenitybdd.core.support;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;

import java.io.IOException;

public abstract class ManagedDriverService<T extends DriverService> {
    protected final DriverService driverService;

    public ManagedDriverService(DriverService driverService) {
        this.driverService = driverService;
    }

    public void start() throws IOException {
        driverService.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (driverService.isRunning()) {
                    driverService.stop();
                }
            }
        });
    }

    public void stop() {
        if (driverService.isRunning()) {
            driverService.stop();
        }
    }

    public WebDriver newDriver(Capabilities capabilities) {
        return new RemoteWebDriver(driverService.getUrl(), capabilities);
    }
}