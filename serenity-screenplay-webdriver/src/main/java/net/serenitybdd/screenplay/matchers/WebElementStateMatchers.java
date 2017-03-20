package net.serenitybdd.screenplay.matchers;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.matchers.statematchers.*;
import org.hamcrest.Matcher;

public class WebElementStateMatchers {
    public static <T extends WebElementState> Matcher<T> isVisible() {
        return new IsVisibleMatcher();
    }
    public static <T extends WebElementState> Matcher<T> isNotVisible() {
        return new IsNotVisibleMatcher();
    }

    public static <T extends WebElementState> Matcher<T> isCurrentlyVisible() {
        return new IsCurrentlyVisibleMatcher();
    }

    public static <T extends WebElementState> Matcher<T> isEnabled() {
        return new IsEnabledMatcher();
    }
    public static <T extends WebElementState> Matcher<T> isNotEnabled() {
        return new IsNotEnabledMatcher();
    }

    public static <T extends WebElementState> Matcher<T> isCurrentlyEnabled() {
        return new IsCurrentlyEnabledMatcher();
    }

    public static <T extends WebElementState> Matcher<T> isPresent() {
        return new IsPresentMatcher();
    }
    public static <T extends WebElementState> Matcher<T> isNotPresent() {
        return new IsNotPresentMatcher();
    }

    public static <T extends WebElementState> Matcher<T> isSelected() {
        return new IsSelectedMatcher();
    }
    public static <T extends WebElementState> Matcher<T> isNotSelected() {
        return new IsNotSelectedMatcher();
    }

    public static <T extends WebElementState> Matcher<T> containsText(String expectedText) {
        return new ContainsTextMatcher(expectedText);
    }

    public static <T extends WebElementState> Matcher<T> hasValue(String expectedText) {
        return new HasValueMatcher<>(expectedText);
    }

    public static <T extends WebElementState> Matcher<T> containsOnlyText(String expectedText) {
        return new ContainsOnlyTextMatcher(expectedText);
    }

    public static <T extends WebElementState> Matcher<T> containsSelectOption(String expectedText) {
        return new ContainsSelectOptionMatcher(expectedText);
    }
}
