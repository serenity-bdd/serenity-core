package net.thucydides.core.annotations.locators;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Handles a resolved {@link WebElementFacade} from a List.
 * 
 * @author Joe Nasca
 */
public class WebElementFacadeListItemHandler extends AbstractListItemHandler<WebElementFacade> {

	private static final String NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2 = "No suitable constructor found.  "
			+ "Expected one of the following:  %s(WebDriver, WebElement, long) or %s(WebDriver, ElementLocator, WebElement, long)";

	public WebElementFacadeListItemHandler(Class<?> interfaceType,
										   ElementLocator locator,
										   WebElement element,
										   PageObject page,
										   long implicitTimeoutInMilliseconds,
										   long waitForTimeoutInMilliseconds) {
		super(WebElementFacade.class, interfaceType, locator, element, page, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
	}

	@Override
	protected Object newElementInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		WebDriver driver = page.getDriver();
		if (implementerClass == WebElementFacadeImpl.class) {
			// the target constructor is protected; use the static wrapper method
			return WebElementFacadeImpl.wrapWebElement(driver, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds, locator.toString());
		}

/*
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
 */
		Object instance = null;

		if (ElementContructorForm.applicableConstructor(implementerClass).isPresent()) {
			Constructor constructor = ElementContructorForm.applicableConstructorFrom(implementerClass).get();
			switch (ElementContructorForm.applicableConstructor((implementerClass)).get()) {
				case WEBDRIVER_LOCATOR_SINGLE_TIMEOUT:
					instance = constructor.newInstance(page.getDriver(), locator, implicitTimeoutInMilliseconds);
					break;
				case WEBDRIVER_LOCATOR_TWO_TIMEOUTS:
					instance = constructor.newInstance(page.getDriver(), locator, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
					break;
				case WEBDRIVER_ELEMENT_SINGLE_TIMEOUT:
					instance = constructor.newInstance(page.getDriver(), null, element, implicitTimeoutInMilliseconds);
					break;
				case WEBDRIVER_ELEMENT_TWO_TIMEOUTS:
					instance = constructor.newInstance(page.getDriver(), null, element, implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
					break;
			}
		}

		if (instance == null) {
			String className = implementerClass.getSimpleName();
			throw new RuntimeException(String.format(NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2, className, className));
		}
		return instance;
	}
}
