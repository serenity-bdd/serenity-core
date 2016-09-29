package net.serenitybdd.core.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ChromeService extends ManagedDriverService{
    public ChromeService() {
        super(new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .build());
    }
    public WebDriver newDriver() {
        return newDriver(DesiredCapabilities.chrome());
    }

}
