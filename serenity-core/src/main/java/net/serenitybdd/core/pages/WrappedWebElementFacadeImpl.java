package net.serenitybdd.core.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class WrappedWebElementFacadeImpl extends WebElementFacadeImpl implements WrapsElement {
    public WrappedWebElementFacadeImpl(WebDriver driver, ElementLocator locator, WebElement webElement, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
        super(driver, locator, webElement, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }

    public WrappedWebElementFacadeImpl(WebDriver driver, ElementLocator locator, WebElement webElement, WebElement resolvedELement, By bySelector, long timeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
        super(driver, locator, webElement, resolvedELement, bySelector, timeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }

    @Override
    public WebElement getWrappedElement() {
        if (getElement() instanceof WrapsElement) {
            return ((WrapsElement) getElement()).getWrappedElement();
        }
        return null;
    }

}
