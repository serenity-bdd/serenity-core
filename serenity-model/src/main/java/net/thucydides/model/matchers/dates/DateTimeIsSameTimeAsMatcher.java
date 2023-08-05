package net.thucydides.model.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

import static net.thucydides.model.matchers.dates.DateMatcherFormatter.formatted;


public class DateTimeIsSameTimeAsMatcher extends TypeSafeMatcher<DateTime> {

    private final DateTime expectedDate;

    public DateTimeIsSameTimeAsMatcher(final DateTime expectedDate) {
        this.expectedDate = expectedDate;
    }

    public boolean matchesSafely(DateTime provided) {
        return (DateComparator.sameDate(provided, expectedDate));
    }

    public void describeTo(Description description) {
        description.appendText("a date that is ");
        description.appendText(formatted(expectedDate));
    }
}
