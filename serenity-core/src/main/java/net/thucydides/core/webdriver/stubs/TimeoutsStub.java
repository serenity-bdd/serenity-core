package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

public class TimeoutsStub implements WebDriver.Timeouts {
    @Override
    public WebDriver.Timeouts implicitlyWait(long time, TimeUnit unit) {
        return this;
    }

    @Override
    public WebDriver.Timeouts setScriptTimeout(long time, TimeUnit unit) {
        return this;
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(long l, TimeUnit timeUnit) {
        return this;
    }
}
