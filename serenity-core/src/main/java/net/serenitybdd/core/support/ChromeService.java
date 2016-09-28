package net.serenitybdd.core.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;

public class ChromeService {
    private final ChromeDriverService chromeDriverService;

    public ChromeService() {
        this.chromeDriverService = new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .build();
    }

    public void start() throws IOException {
        chromeDriverService.start();
    }

    public void stop() {
        chromeDriverService.stop();
    }

    public WebDriver newDriver() {
        return new RemoteWebDriver(chromeDriverService.getUrl(), DesiredCapabilities.chrome());
    }
}