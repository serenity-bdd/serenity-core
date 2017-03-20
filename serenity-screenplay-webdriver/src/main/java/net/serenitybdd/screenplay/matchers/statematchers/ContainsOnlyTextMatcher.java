package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ContainsOnlyTextMatcher<T extends WebElementState> extends TypeSafeMatcher<T> {

    private final String expectedText;

    public ContainsOnlyTextMatcher(String expectedText) {
        this.expectedText = expectedText;
    }

    @Override
    protected boolean matchesSafely(T element) {
        return element.containsOnlyText(expectedText);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an element containing only '").appendText(expectedText).appendText("'");
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendText(WebElementStateDescription.forElement(item));
    }
}