package net.thucydides.core.annotations.locators;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;

public final class SmartElementLocatorFactory implements ElementLocatorFactory {
	  private final WebDriver webDriver;
	  private int timeoutInSeconds;

	  public SmartElementLocatorFactory(WebDriver webDriver, int timeoutInSeconds) {
	    this.webDriver = webDriver;
	    this.timeoutInSeconds = timeoutInSeconds;
	  }

	  public ElementLocator createLocator(Field field) {
	    return new SmartAjaxElementLocator(webDriver, field, timeoutInSeconds);
	  }
}
