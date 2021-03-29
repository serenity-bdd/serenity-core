package serenityscreenplaywebdriver.net.serenitybdd.screenplay.matchers.statematchers;

import serenitycore.net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ContainsTextMatcher<T extends WebElementState> extends TypeSafeMatcher<T> {

    private final String expectedText;

    public ContainsTextMatcher(String expectedText) {
        this.expectedText = expectedText;
    }

    @Override
    protected boolean matchesSafely(T element) {
        return element.containsText(expectedText);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an element containing '").appendText(expectedText).appendText("'");
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText(" was ").appendText(WebElementStateDescription.forElement(item));
    }

}