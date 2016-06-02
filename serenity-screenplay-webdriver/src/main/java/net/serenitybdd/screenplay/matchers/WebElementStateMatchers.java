package net.serenitybdd.screenplay.matchers;

import net.serenitybdd.screenplay.matchers.statematchers.*;
import org.hamcrest.Matcher;

public class WebElementStateMatchers {
    public static <T> Matcher<T> isVisible() {
        return new IsVisibleMatcher();
    }

    public static <T> Matcher<T> isCurrentlyVisible() {
        return new IsCurrentlyVisibleMatcher();
    }

    public static <T> Matcher<T> isEnabled() {
        return new IsEnabledMatcher();
    }

    public static <T> Matcher<T> isCurrentlyEnabled() {
        return new IsCurrentlyEnabledMatcher();
    }

    public static <T> Matcher<T> isPresent() {
        return new IsPresentMatcher();
    }

    public static <T> Matcher<T> isSelected() {
        return new IsSelectedMatcher();
    }

    public static <T> Matcher<T> containsText(String expectedText) {
        return new ContainsTextMatcher(expectedText);
    }

    public static <T> Matcher<T> containsOnlyText(String expectedText) {
        return new ContainsOnlyTextMatcher(expectedText);
    }

    public static <T> Matcher<T> containsSelectOption(String expectedText) {
        return new ContainsSelectOptionMatcher(expectedText);
    }
}
