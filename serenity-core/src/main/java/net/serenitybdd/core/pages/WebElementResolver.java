package net.serenitybdd.core.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.util.List;

public abstract class WebElementResolver {

    public abstract WebElement resolveForDriver(WebDriver driver);

    public abstract List<WebElement> resolveAllForDriver(WebDriver driver);

    public static WebElementResolver by(By bySelector) {
        return new WebElementResolverByLocator(bySelector);
    }

    public static WebElementResolver forWebElement(WebElement webElement) {
        return new ResolvedWebElementResolver(webElement);
    }

    public static LocatorResolverBuilder byLocator(ElementLocator locator) {
        return new LocatorResolverBuilder(locator);
    }

    public static class LocatorResolverBuilder {
        private final ElementLocator locator;

        public LocatorResolverBuilder(ElementLocator locator) {
            this.locator = locator;
        }

        public WebElementResolver withImplicitTimeout(long implicitTimeoutInMilliseconds) {
            return new WebElementResolverByElementLocator(locator, implicitTimeoutInMilliseconds);
        }
    }
}