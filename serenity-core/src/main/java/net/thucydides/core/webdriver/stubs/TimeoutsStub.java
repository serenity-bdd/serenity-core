package net.thucydides.core.webdriver.stubs;

import org.openqa.selenium.WebDriver;

import java.time.Duration;

public class TimeoutsStub implements WebDriver.Timeouts {
    @Override
    public WebDriver.Timeouts implicitlyWait(Duration duration) {
        return this;
    }

    @Override
    public WebDriver.Timeouts scriptTimeout(Duration duration) {
        return this;
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(Duration duration) {
        return this;
    }
}
