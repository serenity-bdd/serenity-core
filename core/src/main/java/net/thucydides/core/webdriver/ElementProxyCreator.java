package net.thucydides.core.webdriver;

import net.thucydides.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

public interface ElementProxyCreator {

	void proxyElements(PageObject pageObject, WebDriver driver);

	void proxyElements(PageObject pageObject, WebDriver driver, int timeoutInSeconds);

}
