package net.thucydides.core.annotations.locators;

import com.google.common.collect.ImmutableList;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.PageObject;
import net.thucydides.core.pages.WebElementFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementListHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.List;

public class SmartFieldDecorator implements FieldDecorator {

    protected ElementLocatorFactory factory;
    protected WebDriver driver;
    protected PageObject pageObject;

    public SmartFieldDecorator(ElementLocatorFactory factory, WebDriver driver,
                               PageObject pageObject) {
        this.driver = driver;
        this.factory = factory;
        this.pageObject = pageObject;
    }

    public Object decorate(ClassLoader loader, Field field) {
        if (!(WebElement.class.isAssignableFrom(field.getType()) || isDecoratableList(field))) {
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
        return annotatedByLegalFindByAnnotation(field);
    }

    private final static List<Class<? extends Annotation>> LEGAL_ANNOTATIONS
            = ImmutableList.of(FindBy.class,
            org.openqa.selenium.support.FindBy.class,
            FindBys.class);


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
        if (WebElementFacade.class.isAssignableFrom(interfaceType)) {
            handler = new SmartElementHandler(interfaceType, locator, driver, pageObject.waitForTimeoutInMilliseconds());
            proxy = (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType}, handler);
        } else {
            handler = new LocatingElementHandler(locator);
            proxy = (T) Proxy.newProxyInstance(loader,
                    new Class[]{WebElement.class, WrapsElement.class, Locatable.class}, handler);
        }

        return proxy;
    }

    /* generates a proxy for a list of elements to be wrapped. */
    @SuppressWarnings("unchecked")
    protected <T> List<T> proxyForListLocator(ClassLoader loader, Class<T> interfaceType, ElementLocator locator) {
        InvocationHandler handler = new LocatingElementListHandler(locator);
        List<T> proxy;
        proxy = (List<T>) Proxy.newProxyInstance(
                loader, new Class[]{List.class}, handler);
        return proxy;
    }
}