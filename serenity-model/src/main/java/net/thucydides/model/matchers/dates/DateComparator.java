package net.thucydides.model.matchers.dates;

import org.joda.time.DateTime;

import java.util.Date;

public class DateComparator {

    public static boolean sameDate(DateTime date, DateTime expectedDate) {
        return (date.getDayOfMonth() == expectedDate.getDayOfMonth())
                && (date.getMonthOfYear() == expectedDate.getMonthOfYear())
                && (date.getYear() == expectedDate.getYear());
    }

    public static boolean sameDate(Date date, Date expectedDate) {

        DateTime dateTime = new DateTime(date);
        DateTime expectedDateTime = new DateTime(expectedDate);

        return (dateTime.getDayOfMonth() == expectedDateTime.getDayOfMonth())
                && (dateTime.getMonthOfYear() == expectedDateTime.getMonthOfYear())
                && (dateTime.getYear() == expectedDateTime.getYear());
    }
}
