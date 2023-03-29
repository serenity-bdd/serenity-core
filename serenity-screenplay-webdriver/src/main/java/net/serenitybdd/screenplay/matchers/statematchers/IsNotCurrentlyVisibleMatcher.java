package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IsNotCurrentlyVisibleMatcher<T extends WebElementState>
        extends TypeSafeMatcher<T>
        implements CheckForAbsenceHint {

    @Override
    protected boolean matchesSafely(T element) {
        return !element.isCurrentlyVisible();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an element that is not currently visible");
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText(WebElementStateDescription.forElement(item)).appendText(" was visible");
    }
}