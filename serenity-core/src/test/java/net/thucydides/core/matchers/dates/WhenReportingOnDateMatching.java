package net.thucydides.core.matchers.dates;


import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Parameterized.class)
public class WhenReportingOnDateMatching {

    private static DateTime januaryOne2000 = new DateTime(2000, 1, 1, 1, 0);
    private static DateTime januaryOne2001 = new DateTime(2001, 1, 1, 1, 0);

    private static List<DateTime> dateTimes = ImmutableList.of(januaryOne2000, januaryOne2001);
    private static List<Date> dates = ImmutableList.of(januaryOne2000.toDate(), januaryOne2001.toDate());

    @Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {new DateIsAfterMatcher(januaryOne2000.toDate()), "a date that is after 1 Jan 2000 01:00:00"},
                {new DateIsBeforeMatcher(januaryOne2000.toDate()), "a date that is before 1 Jan 2000 01:00:00"},
                {new DateIsBetweenMatcher(januaryOne2000.toDate(), januaryOne2001.toDate()), "a date that is between 1 Jan 2000 01:00:00 and 1 Jan 2001 01:00:00"},
                {new DateIsSameAsMatcher(januaryOne2000.toDate()), "a date that is 1 Jan 2000 01:00:00"},
                {new DateTimeIsAfterMatcher(januaryOne2000), "a date that is after 1 Jan 2000 01:00:00"},
                {new DateTimeIsBeforeMatcher(januaryOne2000), "a date that is before 1 Jan 2000 01:00:00"},
                {new DateTimeIsBetweenMatcher(januaryOne2000, januaryOne2001), "a date that is between 1 Jan 2000 01:00:00 and 1 Jan 2001 01:00:00"},
                {new DateTimeIsSameAsMatcher(januaryOne2000), "a date that is 1 Jan 2000 01:00:00"},
                {new DateCollectionContainsSameDatesMatcher(dates),         "a collection of dates containing [1 Jan 2000 01:00:00, 1 Jan 2001 01:00:00]"},
                {new DateTimeCollectionContainsSameDatesMatcher(dateTimes), "a collection of dates containing [1 Jan 2000 01:00:00, 1 Jan 2001 01:00:00]"}}
    );
    }

    private final Matcher<?> dateMatcher;
    private final String expectedMessage;

    public WhenReportingOnDateMatching(Matcher<?> dateMatcher, String expectedMessage) {
        this.expectedMessage = expectedMessage;
        this.dateMatcher = dateMatcher;
    }

    @Test
    public void matcher_message_should_be_accurate() {
        assertThat(dateMatcher.toString(), is(expectedMessage));
    }


}
