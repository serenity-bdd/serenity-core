package net.serenitybdd.core.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

class BuildWebElementFacade {
    public static <T extends WebElementFacade> T from(final WebDriver driver,
                                                      final WebElement element,
                                                      final long timeoutInMilliseconds,
                                                      final long waitForTimeoutInMilliseconds) {
        return (element instanceof WrapsElement)
                ? (T) new WrappedWebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy("<Undefined web element>")
                : (T) new WebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy("<Undefined web element>");
    }

    public static <T extends WebElementFacade> T from(final WebDriver driver,
                                                      final WebElement element,
                                                      final long timeoutInMilliseconds,
                                                      final long waitForTimeoutInMilliseconds,
                                                      final String foundBy) {
        return (element instanceof WrapsElement)
                ? (T) new WrappedWebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy)
                : (T) new WebElementFacadeImpl(driver, null, element, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy);
    }

    public static <T extends WebElementFacade> T from(WebDriver driver,
                                                      WebElement resolvedELement,
                                                      WebElement element,
                                                      By bySelector,
                                                      ElementLocator locator,
                                                      long timeoutInMilliseconds,
                                                      long waitForTimeoutInMilliseconds,
                                                      String foundBy) {
        return (element instanceof WrapsElement)
                ? (T) new WrappedWebElementFacadeImpl(driver, locator, element, resolvedELement, bySelector, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy)
                : (T) new WebElementFacadeImpl(driver, locator, element, resolvedELement, bySelector, timeoutInMilliseconds, waitForTimeoutInMilliseconds).foundBy(foundBy);
    }

    public static <T extends WebElementFacade> T from(final WebDriver driver,
                                                      final By bySelector,
                                                      final long timeoutInMilliseconds,
                                                      final long waitForTimeoutInMilliseconds,
                                                      final String foundBy) {

        return (T) new WebElementFacadeImpl(driver, null, null, timeoutInMilliseconds, waitForTimeoutInMilliseconds, bySelector).foundBy(foundBy);
    }

    public static <T extends WebElementFacade> T from(final WebDriver driver,
                                                      final WebElement element,
                                                      final long timeout) {
        return (element instanceof WrapsElement)
                ? (T) new WrappedWebElementFacadeImpl(driver, null, element, timeout, timeout).foundBy(element.toString())
                : (T) new WebElementFacadeImpl(driver, null, element, timeout, timeout).foundBy(element.toString());
    }

    public static <T extends WebElementFacade> T from(WebDriver driver, ElementLocator locator, WebElement element, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
        return (element instanceof WrapsElement)
                ? (T) new WrappedWebElementFacadeImpl(driver, locator, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds)
                : (T) new WebElementFacadeImpl(driver, locator, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
    }
}