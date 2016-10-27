package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsNotPresentMatcher<T extends WebElementState> extends TypeSafeMatcher<T> {

    @Override
    protected boolean matchesSafely(T element) {
        return !element.isPresent();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("is not present");
    }
}