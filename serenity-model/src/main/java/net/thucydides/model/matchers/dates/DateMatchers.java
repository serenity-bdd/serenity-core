package net.thucydides.model.matchers.dates;


import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Collection;
import java.util.Date;

/**
 * Hamcrest matchers to be used with Dates.
 */
public class DateMatchers {


    public static Matcher<Date> isSameAs(Date expectedDate){
        return new DateIsSameAsMatcher(expectedDate);
    }


    public static Matcher<Date> isCloseTo(Date expected, Period within){
        return new TimeIsCloseToAsMatcher(expected, within);
    }


    public static Matcher<DateTime> isCloseTo(DateTime expected, Period within){
        return new DateTimeIsCloseToAsMatcher(expected, within);
    }


    public static Matcher<Date> isBefore(Date expectedDate){
        return new DateIsBeforeMatcher(expectedDate);
    }


    public static Matcher<Date> isAfter(Date expectedDate){
        return new DateIsAfterMatcher(expectedDate);
    }


    public static Matcher<Date> isBetween(Date startDate, Date endDate){
        return new DateIsBetweenMatcher(startDate, endDate);
    }


    public static Matcher<DateTime> isSameAs(DateTime expectedDate){
        return new DateTimeIsSameAsMatcher(expectedDate);
    }


    public static Matcher<DateTime> isBefore(DateTime expectedDate){
        return new DateTimeIsBeforeMatcher(expectedDate);
    }


    public static Matcher<DateTime> isAfter(DateTime expectedDate){
        return new DateTimeIsAfterMatcher(expectedDate);
    }


    public static Matcher<DateTime> isBetween(DateTime startDate, DateTime endDate){
        return new DateTimeIsBetweenMatcher(startDate, endDate);
    }
    

    public static Matcher<Collection<DateTime>> containsSameDateTimesAs(Collection<DateTime> expectedDates) {
        return new DateTimeCollectionContainsSameDatesMatcher(expectedDates);
    }


    public static Matcher<Collection<Date>> containsSameDatesAs(Collection<Date> expectedDates) {
        return new DateCollectionContainsSameDatesMatcher(expectedDates);
    }
}
