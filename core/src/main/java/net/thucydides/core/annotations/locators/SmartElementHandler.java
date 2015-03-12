package net.thucydides.core.annotations.locators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class SmartElementHandler extends AbstractSingleItemHandler<WebElementFacade> {

	private static final String NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2 = "No suitable constructor found.  "
			+ "Expected:  %s(WebDriver, ElementLocator, long) or %s(WebDriver, ElementLocator, WebElement, long)";

	public SmartElementHandler(Class<?> interfaceType, ElementLocator locator, PageObject page,
			long timeoutInMilliseconds) {
		super(WebElementFacade.class, interfaceType, locator, page, timeoutInMilliseconds);
	}

	@Override
	protected Object newElementInstance(long timeoutInMilliseconds) throws InvocationTargetException,
			NoSuchMethodException, InstantiationException, IllegalAccessException {
		Constructor<?> constructor;
		Object instance;
		try {
			constructor = implementerClass.getConstructor(WebDriver.class, ElementLocator.class, long.class);
			instance = constructor.newInstance(page.getDriver(), locator, timeoutInMilliseconds);
		}
		catch (NoSuchMethodException e) {
			try {
				constructor = implementerClass.getConstructor(WebDriver.class, ElementLocator.class, WebElement.class, long.class);
				instance = constructor.newInstance(page.getDriver(), locator, null, timeoutInMilliseconds);
			}
			catch (NoSuchMethodException e1) {
				String className = implementerClass.getSimpleName();
				throw new RuntimeException(String.format(NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2, className));
			}
		}
		return instance;
	}
}
