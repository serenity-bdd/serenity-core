package net.thucydides.core.annotations.locators;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.annotations.NotImplementedException;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Base class for handlers of non-List members.
 * @author Joe Nasca
 * @param <T>	the target interface
 */
public abstract class AbstractSingleItemHandler<T> implements InvocationHandler {

    protected final ElementLocator locator;
    protected final PageObject page;
    protected final Class<?> implementerClass;

    public AbstractSingleItemHandler(Class<T> targetInterface, Class<?> interfaceType, ElementLocator locator,
			PageObject page) {
    	this.page = page;
        this.locator = locator;
        if (!targetInterface.isAssignableFrom(interfaceType)) {
            throw new NotImplementedException("interface not assignable to " + targetInterface.getSimpleName());
        }

        this.implementerClass = new WebElementFacadeImplLocator().getImplementer(interfaceType);
    }

    @Override
	public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
    	try {
	        if ("getWrappedElement".equals(method.getName())) {
	            return locator.findElement();
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

	protected abstract Object newElementInstance() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;

	private String toStringForElement() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Object webElementFacadeExt = newElementInstance();
		if (webElementFacadeExt == null) {
			return "<" + locator.toString() + ">";
		} else {
			return webElementFacadeExt.toString();//new WebElementDescriber().webElementDescription((WebElement) webElementFacadeExt,locator);
		}
	}

}
