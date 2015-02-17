package net.thucydides.core.webdriver;

import java.util.concurrent.TimeUnit;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.guice.Injectors;

import org.openqa.selenium.WebDriver;

public class DefaultPageObjectInitialiser extends AbstractObjectInitialiser<PageObject> {

	private ElementProxyCreator elementProxyCreator;
	
    public DefaultPageObjectInitialiser(WebDriver driver, long ajaxTimeoutInMilliseconds) {
    	super(driver, ajaxTimeoutInMilliseconds);
    	this.elementProxyCreator = Injectors.getInjector().getInstance(ElementProxyCreator.class);
    }

    public boolean apply(PageObject page) {
    	page.setWaitForTimeout(ajaxTimeout.in(TimeUnit.MILLISECONDS));
        elementProxyCreator.proxyElements(page, driver, ajaxTimeoutInSecondsWithAtLeast1Second());
        return true;
    }
}
