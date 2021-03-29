package serenityscreenplay.net.serenitybdd.screenplay.matchers;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;

public class ReportedErrorMessages {
    public static <E> Matcher<Iterable<? extends E>> reportsErrors(final E expected) {
        return new TypeSafeDiagnosingMatcher<Iterable<? extends E>>() {
            @Override
            public void describeTo(Description description) {
                description.appendValue(expected).appendText(" should be displayed");
            }

            @Override
            protected boolean matchesSafely(Iterable<? extends E> items, Description mismatchDescription) {
                mismatchDescription.appendText(" the visible error messages were ").appendValue(items);
                if (expected instanceof ArrayList) {
                    return Matchers.hasItems(((ArrayList) expected).toArray()).matches(items);
                }
                return Matchers.hasItems(expected).matches(items);
            }
        };
    }
}
