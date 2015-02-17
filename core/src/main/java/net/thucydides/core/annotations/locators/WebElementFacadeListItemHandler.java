package net.thucydides.core.annotations.locators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * Handles a resolved {@link WebElementFacade} from a List.
 * 
 * @author Joe Nasca
 */
public class WebElementFacadeListItemHandler extends AbstractListItemHandler<WebElementFacade> {

	private static final String NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2 = "No suitable constructor found.  "
			+ "Expected one of the following:  %s(WebDriver, WebElement, long) or %s(WebDriver, ElementLocator, WebElement, long)";

	public WebElementFacadeListItemHandler(Class<?> interfaceType, ElementLocator locator,
			WebElement element, PageObject page, long timeoutInMilliseconds) {
		super(WebElementFacade.class, interfaceType, locator, element, page, timeoutInMilliseconds);
	}

	@Override
	protected Object newElementInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		WebDriver driver = page.getDriver();
		if (implementerClass == WebElementFacadeImpl.class) {
			// the target constructor is protected; use the static wrapper method
			return WebElementFacadeImpl.wrapWebElement(driver, element, timeoutInMilliseconds);
		}
		
		// initialize using the element, not the locator
		Constructor<?> constructor = null;
		Object obj = null;
		try {
			constructor = implementerClass.getConstructor(WebDriver.class, WebElement.class, long.class);
			obj = constructor.newInstance(driver, element, timeoutInMilliseconds);
		}
		catch (NoSuchMethodException e) {
			try {
				constructor = implementerClass.getConstructor(WebDriver.class, ElementLocator.class, WebElement.class, long.class);
				obj = constructor.newInstance(driver, (ElementLocator) null, element, timeoutInMilliseconds);
			}
			catch (NoSuchMethodException e1) {
				String className = implementerClass.getSimpleName();
				throw new RuntimeException(String.format(NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2, className, className));
			}
		}
		return obj;
	}
}
