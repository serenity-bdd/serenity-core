package net.serenitybdd.core.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.Arrays;
import java.util.List;

public class WebElementExpectations {

    private final static List<String> HTML_FORM_TAGS = Arrays.asList("input", "button", "select", "textarea", "link", "option");

    public static ExpectedCondition<Boolean> elementIsDisplayed(final WebElementFacade element) {
        return new ExpectedCondition<Boolean>() {
            private WebElementFacade element;
            public ExpectedCondition<Boolean> forElement(WebElementFacade element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                return element.isCurrentlyVisible();
            }

            @Override
            public String toString() {
                return element.toString() + " to be displayed";
            }
        }.forElement(element);
    }


    public static ExpectedCondition<Boolean> elementIsPresent(final WebElementFacade element) {
        return new ExpectedCondition<Boolean>() {
            private WebElementFacade element;
            public ExpectedCondition<Boolean> forElement(WebElementFacade element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                return element.isPresent();
            }

            @Override
            public String toString() {
                return element.toString() + " to be present";
            }

        }.forElement(element);
    }

    public static ExpectedCondition<Boolean> elementIsEnabled(final WebElementFacadeImpl element) {
        return new ExpectedCondition<Boolean>() {
            private WebElementFacadeImpl element;
            public ExpectedCondition<Boolean> forElement(WebElementFacadeImpl element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                WebElement resolvedElement = element.getElement();
                return ((resolvedElement != null) && (!isDisabledField(element)));
            }

            @Override
            public String toString() {
                return element.toString() + " to be enabled";
            }

        }.forElement(element);
    }


    public static ExpectedCondition<Boolean> elementIsNotEnabled(final WebElementFacade element) {
        return new ExpectedCondition<Boolean>() {
            private WebElementFacade element;
            public ExpectedCondition<Boolean> forElement(WebElementFacade element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                return element.isDisabled();
            }

            @Override
            public String toString() {
                return element.toString() + " to not be enabled";
            }

        }.forElement(element);
    }


    public static ExpectedCondition<Boolean> elementIsClickable(final WebElementFacadeImpl element) {
        return new ExpectedCondition<Boolean>() {
            private WebElementFacadeImpl element;
            public ExpectedCondition<Boolean> forElement(WebElementFacadeImpl element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                WebElement resolvedElement = element.getElement();
                return ((resolvedElement != null) && (resolvedElement.isDisplayed()) && resolvedElement.isEnabled());
            }

            @Override
            public String toString() {
                return element.toString() + " to be clickable";
            }

        }.forElement(element);
    }

    public static ExpectedCondition<Boolean> elementIsNotDisplayed(final WebElementFacade element) {
        return new ExpectedCondition<Boolean>() {
            private WebElementFacade element;
            public ExpectedCondition<Boolean> forElement(WebElementFacade element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                return !element.isCurrentlyVisible();
            }

            @Override
            public String toString() {
                return element.toString() + " to be not displayed";
            }
        }.forElement(element);
    }


    public static ExpectedCondition<Boolean> elementIsNotPresent(final WebElementFacade element) {
        return new ExpectedCondition<Boolean>() {
            private WebElementFacade element;
            public ExpectedCondition<Boolean> forElement(WebElementFacade element) {
                this.element = element;
                return this;
            }

            public Boolean apply(WebDriver driver) {
                return !element.isPresent();
            }

            @Override
            public String toString() {
                return element.toString() + " to be not present";
            }

        }.forElement(element);
    }

    private static boolean isDisabledField(WebElement element) {
        return (isAFormElement(element) && (!element.isEnabled()));
    }

    private static boolean isAFormElement(WebElement element) {
        if ((element == null) || (element.getTagName() == null)) {
            return false;
        }
        String tag = element.getTagName().toLowerCase();
        return HTML_FORM_TAGS.contains(tag);

    }


}
