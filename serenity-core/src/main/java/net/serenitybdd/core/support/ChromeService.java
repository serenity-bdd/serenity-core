package net.serenitybdd.core.support;

import org.openqa.selenium.chrome.ChromeDriverService;

public class ChromeService extends ManagedDriverService{
    public ChromeService() {
        super(new ChromeDriverService.Builder()
                .usingAnyFreePort()
                .build());
    }
}
