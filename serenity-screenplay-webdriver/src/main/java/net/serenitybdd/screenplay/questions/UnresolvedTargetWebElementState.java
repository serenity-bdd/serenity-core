package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14/01/2016.
 */
public class UnresolvedTargetWebElementState implements WebElementState {

    private final String name;
    private String selector = "";

    public UnresolvedTargetWebElementState(String name) {
        this.name = name;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    @Override
    public String toString() {
        if (selector.isEmpty())
            return name;
        else
            return "[" + name + " located with: " + selector + "]";
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isCurrentlyVisible() {
        return false;
    }

    @Override
    public boolean isCurrentlyEnabled() {
        return false;
    }

    @Override
    public WebElementState shouldBeVisible() {
        throw new AssertionError("Element should be visible");
    }

    @Override
    public WebElementState shouldBeCurrentlyVisible() {
        throw new AssertionError("Element should be visible");
    }

    @Override
    public WebElementState shouldNotBeVisible() {
        return this;
    }

    @Override
    public WebElementState shouldNotBeCurrentlyVisible() {
        return this;
    }

    @Override
    public boolean hasFocus() {
        return false;
    }

    @Override
    public boolean containsText(String value) {
        return false;
    }

    @Override
    public boolean containsValue(String value) {
        return false;
    }

    @Override
    public boolean containsOnlyText(String value) {
        return false;
    }

    @Override
    public boolean containsSelectOption(String value) {
        return false;
    }

    @Override
    public WebElementState shouldContainText(String textValue) {
        throw new AssertionError("Element should contain text " + textValue);
    }

    @Override
    public WebElementState shouldContainOnlyText(String textValue) {
        throw new AssertionError("Element should contain text " + textValue);
    }

    @Override
    public WebElementState shouldContainSelectedOption(String textValue) {
        throw new AssertionError("Element should contain selected option " + textValue);
    }

    @Override
    public WebElementState shouldNotContainText(String textValue) {
        return this;
    }

    @Override
    public WebElementState shouldBeEnabled() {
        throw new AssertionError("Element should be enabled");
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public WebElementState shouldNotBeEnabled() {
        return this;
    }

    @Override
    public String getSelectedVisibleTextValue() {
        return null;
    }

    @Override
    public List<String> getSelectedVisibleTexts() {
        return null;
    }

    @Override
    public String getSelectedValue() {
        return null;
    }

    @Override
    public List<String> getSelectedValues() {
        return null;
    }

    @Override
    public List<String> getSelectOptions() {
        return new ArrayList<>();
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public WebElementState shouldBePresent() {
        throw new AssertionError("Element should be present");
    }

    @Override
    public WebElementState shouldNotBePresent() {
        return this;
    }

    @Override
    public WebElementState shouldBeSelected() {
        return this;
    }

    @Override
    public WebElementState shouldNotBeSelected() {
        return this;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public String getTextValue() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    @Override
    public WebElementState expect(String errorMessage) {
        return null;
    }

    @Override
    public boolean isClickable() {
        return false;
    }
}
