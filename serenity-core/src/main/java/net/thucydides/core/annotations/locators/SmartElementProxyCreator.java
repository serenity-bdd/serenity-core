package net.thucydides.core.annotations.locators;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import net.serenitybdd.core.environment.ConfiguredEnvironment;
import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.webdriver.Configuration;
import net.thucydides.core.webdriver.ElementLocatorFactorySelector;
import net.thucydides.core.webdriver.ElementProxyCreator;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.util.concurrent.TimeUnit;

public class SmartElementProxyCreator implements ElementProxyCreator {

	@Override
	public void proxyElements(PageObject pageObject, WebDriver driver) {
		if(driver instanceof WebDriverFacade) {
			WebDriver proxiedDriver = ((WebDriverFacade) driver).getProxiedDriver();
			if(proxiedDriver instanceof AppiumDriver) {
				PageFactory.initElements(new AppiumFieldDecorator(driver), this);
			} else {
				ElementLocatorFactory finder = getElementLocatorFactorySelector().getLocatorFor(driver);
				PageFactory.initElements(new SmartFieldDecorator(finder, driver, pageObject), pageObject);
			}
		} else {
			ElementLocatorFactory finder = getElementLocatorFactorySelector().getLocatorFor(driver);
			PageFactory.initElements(new SmartFieldDecorator(finder, driver, pageObject), pageObject);
		}
	}


	@Override
	public void proxyElements(PageObject pageObject, WebDriver driver, int timeoutInSeconds) {
		if(driver instanceof WebDriverFacade) {
			WebDriver proxiedDriver = ((WebDriverFacade) driver).getProxiedDriver();
			if(proxiedDriver instanceof AppiumDriver) {
				PageFactory.initElements(new AppiumFieldDecorator(driver, timeoutInSeconds, TimeUnit.SECONDS), this);
			} else {
				ElementLocatorFactory finder = getElementLocatorFactorySelector().withTimeout(timeoutInSeconds).getLocatorFor(driver);
				PageFactory.initElements(new SmartFieldDecorator(finder, driver, pageObject), pageObject);
			}
		} else {
			ElementLocatorFactory finder = getElementLocatorFactorySelector().withTimeout(timeoutInSeconds).getLocatorFor(driver);
			PageFactory.initElements(new SmartFieldDecorator(finder, driver, pageObject), pageObject);
		}
	}

	private ElementLocatorFactorySelector getElementLocatorFactorySelector() {
	    Configuration configuration = ConfiguredEnvironment.getConfiguration();
	    return new ElementLocatorFactorySelector(configuration);
	}

}
