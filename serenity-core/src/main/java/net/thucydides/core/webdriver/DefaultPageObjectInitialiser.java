package net.thucydides.core.webdriver;

import net.serenitybdd.core.di.*;
import net.serenitybdd.core.pages.*;
import org.openqa.selenium.*;

import java.util.concurrent.*;

public class DefaultPageObjectInitialiser extends AbstractObjectInitialiser<PageObject> {

	private ElementProxyCreator elementProxyCreator;
	
    public DefaultPageObjectInitialiser(WebDriver driver, long ajaxTimeoutInMilliseconds) {
    	super(driver, ajaxTimeoutInMilliseconds);
    	this.elementProxyCreator = WebDriverInjectors.getInjector().getInstance(ElementProxyCreator.class);
    }

    public boolean apply(PageObject page) {
        if (driver != null) {
            page.setWaitForElementTimeout(ajaxTimeout.in(TimeUnit.MILLISECONDS));
            elementProxyCreator.proxyElements(page, driver, ajaxTimeoutInSecondsWithAtLeast1Second());
        }
        return true;
    }
}
