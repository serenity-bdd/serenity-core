package net.thucydides.core.annotations.locators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

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
			WebElement element, PageObject page, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
		super(WidgetObject.class, interfaceType, locator, element, page, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
	}

	protected Object newElementInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		// initialize using the element, not the locator
//		Constructor<?> constructor = null;
//		Object obj = null;
//		try {
//			constructor = implementerClass.getConstructor(PageObject.class, WebElement.class, long.class);
//			obj = constructor.newInstance(page, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
//		}
//		catch (NoSuchMethodException e) {
//			try {
//				constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, WebElement.class, long.class);
//				obj = constructor.newInstance(page, null, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
//			}
//			catch (NoSuchMethodException e1) {
//				String className = implementerClass.getSimpleName();
//				throw new RuntimeException(String.format(NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2, className, className));
//			}
//		}
//		return obj;

		Object instance = null;

		if (ElementContructorForm.applicableConstructor(implementerClass).isPresent()) {
			Constructor constructor = ElementContructorForm.applicableConstructorFrom(implementerClass).get();
			switch (ElementContructorForm.applicableConstructor((implementerClass)).get()) {
				case PAGE_ELEMENT_SINGLE_TIMEOUT:
					instance = constructor.newInstance(page, element, implicitTimeoutInMilliseconds);
					break;
				case PAGE_ELEMENT_TWO_TIMEOUTS:
					instance = constructor.newInstance(page, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
					break;

				case PAGE_LOCATOR_SINGLE_TIMEOUT:
					instance = constructor.newInstance(page, locator, implicitTimeoutInMilliseconds);
					break;
				case PAGE_LOCATOR_TWO_TIMEOUTS:
					instance = constructor.newInstance(page, locator, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
					break;
				case PAGE_LOCATOR_ELEMENT_SINGLE_TIMEOUT:
					instance = constructor.newInstance(page, locator, element, implicitTimeoutInMilliseconds);
					break;
				case PAGE_LOCATOR_ELEMENT_TWO_TIMEOUTS:
					instance = constructor.newInstance(page, locator, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
					break;
			}
		}
		return instance;

	}
}
