package net.thucydides.model.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

import static net.thucydides.model.matchers.dates.DateMatcherFormatter.formatted;


public class DateTimeIsBetweenMatcher extends TypeSafeMatcher<DateTime> {

    private final DateTime startDate;
    private final DateTime endDate;

    public DateTimeIsBetweenMatcher(final DateTime startDate, final DateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean matchesSafely(DateTime provided) {
        return (provided.isEqual(startDate) || provided.isAfter(startDate))
                && (provided.isEqual(endDate) || provided.isBefore(endDate));
    }

    public void describeTo(Description description) {
        description.appendText("a date that is between ");
        description.appendText(formatted(startDate));
        description.appendText(" and ");
        description.appendText(formatted(endDate));
    }
}
