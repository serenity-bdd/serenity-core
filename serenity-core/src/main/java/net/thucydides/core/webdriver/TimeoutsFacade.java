package net.thucydides.core.webdriver;

import org.openqa.selenium.WebDriver;

import java.time.Duration;
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
    public WebDriver.Timeouts implicitlyWait(long timeoutValue, TimeUnit timeUnit) {
        webDriverFacade.implicitTimeout = Duration.of(timeoutValue, TemporalUnitConverter.fromTimeUnit(timeUnit));
        return (timeouts != null) ? timeouts.implicitlyWait(timeoutValue,timeUnit) : timeouts;
    }

    @Override
    public WebDriver.Timeouts setScriptTimeout(long timeoutValue, TimeUnit timeUnit) {
        return (timeouts != null) ? timeouts.setScriptTimeout(timeoutValue, timeUnit) : timeouts;
    }

    @Override
    public WebDriver.Timeouts pageLoadTimeout(long timeoutValue, TimeUnit timeUnit) {
        return (timeouts != null) ? timeouts.pageLoadTimeout(timeoutValue,timeUnit) : timeouts;
    }
}
