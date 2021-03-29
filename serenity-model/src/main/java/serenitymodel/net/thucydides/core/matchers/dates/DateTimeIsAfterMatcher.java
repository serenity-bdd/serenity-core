package serenitymodel.net.thucydides.core.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;


public class DateTimeIsAfterMatcher extends TypeSafeMatcher<DateTime> {

    private final DateTime expectedDate;

    public DateTimeIsAfterMatcher(final DateTime expectedDate) {
        this.expectedDate = expectedDate;
    }

    public boolean matchesSafely(DateTime provided) {
        return provided.isAfter(expectedDate);
    }

    public void describeTo(Description description) {
        description.appendText("a date that is after ");
        description.appendText(DateMatcherFormatter.formatted(expectedDate));
    }
}
