package net.thucydides.core.webdriver;

import net.serenitybdd.core.di.SerenityInfrastructure;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

public class DefaultPageObjectInitialiser extends AbstractObjectInitialiser<PageObject> {

	private ElementProxyCreator elementProxyCreator;
	
    public DefaultPageObjectInitialiser(WebDriver driver, long ajaxTimeoutInMilliseconds) {
    	super(driver, ajaxTimeoutInMilliseconds);
    	this.elementProxyCreator = SerenityInfrastructure.getElementProxyCreator();
    }

    public boolean apply(PageObject page) {
        if (driver != null) {
            page.setWaitForElementTimeout(ajaxTimeout.toMillis());
            elementProxyCreator.proxyElements(page, driver, ajaxTimeoutInSecondsWithAtLeast1Second());
        }
        return true;
    }
}
