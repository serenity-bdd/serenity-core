package net.serenitybdd.core.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

class BuildWebElementFacade {
    public static WebElementFacade from(final WebDriver driver,
                                                      final WebElement element,
                                                      final long timeoutInMilliseconds,
                                                      final long waitForTimeoutInMilliseconds) {
        return (element instanceof WrapsElement)
                ?  new WrappedWebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy("<Undefined web element>")
                : new WebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy("<Undefined web element>");
    }

    public static WebElementFacade from(final WebDriver driver,
                                                      final WebElement element,
                                                      final long timeoutInMilliseconds,
                                                      final long waitForTimeoutInMilliseconds,
                                                      final String foundBy) {
        return (element instanceof WrapsElement)
                ? new WrappedWebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy)
                : new WebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy);
    }

    public static WebElementFacade from(WebDriver driver,
                                                      WebElement resolvedELement,
                                                      WebElement element,
                                                      By bySelector,
                                                      ElementLocator locator,
                                                      long timeoutInMilliseconds,
                                                      long waitForTimeoutInMilliseconds,
                                                      String foundBy) {
        return (element instanceof WrapsElement)
                ? new WrappedWebElementFacadeImpl(driver, locator, element, resolvedELement, bySelector, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy)
                : new WebElementFacadeImpl(driver, locator, element, resolvedELement, bySelector, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy);
    }

    public static  WebElementFacade from(final WebDriver driver,
                                                      final By bySelector,
                                                      final long timeoutInMilliseconds,
                                                      final long waitForTimeoutInMilliseconds,
                                                      final String foundBy) {

        return new WebElementFacadeImpl(driver, null, null, timeoutInMilliseconds, waitForTimeoutInMilliseconds, bySelector).foundBy(foundBy);
    }

    public static WebElementFacade from(final WebDriver driver,
                                                      final WebElement element,
                                                      final long timeout) {
        return (element instanceof WrapsElement)
                ? new WrappedWebElementFacadeImpl(driver, null, element, timeout, timeout).foundBy(element.toString())
                : new WebElementFacadeImpl(driver, null, element, timeout, timeout).foundBy(element.toString());
    }

    public static WebElementFacadeImpl from(WebDriver driver, ElementLocator locator, WebElement element, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
        return (element instanceof WrapsElement)
                ? new WrappedWebElementFacadeImpl(driver, locator, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds)
                : new WebElementFacadeImpl(driver, locator, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }
}