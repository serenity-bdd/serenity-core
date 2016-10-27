package net.serenitybdd.core.webdriver.servicepools;

import org.openqa.selenium.phantomjs.PhantomJSDriverService;

public class PhantomJSServicePool extends DriverServicePool<PhantomJSDriverService> {
    @Override
    protected PhantomJSDriverService newDriverService() {
        return new PhantomJSDriverService.Builder()
                .usingAnyFreePort()
                .build();
    }

    protected String serviceName(){ return "phantomjs"; }
}
