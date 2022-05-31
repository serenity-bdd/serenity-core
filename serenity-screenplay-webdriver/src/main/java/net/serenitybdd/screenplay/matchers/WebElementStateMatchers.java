package net.serenitybdd.screenplay.matchers;

import net.serenitybdd.core.pages.WebElementState;
import net.serenitybdd.screenplay.matchers.statematchers.*;
import org.hamcrest.Matcher;

public class WebElementStateMatchers {
    /**
     * Is this element present and visible on the screen
     */
    public static <T extends WebElementState> Matcher<T> isVisible() {
        return new IsVisibleMatcher<T>();
    }

    /**
     * Is this element not visible on the screen
     */
    public static <T extends WebElementState> Matcher<T> isNotVisible() {
        return new IsNotVisibleMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isNotEmpty() {
        return new IsNotEmptyMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isEmpty() {
        return new IsEmptyMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isCurrentlyVisible() {
        return new IsCurrentlyVisibleMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isNotCurrentlyVisible() {
        return new IsNotCurrentlyVisibleMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isEnabled() {
        return new IsEnabledMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isNotEnabled() {
        return new IsNotEnabledMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isCurrentlyEnabled() {
        return new IsCurrentlyEnabledMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isNotCurrentlyEnabled() {
        return new IsNotCurrentlyEnabledMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isPresent() {
        return new IsPresentMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isNotPresent() {
        return new IsNotPresentMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isSelected() {
        return new IsSelectedMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isNotSelected() {
        return new IsNotSelectedMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> isClickable() {
        return new isClickableMatcher<T>();
    }

    public static <T extends WebElementState> Matcher<T> containsText(String expectedText) {
        return new ContainsTextMatcher<T>(expectedText);
    }

    public static <T extends WebElementState> Matcher<T> hasValue(String expectedText) {
        return new HasValueMatcher<T>(expectedText);
    }

    public static <T extends WebElementState> Matcher<T> containsOnlyText(String expectedText) {
        return new ContainsOnlyTextMatcher<T>(expectedText);
    }

    public static <T extends WebElementState> Matcher<T> containsSelectOption(String expectedText) {
        return new ContainsSelectOptionMatcher<T>(expectedText);
    }
}
