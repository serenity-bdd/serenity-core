package net.thucydides.core.annotations.locators;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import net.serenitybdd.core.pages.PageObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Constructor;
import java.util.List;

public enum ElementContructorForm {
    WEBDRIVER_ELEMENT_TWO_TIMEOUTS(WebDriver.class, ElementLocator.class, WebElement.class, long.class, long.class),
    WEBDRIVER_ELEMENT_SINGLE_TIMEOUT(WebDriver.class, ElementLocator.class, WebElement.class, long.class),
    WEBDRIVER_LOCATOR_TWO_TIMEOUTS(WebDriver.class, ElementLocator.class, long.class, long.class),
    WEBDRIVER_LOCATOR_SINGLE_TIMEOUT(WebDriver.class, ElementLocator.class, long.class),

    //			constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, long.class);
//			instance = constructor.newInstance(page, locator, page.getImplicitWaitTimeout(), page.getWaitForTimeout());
//		}
//		catch (NoSuchMethodException e) {
//			try {
//				constructor = implementerClass.getConstructor(PageObject.class, ElementLocator.class, WebElement.class, long.class);


    PAGE_LOCATOR_ELEMENT_SINGLE_TIMEOUT(PageObject.class, ElementLocator.class, WebElement.class, long.class),
    PAGE_LOCATOR_ELEMENT_TWO_TIMEOUTS(PageObject.class, ElementLocator.class, WebElement.class, long.class),
    PAGE_LOCATOR_TWO_TIMEOUTS(PageObject.class, ElementLocator.class, long.class, long.class),
    PAGE_LOCATOR_SINGLE_TIMEOUT(PageObject.class, ElementLocator.class, long.class),
    PAGE_ELEMENT_TWO_TIMEOUTS(PageObject.class, WebElement.class, long.class, long.class),
    PAGE_ELEMENT_SINGLE_TIMEOUT(PageObject.class, WebElement.class, long.class);

    private final List<Class> parameterTypes;

    ElementContructorForm(Class... parameterTypes) {
        this.parameterTypes = ImmutableList.copyOf(parameterTypes);
    }

    public static Optional<ElementContructorForm> matchingFormFor(Class... parameterTypes) {
        for (ElementContructorForm constructorForm : ElementContructorForm.values()) {
            if (constructorForm.parameterTypes.equals(ImmutableList.copyOf(parameterTypes))) {
                return Optional.of(constructorForm);
            }
        }
        return Optional.absent();
    }



    private Optional<Constructor> findMatchingConstructorFrom(Constructor<?>[] declaredConstructors) {
        for(Constructor constructor : declaredConstructors) {
            if (parameterTypes.equals(ImmutableList.copyOf(constructor.getParameterTypes()))) {
                return Optional.of(constructor);
            }
        }
        return Optional.absent();
    }


    public static Optional<Constructor> applicableConstructorFrom(Class implementerClass) {
        Constructor<?>[] declaredConstructors = implementerClass.getDeclaredConstructors();

        for (ElementContructorForm elementContructorForm : ElementContructorForm.values()) {
            Optional<Constructor> matchingConstructor = elementContructorForm.findMatchingConstructorFrom(declaredConstructors);
            if (matchingConstructor.isPresent()) {
                return matchingConstructor;
            }
        }
        return Optional.absent();

    }

    public static Optional<ElementContructorForm> applicableConstructor(Class implementerClass) {

        Constructor<?>[] declaredConstructors = implementerClass.getDeclaredConstructors();

        for (ElementContructorForm elementContructorForm : ElementContructorForm.values()) {
            Optional<Constructor> matchingConstructor = elementContructorForm.findMatchingConstructorFrom(declaredConstructors);
            if (matchingConstructor.isPresent()) {
                return Optional.of(elementContructorForm);
            }
        }
        return Optional.absent();
    }

};
