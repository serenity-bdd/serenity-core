package net.thucydides.core.pages;

import net.serenitybdd.core.pages.PageObject;
import net.thucydides.core.steps.EnclosingClass;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class PageFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PageFactory.class);

    private WebDriver driver;

    public PageFactory(WebDriver driver) {
        this.driver = driver;
    }

    private WebDriver getDriver() {
        return driver;
    }

    /**
     * Create a new Page Object of the given type.
     * The Page Object must have a constructor
     *
     * @param pageObjectClass
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("unchecked")
    public <T extends net.serenitybdd.core.pages.PageObject> T createPageOfType(final Class<T> pageObjectClass) {
        T currentPage = null;
        try {
            currentPage = createFromSimpleConstructor(pageObjectClass);
            if (currentPage == null) {
                currentPage = createFromConstructorWithWebdriver(pageObjectClass);
            }

        } catch (NoSuchMethodException e) {
            LOGGER.warn("This page object does not appear have a constructor that takes a WebDriver parameter: {} ({})",
                    pageObjectClass, e.getMessage());
            thisPageObjectLooksDodgy(pageObjectClass, "This page object does not appear have a constructor that takes a WebDriver parameter");
        } catch (InvocationTargetException e) {
            // Unwrap the underlying exception
            LOGGER.warn("Failed to instantiate page of type {} ({})", pageObjectClass, e.getTargetException());
            thisPageObjectLooksDodgy(pageObjectClass,"Failed to instantiate page (" + e.getTargetException() +")");
        }catch (Exception e) {
            //shouldn't even get here
            LOGGER.warn("Failed to instantiate page of type {} ({})", pageObjectClass, e);
            thisPageObjectLooksDodgy(pageObjectClass,"Failed to instantiate page (" + e +")");
        }
        return currentPage;
    }

    private <T extends net.serenitybdd.core.pages.PageObject> T createFromSimpleConstructor(Class<T> pageObjectClass)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        T newPage = null;
        try {
            if (hasDefaultConstructor(pageObjectClass)) {
                Class[] constructorArgs = new Class[0];
                Constructor<? extends net.serenitybdd.core.pages.PageObject> constructor = pageObjectClass.getDeclaredConstructor(constructorArgs);
                constructor.setAccessible(true);
                newPage = (T) constructor.newInstance();
                newPage.setDriver(getDriver());
            } else if (hasOuterClassConstructor(pageObjectClass)) {
                Constructor<? extends net.serenitybdd.core.pages.PageObject> constructor = pageObjectClass.getDeclaredConstructor(new Class[] {pageObjectClass.getEnclosingClass()});
                constructor.setAccessible(true);
                newPage = (T) constructor.newInstance(EnclosingClass.of(pageObjectClass).newInstance());
                newPage.setDriver(getDriver());
            }

        } catch (NoSuchMethodException e) {
            // Try a different constructor
        }
        return newPage;
    }

    private <T extends net.serenitybdd.core.pages.PageObject> boolean hasDefaultConstructor(Class<T> pageObjectClass) {
        return Arrays.stream(pageObjectClass.getDeclaredConstructors())
                .anyMatch( constructor -> constructor.getParameters().length == 0 );
    }

    private <T extends net.serenitybdd.core.pages.PageObject> boolean hasOuterClassConstructor(Class<T> pageObjectClass) {
        return Arrays.stream(pageObjectClass.getConstructors())
                .anyMatch( constructor -> constructor.getParameters().length == 1
                        && constructor.getParameters()[0].getType() == pageObjectClass.getEnclosingClass() );
    }

    private <T extends net.serenitybdd.core.pages.PageObject> T createFromConstructorWithWebdriver(Class<T> pageObjectClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class[] constructorArgs = new Class[1];
        constructorArgs[0] = WebDriver.class;
        Constructor<? extends PageObject> constructor = pageObjectClass.getConstructor(constructorArgs);
        return (T) constructor.newInstance(getDriver());
    }

    private void thisPageObjectLooksDodgy(final Class<? extends PageObject> pageObjectClass, String message) {
        String errorDetails = "The page object " + pageObjectClass + " could not be instantiated:\n" + message;
        throw new WrongPageError(errorDetails);
    }

}
