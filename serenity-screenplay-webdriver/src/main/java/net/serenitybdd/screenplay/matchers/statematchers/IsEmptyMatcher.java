package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsEmptyMatcher<T extends WebElementState> extends TypeSafeMatcher<T> {
    public IsEmptyMatcher() {
    }

    protected boolean matchesSafely(T element) {
        return !element.isVisible() || element.getText().isEmpty();
    }

    public void describeTo(Description description) {
        description.appendText("an element that is empty");
    }

    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText(WebElementStateDescription.forElement(item));
    }
}