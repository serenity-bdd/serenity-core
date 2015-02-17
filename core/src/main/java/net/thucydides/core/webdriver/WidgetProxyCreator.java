package net.thucydides.core.webdriver;

import net.serenitybdd.core.pages.WidgetObject;

import org.openqa.selenium.WebDriver;

public interface WidgetProxyCreator {

	void proxyElements(WidgetObject pageObject, WebDriver driver);
	
	void proxyElements(WidgetObject pageObject, WebDriver driver, int timeoutInSeconds);
	
}
