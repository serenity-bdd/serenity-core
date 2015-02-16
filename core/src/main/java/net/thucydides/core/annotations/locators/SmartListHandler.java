package net.thucydides.core.annotations.locators;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractList;
import java.util.List;

import net.serenitybdd.core.pages.WebElementFacade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

/**
 * Handles Lists of {@link WebElementFacade}s.
 * 
 * @author Joe Nasca
 */
public class SmartListHandler implements InvocationHandler {

	private final ClassLoader loader;
	private final Class<?> interfaceType;
	private final ElementLocator locator;
	private final WebDriver driver;
	private final long timeoutInMilliseconds;

	public SmartListHandler(ClassLoader loader, Class<?> interfaceType, ElementLocator locator, WebDriver driver, long timeoutInMilliseconds) {
		this.loader = loader;
		this.interfaceType = interfaceType;
		this.locator = locator;
		this.driver = driver;
		this.timeoutInMilliseconds = timeoutInMilliseconds;
	}

	@Override
	public Object invoke(Object object, Method method, Object[] objects) throws Throwable {
		List<WebElement> elements = locator.findElements();
		List<?> facadeList = new FacadeList<>(elements);
		try {
			return method.invoke(facadeList, objects);
		}
		catch (InvocationTargetException e) {
			// Unwrap the underlying exception
			throw e.getCause();
		}
	}

	/**
	 * A proxy List of elements which are lazily constructed and proxied if needed.
	 * 
	 * @author Joe Nasca
	 * @param <T>
	 */
	private class FacadeList<T> extends AbstractList<T> {

		private final List<WebElement> elms;
		
		private FacadeList(List<WebElement> elms) {
			this.elms = elms;
		}

		@Override
		public T get(int index) {
			WebElement elm = elms.get(index);
			return newProxyElementOfList(elm);
		}

		@Override
		public int size() {
			return elms.size();
		}
		
		@SuppressWarnings("unchecked")
		private T newProxyElementOfList(WebElement element) {
			InvocationHandler handler = null;
			if (WebElementFacade.class.isAssignableFrom(interfaceType)) {
				handler = new WebElementFacadeListItemHandler(interfaceType, locator, element, driver, timeoutInMilliseconds);
			}
			if (handler != null) {
				return (T) Proxy.newProxyInstance(loader, new Class[] {interfaceType}, handler);
			}
			if (WebElement.class.isAssignableFrom(interfaceType)) {
				// no need to proxy
				return (T) element;
			}
			throw new RuntimeException("Unrecognized element type: " + interfaceType.getCanonicalName());
		}
	}
}
