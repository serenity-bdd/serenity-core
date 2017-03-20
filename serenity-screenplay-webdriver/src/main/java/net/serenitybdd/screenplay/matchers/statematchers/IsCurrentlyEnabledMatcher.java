package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;

public class IsCurrentlyEnabledMatcher<T extends WebElementState> extends BaseWebElementStateMatcher<T> {

    @Override
    protected boolean matchesSafely(T element) {
        return existing(element).isCurrentlyEnabled();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an element that is currently enabled");
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText(WebElementStateDescription.forElement(item)).appendText(" was not enabled");
    }

}