package net.thucydides.core.webdriver;

import com.google.common.base.Predicate;
import net.thucydides.core.annotations.locators.SmartElementProxyCreator;
import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

/**
 * Initializes WebElement and WebElementFacade fields in a page object.
 */
public class DefaultPageObjectInitialiser implements Predicate<PageObject> {

    private final WebDriver driver;
    private final int ajaxTimeoutInMilliseconds;
    private final ElementProxyCreator elementProxyCreator;

    public DefaultPageObjectInitialiser(WebDriver driver, int ajaxTimeoutInMilliseconds) {
        this.driver = driver;
        this.ajaxTimeoutInMilliseconds = ajaxTimeoutInMilliseconds;
        this.elementProxyCreator = new SmartElementProxyCreator();
    }

    protected int ajaxTimeoutInSecondsWithAtLeast1Second() {
        if (ajaxTimeoutInMilliseconds > 1000) {
            return ajaxTimeoutInMilliseconds / 1000;
        } else {
            return 1;
        }
    }

    public boolean apply(PageObject page) {
        page.setWaitForTimeout(ajaxTimeoutInMilliseconds);
        elementProxyCreator.proxyElements(page, driver, ajaxTimeoutInSecondsWithAtLeast1Second());
        return true;
    }
}
