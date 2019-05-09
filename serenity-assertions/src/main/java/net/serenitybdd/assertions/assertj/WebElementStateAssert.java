package net.serenitybdd.assertions.assertj;

import net.serenitybdd.core.pages.WebElementState;
import org.assertj.core.api.AbstractAssert;

public class WebElementStateAssert extends AbstractAssert<WebElementStateAssert, WebElementState> {
    public WebElementStateAssert(WebElementState webElementState) {
        super(webElementState, WebElementStateAssert.class);
    }

    public static WebElementStateAssert assertThatElement(WebElementState actual) {
        return assertThat(actual);
    }
    public static WebElementStateAssert assertThat(WebElementState actual) {
        return new WebElementStateAssert(actual);
    }

    public WebElementStateAssert isVisible() {
        if (!actual.isVisible()) {
            failWithMessage("Expected element <%s> to be visible", actual);
        }
        return this;
    }

    public WebElementStateAssert isEnabled() {
        if (!actual.isEnabled()) {
            failWithMessage("Expected element <%s> to be enabled", actual);
        }
        return this;
    }

    public WebElementStateAssert isPresent() {
        if (!actual.isPresent()) {
            failWithMessage("Expected element <%s> to be present", actual);
        }
        return this;
    }

    public WebElementStateAssert isClickable() {
        if (!actual.isClickable()) {
            failWithMessage("Expected element <%s> to be clickable", actual);
        }
        return this;
    }

    public WebElementStateAssert isSelected() {
        if (!actual.isSelected()) {
            failWithMessage("Expected element <%s> to be selected", actual);
        }
        return this;
    }

    public WebElementStateAssert isCurrentlyEnabled() {
        if (!actual.isCurrentlyEnabled()) {
            failWithMessage("Expected element <%s> to be currently enabled", actual);
        }
        return this;
    }

    public WebElementStateAssert isNotVisible() {
        if (actual.isVisible()) {
            failWithMessage("Expected element <%s> not to be visible", actual);
        }
        return this;
    }

    public WebElementStateAssert isCurrentlyVisible() {
        if (!actual.isCurrentlyVisible()) {
            failWithMessage("Expected element <%s> to be currently visible", actual);
        }
        return this;
    }

    public WebElementStateAssert hasFocus() {
        if (!actual.hasFocus()) {
            failWithMessage("Expected element <%s> to be have the focus", actual);
        }
        return this;
    }

    public WebElementStateAssert containsText(String expectedText) {
        if (!actual.containsText(expectedText)) {
            failWithMessage("Expected element <%s> to contain text '<%s'> but actual text was '%s'", expectedText, actual.getTextValue());
        }
        return this;
    }

    public WebElementStateAssert containsValue(String expectedValue) {
        if (!actual.containsValue(expectedValue)) {
            failWithMessage("Expected element <%s> to have value '%s' but was '%s'", expectedValue, actual.getValue());
        }
        return this;
    }

    public WebElementStateAssert containsOnlyText(String expectedText) {
        if (!actual.containsOnlyText(expectedText)) {
            failWithMessage("Expected element <%s> to contain only text '%s' but was '%s'", expectedText, actual.getTextValue());
        }
        return this;
    }

    public WebElementStateAssert containsSelectOption(String expectedOption) {
        if (!actual.containsSelectOption(expectedOption)) {
            failWithMessage("Expected element <%s> to contain the select option '%s' but options were '%s'", expectedOption, actual.getSelectOptions());
        }
        return this;
    }

}
