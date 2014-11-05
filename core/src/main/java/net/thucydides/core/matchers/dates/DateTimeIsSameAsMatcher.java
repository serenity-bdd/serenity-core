package net.thucydides.core.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

import static net.thucydides.core.matchers.dates.DateMatcherFormatter.formatted;


class DateTimeIsSameAsMatcher extends TypeSafeMatcher<DateTime> {

    private final DateTime expectedDate;

    public DateTimeIsSameAsMatcher(final DateTime expectedDate) {
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
