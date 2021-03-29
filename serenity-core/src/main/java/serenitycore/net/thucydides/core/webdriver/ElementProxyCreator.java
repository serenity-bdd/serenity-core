package serenitycore.net.thucydides.core.webdriver;

import serenitycore.net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;

public interface ElementProxyCreator {

	void proxyElements(PageObject pageObject, WebDriver driver);

	void proxyElements(PageObject pageObject, WebDriver driver, int timeoutInSeconds);

}
