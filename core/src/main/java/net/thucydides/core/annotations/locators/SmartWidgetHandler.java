package net.thucydides.core.annotations.locators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WidgetObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * Handles single-item {@link WidgetObject} proxies.
 * 
 * @author Joe Nasca
 */
public class SmartWidgetHandler extends AbstractSingleItemHandler<WidgetObject> {
	
	private static final String NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2 = "No suitable constructor found.  "
			+ "Expected:  %s(PageObject, ElementLocator, long) or %s(PageObject, ElementLocator, WebElement, long)";

	public SmartWidgetHandler(Class<?> interfaceType, ElementLocator locator, PageObject page,
			long timeoutInMilliseconds) {
		super(WidgetObject.class, interfaceType, locator, page, timeoutInMilliseconds);
	}

	@Override
	protected Object newElementInstance(long timeoutInMilliseconds) throws InvocationTargetException,
			NoSuchMethodException, InstantiationException, IllegalAccessException {
		Constructor<?> constructor = null;
		Object instance = null;
		try {
			constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, long.class);
			instance = constructor.newInstance(page, locator, timeoutInMilliseconds);
		}
		catch (NoSuchMethodException e) {
			try {
				constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, WebElement.class, long.class);
				instance = constructor.newInstance(page, locator, (WebElement) null, timeoutInMilliseconds);
			}
			catch (NoSuchMethodException e1) {
				String className = implementerClass.getSimpleName();
				throw new RuntimeException(String.format(NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2, className, className));
			}
		}
		return instance;
	}
}
