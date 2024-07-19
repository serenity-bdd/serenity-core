package net.thucydides.model.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;

import java.util.Date;


public class DateIsSameAsMatcher extends TypeSafeMatcher<Date> {

    private final DateTime expectedDate;

    public DateIsSameAsMatcher(final Date expectedDate) {
        this.expectedDate = new DateTime(expectedDate);
    }

    public boolean matchesSafely(Date date) {
        return (DateComparator.sameDate(new DateTime(date), expectedDate));
    }

    public void describeTo(Description description) {
        description.appendText("a date that is ");
        description.appendText(DateMatcherFormatter.formatted(expectedDate));
    }
}
