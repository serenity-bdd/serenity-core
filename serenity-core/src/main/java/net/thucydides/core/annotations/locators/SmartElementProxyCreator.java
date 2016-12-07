package net.thucydides.core.annotations.locators;

import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.ElementLocatorFactorySelector;
import net.thucydides.core.webdriver.ElementProxyCreator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

public class SmartElementProxyCreator implements ElementProxyCreator {

	@Override
	public void proxyElements(PageObject pageObject, WebDriver driver) {
		ElementLocatorFactory finder = getElementLocatorFactorySelector().getLocatorFor(driver);
        FieldDecorator decorator = new SmartFieldDecorator(finder, driver, pageObject);
        PageFactory.initElements(decorator, pageObject);

	}

	@Override
	public void proxyElements(PageObject pageObject, WebDriver driver, int timeoutInSeconds) {
		ElementLocatorFactory finder = getElementLocatorFactorySelector().withTimeout(timeoutInSeconds).getLocatorFor(driver);
        FieldDecorator decorator = new SmartFieldDecorator(finder, driver, pageObject);
        PageFactory.initElements(decorator, pageObject);

	}
	
	private ElementLocatorFactorySelector getElementLocatorFactorySelector() {
	    Configuration configuration = ConfiguredEnvironment.getConfiguration();
	    return new ElementLocatorFactorySelector(configuration);
	}

}
