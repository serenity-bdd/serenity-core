package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

/**
 * Created by john on 30/01/15.
 */
public class TimeoutsFacade implements WebDriver.Timeouts {

    private final WebDriverFacade webDriverFacade;
    private final WebDriver.Timeouts timeouts;

    public TimeoutsFacade(WebDriverFacade webDriverFacade, WebDriver.Timeouts timeouts) {
        this.webDriverFacade = webDriverFacade;
        this.timeouts = timeouts;
    }

    @Override
    public WebDriver.Timeouts implicitlyWait(long value, TimeUnit timeUnit) {
        webDriverFacade.implicitTimeoutValue = value;
        webDriverFacade.implicitTimeoutUnit = timeUnit;
        return timeouts.implicitlyWait(value,timeUnit);
    }

    @Override
    public WebDriver.Timeouts setScriptTimeout(long l, TimeUnit timeUnit) {
        return timeouts.setScriptTimeout(1, timeUnit);
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(long l, TimeUnit timeUnit) {
        return timeouts.pageLoadTimeout(l,timeUnit);
    }
}
