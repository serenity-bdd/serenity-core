package net.thucydides.core.annotations.locators;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WidgetObject;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Handles single-item {@link WidgetObject} proxies.
 *
 * @author Joe Nasca
 */
public class SmartWidgetHandler extends AbstractSingleItemHandler<WidgetObject> {

    private static final String NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2 = "No suitable constructor found.  "
            + "Expected:  %s(PageObject, ElementLocator, long) or %s(PageObject, ElementLocator, WebElement, long)";

    public SmartWidgetHandler(Class<?> interfaceType, ElementLocator locator, PageObject page) {
        super(WidgetObject.class, interfaceType, locator, page);
    }

    @Override
    protected Object newElementInstance() throws InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
//		Constructor<?> constructor = null;
//		Object instance = null;
//		try {
//			constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, long.class);
//			instance = constructor.newInstance(page, locator, page.getImplicitWaitTimeout(), page.getWaitForTimeout());
//		}
//		catch (NoSuchMethodException e) {
//			try {
//				constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, WebElement.class, long.class);
//				instance = constructor.newInstance(page, locator, null,  page.getImplicitWaitTimeout(), page.getWaitForTimeout());
//			}
//			catch (NoSuchMethodException e1) {
//				String className = implementerClass.getSimpleName();
//				throw new RuntimeException(String.format(NO_SUITABLE_CONSTRUCTOR_FOUND_FMT2, className, className));
//			}
//		}
//		return instance;


        Object instance = null;

        if (ElementContructorForm.applicableConstructor(implementerClass).isPresent()) {
            Constructor constructor = ElementContructorForm.applicableConstructorFrom(implementerClass).get();
            switch (ElementContructorForm.applicableConstructor((implementerClass)).get()) {
                case PAGE_LOCATOR_SINGLE_TIMEOUT:
                    instance = constructor.newInstance(page, locator, page.getImplicitWaitTimeout().toMillis());
                    break;
                case PAGE_LOCATOR_TWO_TIMEOUTS:
                    instance = constructor.newInstance(page, locator, page.getImplicitWaitTimeout().toMillis(), page.getWaitForTimeout().toMillis());
                    break;
                case PAGE_LOCATOR_ELEMENT_SINGLE_TIMEOUT:
                    instance = constructor.newInstance(page, locator, null, page.getImplicitWaitTimeout().toMillis());
                    break;
                case PAGE_LOCATOR_ELEMENT_TWO_TIMEOUTS:
                    instance = constructor.newInstance(page, locator, null, page.getImplicitWaitTimeout().toMillis(), page.getWaitForTimeout().toMillis());
                    break;
            }
        }
        return instance;
    }
}
