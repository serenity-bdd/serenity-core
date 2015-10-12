package net.thucydides.core.annotations.locators;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementDescriber;
import net.thucydides.core.annotations.NotImplementedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Similar to {@link SmartElementHandler} but wraps a concrete WebElement
 * instead of an ElementLocator.
 * 
 * @author Joe Nasca
 * @param <T>	the target interface
 */
public abstract class AbstractListItemHandler<T> implements InvocationHandler {
	
	protected final ElementLocator locator;
    protected final WebElement element;
    protected final PageObject page;
    protected final Class<?> implementerClass;
    protected final long implicitTimeoutInMilliseconds;
    protected final long waitForTimeoutInMilliseconds;

    /**
     * Constructor.
     * @param targetInterface	usually WidgetObject or WebElementFacade
     * @param locator			the locator of the List containing this element
     */
    public AbstractListItemHandler(Class<T> targetInterface, Class<?> interfaceType, ElementLocator locator, WebElement element,
                                   PageObject page, long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
    	this.locator = locator;
    	this.page = page;
        this.element = element;
        if (!targetInterface.isAssignableFrom(interfaceType)) {
            throw new NotImplementedException("interface not assignable to " + targetInterface.getSimpleName());
        }
        this.implementerClass = new WebElementFacadeImplLocator().getImplementer(interfaceType);
        this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
        this.waitForTimeoutInMilliseconds = waitForTimeoutInMilliseconds;
    }

    @Override
	public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
    	try {
	        if ("getWrappedElement".equals(method.getName())) {
	            return element;
	        } else if ("toString".equals(method.getName())) {
				return toStringForElement();
			}
			Object webElementFacadeExt = newElementInstance();

	        return method.invoke(implementerClass.cast(webElementFacadeExt), objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }

	protected abstract Object newElementInstance() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;

	private String toStringForElement() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		return new WebElementDescriber().webElementDescription(element, locator);
	}
}
