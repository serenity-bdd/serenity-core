package net.serenitybdd.core.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class WebElementResolverByLocator extends WebElementResolver {
    private final By bySelector;
    private final long implicitTimeoutInMilliseconds;

    public WebElementResolverByLocator(By bySelector, long implicitTimeoutInMilliseconds) {
        this.bySelector = bySelector;
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
    }

    public WebElementResolverByLocator(By bySelector) {
        this(bySelector, 0L);
    }

    @Override
    public WebElement resolveForDriver(WebDriver driver) {

        return driver.findElement(bySelector);
    }

    @Override
    public List<WebElement> resolveAllForDriver(WebDriver driver) {
        return driver.findElements(bySelector);
    }
}
