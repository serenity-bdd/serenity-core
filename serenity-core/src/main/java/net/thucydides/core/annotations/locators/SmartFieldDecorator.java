package net.thucydides.core.annotations.locators;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AndroidFindBys;
import io.appium.java_client.pagefactory.iOSFindBy;
import io.appium.java_client.pagefactory.iOSFindBys;
import net.serenitybdd.core.annotations.findby.FindBy;
import net.serenitybdd.core.annotations.findby.di.CustomFindByAnnotationProviderService;
import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.core.pages.WebElementFacade;
import net.serenitybdd.core.pages.WebElementFacadeImpl;
import net.serenitybdd.core.pages.WidgetObject;
import net.thucydides.core.guice.Injectors;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementListHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

public class SmartFieldDecorator implements FieldDecorator {

    protected ElementLocatorFactory factory;
    protected WebDriver driver;
    protected PageObject page;
    private CustomFindByAnnotationProviderService customFindByAnnotationProviderService;

    public SmartFieldDecorator(ElementLocatorFactory factory, WebDriver driver, PageObject pageObject) {
       this(factory, driver, pageObject, Injectors.getInjector().getInstance(CustomFindByAnnotationProviderService.class));
    }

    public SmartFieldDecorator(ElementLocatorFactory factory, WebDriver driver, PageObject pageObject,
                               CustomFindByAnnotationProviderService customFindByAnnotationProviderService) {
        this.factory = factory;
        this.driver = driver;
        this.page = pageObject;
        this.customFindByAnnotationProviderService = customFindByAnnotationProviderService;
    }

    public Object decorate(ClassLoader loader, Field field) {
        if (!(WebElement.class.isAssignableFrom(field.getType()) || isDecoratableList(field))
        		// skip members of the base class
        		|| field.getDeclaringClass() == WebElementFacadeImpl.class) {
            return null;
        }
        ElementLocator locator = factory.createLocator(field);
        if (locator == null) {
            return null;
        }

        Class<?> fieldType = field.getType();

        if (WebElement.class.isAssignableFrom(fieldType)) {
            return proxyForLocator(loader, fieldType, locator);
        } else if (List.class.isAssignableFrom(fieldType)) {
            Class<?> erasureClass = getErasureClass(field);
            return proxyForListLocator(loader, erasureClass, locator);
        } else {
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    private Class getErasureClass(Field field) {
        Type genericType = field.getGenericType();
        if (!(genericType instanceof ParameterizedType)) {
            return null;
        }
        return (Class) ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    @SuppressWarnings("rawtypes")
    private boolean isDecoratableList(Field field) {
        if (!List.class.isAssignableFrom(field.getType())) {
            return false;
        }
        Class erasureClass = getErasureClass(field);
        if (erasureClass == null || !WebElement.class.isAssignableFrom(erasureClass)) {
            return false;
        }
        return annotatedByLegalFindByAnnotation(field) ||  isAnnotatedByCustomFindByAnnotation(field);
    }

    private boolean isAnnotatedByCustomFindByAnnotation(Field field) {
        return customFindByAnnotationProviderService.getCustomFindByAnnotationServices().stream()
                .anyMatch(annotationService ->annotationService.isAnnotatedByCustomFindByAnnotation(field));
    }

    private final static List<Class<? extends Annotation>> LEGAL_ANNOTATIONS
            = Arrays.asList(FindBy.class,
                            net.thucydides.core.annotations.findby.FindBy.class,
                            org.openqa.selenium.support.FindBy.class,
                            FindBys.class,
                            FindAll.class,
                            AndroidFindBy.class,
                            AndroidFindBys.class,
                            iOSFindBy.class,
                            iOSFindBys.class);


    private boolean annotatedByLegalFindByAnnotation(Field field) {
        for (Annotation annotation : field.getAnnotations()) {
            if (LEGAL_ANNOTATIONS.contains(annotation.annotationType())) {
                return true;
            }
        }
        return false;
    }

    /* Generate a type-parameterized locator proxy for the element in question. */
    @SuppressWarnings("unchecked")
    protected <T> T proxyForLocator(ClassLoader loader, Class<T> interfaceType, ElementLocator locator) {
        InvocationHandler handler;
        T proxy = null;
        if (WidgetObject.class.isAssignableFrom(interfaceType)) {
        	handler = new SmartWidgetHandler(interfaceType, locator, page);
            proxy = (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType}, handler);
        }
        else if (WebElementFacade.class.isAssignableFrom(interfaceType)) {
            handler = new SmartElementHandler(interfaceType, locator, page);
            proxy = (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType}, handler);
        } else {
            handler = new LocatingElementHandler(locator);
            proxy = (T) Proxy.newProxyInstance(loader,
                    new Class[]{WebElement.class, WrapsElement.class, Locatable.class}, handler);
        }
        return proxy;
    }

	@SuppressWarnings("unchecked")
	protected <T> List<T> proxyForListLocator(ClassLoader loader, Class<T> interfaceType, ElementLocator locator) {
		InvocationHandler handler = null;
		if (net.serenitybdd.core.pages.WebElementFacade.class.isAssignableFrom(interfaceType)) {
			handler = new SmartListHandler(loader, interfaceType, locator, page, page.implicitTimoutMilliseconds(), page.waitForTimeoutInMilliseconds());
		}
		else {
			handler = new LocatingElementListHandler(locator);
		}
		return (List<T>) Proxy.newProxyInstance(loader, new Class[]{List.class}, handler);
	}
}