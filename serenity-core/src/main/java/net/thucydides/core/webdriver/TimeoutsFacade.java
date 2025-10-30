package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

import java.time.Duration;

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
    public WebDriver.Timeouts implicitlyWait(Duration timeout) {
        webDriverFacade.implicitTimeout = timeout;
        return (timeouts != null) ? timeouts.implicitlyWait(timeout) : timeouts;
    }

    @Override
    public Duration getImplicitWaitTimeout() {
        return WebDriver.Timeouts.super.getImplicitWaitTimeout();
    }

    @Override
    public WebDriver.Timeouts scriptTimeout(Duration duration) {
        webDriverFacade.implicitTimeout = duration;
        return (timeouts != null) ? timeouts.scriptTimeout(duration) : timeouts;
    }

    @Override
    public Duration getScriptTimeout() {
        return WebDriver.Timeouts.super.getScriptTimeout();
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(Duration timeout) {
        return (timeouts != null) ? timeouts.pageLoadTimeout(timeout) : timeouts;
    }

    @Override
    public Duration getPageLoadTimeout() {
        return WebDriver.Timeouts.super.getPageLoadTimeout();
    }
}
