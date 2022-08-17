package net.serenitybdd.core.webdriver.driverproviders;

import net.serenitybdd.core.webdriver.servicepools.StopServiceHook;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

public class DriverServices {

    private static ThreadLocal<Map<ChromeOptions, ChromeDriverService>> chromeDriverServices = ThreadLocal.withInitial(HashMap::new);

    public static ChromeDriverService getChromeDriverService(ChromeOptions options) {
        ChromeDriverService chromeDriverService = chromeDriverServices.get().get(options);
        if (chromeDriverService != null && chromeDriverService.isRunning()) {
            return chromeDriverService;
        } else {
            chromeDriverService = ChromeDriverService.createServiceWithConfig(options);
            Runtime.getRuntime().addShutdownHook(new StopServiceHook(chromeDriverService));
            chromeDriverServices.get().put(options, chromeDriverService);
            return chromeDriverService;
        }
    }
}
