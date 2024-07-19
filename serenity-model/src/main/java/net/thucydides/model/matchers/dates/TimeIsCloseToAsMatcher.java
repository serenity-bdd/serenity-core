package net.thucydides.model.matchers.dates;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Date;


public class TimeIsCloseToAsMatcher extends TypeSafeMatcher<Date> {

    private final DateTime expectedDate;
    private final DateTime minimumDate;
    private final DateTime maximumDate;
    private final Period margin;

    public TimeIsCloseToAsMatcher(final Date expected, Period margin) {
        this.expectedDate = new DateTime(expected);
        this.margin = margin;
        this.minimumDate = expectedDate.minus(margin);
        this.maximumDate = expectedDate.plus(margin);
    }

    public boolean matchesSafely(Date date) {
        DateTime specifiedDate = new DateTime(date);
        return !(specifiedDate.isBefore(minimumDate) || specifiedDate.isAfter(maximumDate));
    }

    public void describeTo(Description description) {
        description.appendText("a date that is within " + margin.toString() + " of ");
        description.appendText(DateMatcherFormatter.formatted(expectedDate));
    }
}
