package net.serenitybdd.screenplay.matchers.statematchers;

import net.serenitybdd.core.pages.WebElementState;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ContainsSelectOptionMatcher<T extends WebElementState> extends TypeSafeMatcher<T> {

    private final String expectedOption;

    public ContainsSelectOptionMatcher(String expectedText) {
        this.expectedOption = expectedText;
    }

    @Override
    protected boolean matchesSafely(T element) {
        return element.containsSelectOption(expectedOption);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("an element containing the option'").appendText(expectedOption).appendText("'");
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText("was ")
                .appendValueList("[",",","]",item.getSelectOptions())
                .appendText(WebElementStateDescription.forElement(item));
    }

}