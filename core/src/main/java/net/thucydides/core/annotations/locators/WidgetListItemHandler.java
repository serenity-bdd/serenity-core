package net.thucydides.core.annotations.locators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WidgetObject;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * Handles a resolved {@link WidgetObject} from a List.
 * 
 * @author Joe Nasca
 */
public class WidgetListItemHandler extends AbstractListItemHandler<WidgetObject> {

	private static final String NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2 = "No suitable constructor found.  "
			+ "Expected one of the following:  %s(PageObject, WebElement, long) or %s(PageObject, ElementLocator, WebElement, long)";

	public WidgetListItemHandler(Class<?> interfaceType, ElementLocator locator,
			WebElement element, PageObject page, long timeoutInMilliseconds) {
		super(WidgetObject.class, interfaceType, locator, element, page, timeoutInMilliseconds);
	}

	protected Object newElementInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		// initialize using the element, not the locator
		Constructor<?> constructor = null;
		Object obj = null;
		try {
			constructor = implementerClass.getConstructor(PageObject.class, WebElement.class, long.class);
			obj = constructor.newInstance(page, element, timeoutInMilliseconds);
		}
		catch (NoSuchMethodException e) {
			try {
				constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, WebElement.class, long.class);
				obj = constructor.newInstance(page, (ElementLocator) null, element, timeoutInMilliseconds);
			}
			catch (NoSuchMethodException e1) {
				String className = implementerClass.getSimpleName();
				throw new RuntimeException(String.format(NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2, className, className));
			}
		}
		return obj;
	}
}
