package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.servicepools.StopServiceHook;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public class DriverServices {

    private static ChromeDriverService chromeDriverService;

    public static synchronized ChromeDriverService getChromeDriverService(ChromeOptions options) {
        if (chromeDriverService != null && chromeDriverService.isRunning()) {
            return chromeDriverService;
        } else {
            chromeDriverService = ChromeDriverService.createServiceWithConfig(options);
            Runtime.getRuntime().addShutdownHook(new StopServiceHook(chromeDriverService));
            return chromeDriverService;
        }
    }
}
