package net.serenitybdd.core.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WebElementFacadeWait {
    PageObject page;
    private final long timeoutInSeconds;
    private final long sleepInMillis;

    public WebElementFacadeWait(PageObject page) {
        this.timeoutInSeconds = page.getImplicitWaitTimeout().getSeconds();
        this.sleepInMillis = 200;
        this.page = page;
    }

    public WebElementFacadeWait(PageObject page, long timeoutInSeconds) {
        this.timeoutInSeconds = timeoutInSeconds;
        this.sleepInMillis = 200;
        this.page = page;
    }

    protected WebElementFacadeWait(PageObject page, long timeoutInSeconds, long sleepInMillis) {
        this.timeoutInSeconds = timeoutInSeconds;
        this.sleepInMillis = sleepInMillis;
        this.page = page;
    }


    public WebElementFacadeWait forUpTo(long timeoutInSeconds) {
        return new WebElementFacadeWait(page, timeoutInSeconds, sleepInMillis);
    }

    public WebElementFacadeWait pollingEvery(long sleepInMillis) {
        return new WebElementFacadeWait(page, timeoutInSeconds, sleepInMillis);
    }

    public WebElementFacade until(ExpectedCondition<? extends WebElement> isTrue) {
        return page.element(new WebDriverWait(page.getDriver(), Duration.ofSeconds(timeoutInSeconds)).until(isTrue));
    }
}
