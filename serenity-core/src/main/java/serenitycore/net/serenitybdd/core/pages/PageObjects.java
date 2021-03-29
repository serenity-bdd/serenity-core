package serenitycore.net.serenitybdd.core.pages;

import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class PageObjects {

    public static final String NO_WEBDRIVER_CONSTRUCTOR_MESSAGE = "This page object does not appear have a constructor that takes a WebDriver parameter";

    private final WebDriver driver;

    public PageObjects(WebDriver driver) {
        this.driver = driver;
    }

    public static PageObjects usingDriver(WebDriver driver) {
        return new PageObjects(driver);
    }

    public <T extends PageObject> T ofType(Class<T> pageObjectClass) {
        try {
            Optional<T> simplePageObject = newPageObjectWithSimpleConstructor(pageObjectClass);
            return (simplePageObject.isPresent() ? simplePageObject.get() : newPageObjectWithDriver(pageObjectClass));
        } catch (Throwable somethingWentWrong) {
            throw pageLooksDodgyExceptionBasedOn(somethingWentWrong, pageObjectClass);
        }
    }

    private <T extends PageObject> PageLooksDodgyException pageLooksDodgyExceptionBasedOn(Throwable somethingWentWrong, Class<T> pageObjectClass) {
        if (somethingWentWrong instanceof NoSuchMethodException) {
            return thisPageObjectLooksDodgy(pageObjectClass, NO_WEBDRIVER_CONSTRUCTOR_MESSAGE, somethingWentWrong);
        }
        if (somethingWentWrong instanceof InvocationTargetException) {
            return thisPageObjectLooksDodgy(pageObjectClass,"Failed to instantiate page",
                                           ((InvocationTargetException) somethingWentWrong).getTargetException());
        }
        return thisPageObjectLooksDodgy(pageObjectClass,"Failed to instantiate page", somethingWentWrong);
    }


    @SuppressWarnings("unchecked")
    private <T extends PageObject> Optional<T> newPageObjectWithSimpleConstructor(Class<T> pageObjectClass)
            throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        try {
            Class[] constructorArgs = new Class[0];
            Constructor<? extends PageObject> constructor = pageObjectClass.getConstructor(constructorArgs);
            T newPage = (T) constructor.newInstance();
            newPage.setDriver(driver);
            return Optional.of(newPage);

        } catch (NoSuchMethodException e) {
            // Try a different constructor
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T extends PageObject> T newPageObjectWithDriver(Class<T> pageObjectClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class[] constructorArgs = new Class[1];
        constructorArgs[0] = WebDriver.class;
        Constructor<? extends PageObject> constructor = pageObjectClass.getConstructor(constructorArgs);
        return (T) constructor.newInstance(driver);
    }

    private PageLooksDodgyException thisPageObjectLooksDodgy(final Class<? extends PageObject> pageObjectClass,
                                               String message,
                                               Throwable e) {
        return new PageLooksDodgyException("The page object " + pageObjectClass + " looks dodgy:\n" + message,e);
    }

}
