package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsCurrentlyVisibleMatcher<T extends WebElementState> extends TypeSafeMatcher<T> {

    @Override
    protected boolean matchesSafely(T element) {
        return element.isCurrentlyVisible();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is currently visible");
    }
}