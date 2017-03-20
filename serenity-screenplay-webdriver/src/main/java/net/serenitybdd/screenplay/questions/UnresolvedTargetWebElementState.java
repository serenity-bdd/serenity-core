package net.serenitybdd.screenplay.questions;

import net.serenitybdd.core.pages.WebElementState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 14/01/2016.
 */
public class UnresolvedTargetWebElementState implements WebElementState {

    private final String name;

    public UnresolvedTargetWebElementState(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "not so";
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
    public void shouldBeVisible() {
        throw new AssertionError("Element should be visible");
    }

    @Override
    public void shouldBeCurrentlyVisible() {
        throw new AssertionError("Element should be visible");
    }

    @Override
    public void shouldNotBeVisible() {}

    @Override
    public void shouldNotBeCurrentlyVisible() {}

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
    public void shouldContainText(String textValue) {
        throw new AssertionError("Element should contain text " + textValue);
    }

    @Override
    public void shouldContainOnlyText(String textValue) {
        throw new AssertionError("Element should contain text " + textValue);
    }

    @Override
    public void shouldContainSelectedOption(String textValue) {
        throw new AssertionError("Element should contain selected option " + textValue);
    }

    @Override
    public void shouldNotContainText(String textValue) {}

    @Override
    public void shouldBeEnabled() {
        throw new AssertionError("Element should be enabled");
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void shouldNotBeEnabled() {

    }

    @Override
    public String getSelectedVisibleTextValue() {
        return null;
    }

    @Override
    public String getSelectedValue() {
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
    public void shouldBePresent() {
        throw new AssertionError("Element should be present");
    }

    @Override
    public void shouldNotBePresent() {

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
    public WebElementState expect(String errorMessage) {
        return null;
    }
}
