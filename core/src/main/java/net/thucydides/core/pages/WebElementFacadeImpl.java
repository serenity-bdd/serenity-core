package net.thucydides.core.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/** @deprecated Use same-named class in serenitybdd package
 *
 */
@Deprecated
public class WebElementFacadeImpl extends net.serenitybdd.core.pages.WebElementFacadeImpl implements WebElementFacade {
    /**
     * @param driver
     * @param webElement
     * @param timeoutInMilliseconds
     * @deprecated As of release 0.9.127, replaced by static {@link #wrapWebElement(WebDriver driver, WebElement webElement, long timeoutInMilliseconds)}
     */
    @Deprecated
    public WebElementFacadeImpl(WebDriver driver, WebElement webElement, long timeoutInMilliseconds) {
        this(driver, (ElementLocator) null, webElement, timeoutInMilliseconds);
    }

    public WebElementFacadeImpl(WebDriver driver, ElementLocator locator, long timeoutInMilliseconds) {
        super(driver, locator, timeoutInMilliseconds);
    }

    /** DO NOT USE THIS CONSTRUCTOR
     * This needs to be public while the rename takes place so that serenitybdd can construct a WebElementFacadeImpl in the thucydides namespace
     *
     * When the thucydides namespace is removed, the serenitybdd will be modified accordingly.
     * @param driver
     * @param locator
     * @param webElement
     * @param timeoutInMilliseconds
     */
    public WebElementFacadeImpl(WebDriver driver, ElementLocator locator, WebElement webElement, long timeoutInMilliseconds) {
        super(driver, locator, webElement, timeoutInMilliseconds);
    }
}
