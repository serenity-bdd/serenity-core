package net.thucydides.core.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/** @deprecated Use same-named class in serenitybdd package
 *
 */
@Deprecated
public class WebElementFacadeImpl extends net.serenitybdd.core.pages.WebElementFacadeImpl implements WebElementFacade {
    public WebElementFacadeImpl(WebDriver driver, WebElement webElement, long timeoutInMilliseconds) {
        super(driver, webElement, timeoutInMilliseconds);
    }

    public WebElementFacadeImpl(WebDriver driver, ElementLocator locator, long timeoutInMilliseconds) {
        super(driver, locator, timeoutInMilliseconds);
    }
}
