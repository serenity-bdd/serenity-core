package net.thucydides.core.webdriver;

import com.google.common.base.Predicate;
import net.thucydides.core.annotations.locators.SmartElementProxyCreator;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Duration;

import java.util.concurrent.TimeUnit;


/**
 * Initializes WebElement and WebElementFacade fields in a page object.
 */
public class DefaultPageObjectInitialiser implements Predicate<PageObject> {

    private final WebDriver driver;
    private final Duration ajaxTimeout;
    private final ElementProxyCreator elementProxyCreator;

    public DefaultPageObjectInitialiser(WebDriver driver, long ajaxTimeoutInMilliseconds) {
        this.driver = driver;
        ajaxTimeout = new Duration(ajaxTimeoutInMilliseconds, TimeUnit.MILLISECONDS);
        this.elementProxyCreator = new SmartElementProxyCreator();
    }

    protected int ajaxTimeoutInSecondsWithAtLeast1Second() {
        return (int) ((ajaxTimeout.in(TimeUnit.SECONDS) > 0) ? ajaxTimeout.in(TimeUnit.SECONDS) : 1);
    }

    public boolean apply(net.serenitybdd.core.pages.PageObject page) {
        page.setWaitForTimeout(ajaxTimeout.in(TimeUnit.MILLISECONDS));
        elementProxyCreator.proxyElements(page, driver, ajaxTimeoutInSecondsWithAtLeast1Second());
        return true;
    }
}
