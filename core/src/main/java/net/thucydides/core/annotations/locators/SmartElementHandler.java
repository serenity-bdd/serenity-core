package net.thucydides.core.annotations.locators;

import net.serenitybdd.core.annotations.ImplementedBy;
import net.thucydides.core.annotations.NotImplementedException;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementDescriber;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class SmartElementHandler implements InvocationHandler{
    private final ElementLocator locator;
    private final WebDriver driver;
    private final Class<?> implementerClass;
    private final long timeoutInMilliseconds;

    private Class<?> getImplementer(Class<?> interfaceType) {
    	if (!interfaceType.isInterface()){
		throw new NotImplementedException(interfaceType.getSimpleName() +
    				" is not an interface");
    	}
		Class<?> implementerClass = null;
    	ImplementedBy implBy = interfaceType.getAnnotation(ImplementedBy.class);
    	if (implBy == null){
			// todo Remove when thucydides ImplementedBy is finally removed
            net.thucydides.core.annotations.ImplementedBy implByDep = interfaceType.getAnnotation(net.thucydides.core.annotations.ImplementedBy.class);
			if(implByDep == null) {
				throw new NotImplementedException(interfaceType.getSimpleName() +
						" is not implemented by any class (or not annotated by @ImplementedBy)");
			} else {
				implementerClass = implByDep.value();
			}
	} else {
			implementerClass = implBy.value();
		}
		if (!interfaceType.isAssignableFrom(implementerClass)) {
			throw new NotImplementedException(String.format("implementer Class '%s' does not implement the interface '%s'", implementerClass, interfaceType.getName()));
		}
		return implementerClass;
	}

    public SmartElementHandler(Class<?> interfaceType, ElementLocator locator,
			WebDriver driver, long timeoutInMilliseconds) {
    	this.driver = driver;
        this.locator = locator;
        if (!WebElementFacade.class.isAssignableFrom(interfaceType)) {
            throw new NotImplementedException("interface not assignable to WebElementFacade");
        }

        this.implementerClass = getImplementer(interfaceType);
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

	public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
    	try {
	        if ("getWrappedElement".equals(method.getName())) {
	            return locator.findElement();
	        } else if ("toString".equals(method.getName())) {
				return toStringForElement();
			}
			Object webElementFacadeExt = newElementInstance(driver, locator, timeoutInMilliseconds);

	        return method.invoke(implementerClass.cast(webElementFacadeExt), objects);
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            throw e.getCause();
        }
    }

	private Object newElementInstance(WebDriver driver, ElementLocator locator, long timeoutInMilliseconds) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Constructor<?> constructor = implementerClass.getConstructor(WebDriver.class, ElementLocator.class, long.class);
		return constructor.newInstance(driver, locator, timeoutInMilliseconds);
	}

	private String toStringForElement() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		Object webElementFacadeExt = newElementInstance(driver, locator, 100);
		if (webElementFacadeExt == null) {
			return "<" + locator.toString() + ">";
		} else {
			return new WebElementDescriber().webElementDescription((WebElement) webElementFacadeExt,locator);
		}
	}

}

