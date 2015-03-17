package net.thucydides.core.annotations.locators;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractList;
import java.util.List;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WidgetObject;

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
	private final PageObject page;
	private final long implicitTimeoutInMilliseconds;
	private final long waitForTimeoutInMilliseconds;

	public SmartListHandler(ClassLoader loader, Class<?> interfaceType, ElementLocator locator, PageObject page,
							long implicitTimeoutInMilliseconds, long waitForTimeoutInMilliseconds) {
		this.loader = loader;
		this.interfaceType = interfaceType;
		this.locator = locator;
		this.page = page;
		this.implicitTimeoutInMilliseconds = implicitTimeoutInMilliseconds;
		this.waitForTimeoutInMilliseconds = waitForTimeoutInMilliseconds;
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
			if (WidgetObject.class.isAssignableFrom(interfaceType)) {
				handler = new WidgetListItemHandler(interfaceType, locator, element, page,
													implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
			}
			else if (WebElementFacade.class.isAssignableFrom(interfaceType)) {
				handler = new WebElementFacadeListItemHandler(interfaceType, locator, element, page,
															  implicitTimeoutInMilliseconds, waitForTimeoutInMilliseconds);
			}
			if (handler != null) {
				return (T) Proxy.newProxyInstance(loader, new Class[] {interfaceType}, handler);
			}
			else if (WebElement.class.isAssignableFrom(interfaceType)) {
				// element is already located
				return (T) element;
			}
			throw new RuntimeException("Unrecognized element type: " + interfaceType.getCanonicalName());
		}
	}
}
