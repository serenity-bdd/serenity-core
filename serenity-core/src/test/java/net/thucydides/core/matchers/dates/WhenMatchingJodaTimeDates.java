package net.thucydides.core.matchers.dates;


import net.serenitybdd.model.collect.NewSet;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static net.thucydides.model.matchers.dates.DateMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class WhenMatchingJodaTimeDates {

    DateTime dateTime0;
    DateTime dateTime1;
    DateTime dateTime2;
    DateTime dateTime3;
    DateTime dateTime4;
    DateTime earlyDate;
    DateTime lateDate;

    @Before
    public void setupDates() {
        dateTime0 = new DateTime(2000,01,01,12,0);
        dateTime1 = new DateTime(2000,01,01,12,0);
        dateTime2 = new DateTime(2001,01,01,12,0);
        dateTime3 = new DateTime(2002,01,01,12,0);
        dateTime4 = new DateTime(2003,01,01,12,0);
        earlyDate = new DateTime(1900,01,01,12,0);
        lateDate = new DateTime(2100,01,01,12,0);
    }

    @Test
    public void should_be_able_to_check_whether_two_dates_are_equal() {
        assertThat(dateTime0, isSameAs(dateTime1));
    }

    @Test
    public void should_be_able_to_check_whether_two_dates_are_not_equal() {
        assertThat(dateTime1, not(isSameAs(dateTime2)));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_before_another() {
        assertThat(dateTime1, isBefore(dateTime2));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_not_before_another() {
        assertThat(dateTime2, not(isBefore(dateTime1)));
    }


    @Test
    public void should_be_able_to_check_whether_a_date_is_after_another() {
        assertThat(dateTime2, isAfter(dateTime1));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_not_after_another() {
        assertThat(dateTime1, not(isAfter(dateTime2)));
    }

    @Test
    public void should_be_able_to_work_with_date_manipulation() {
        assertThat(dateTime1, isBefore(dateTime1.plusDays(1)));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_between_two_dates() {
        assertThat(dateTime2, isBetween(dateTime1, dateTime3));
    }

    @Test
    public void a_date_on_the_boundary_is_considered_between_two_dates() {
        assertThat(dateTime1, isBetween(dateTime1, dateTime3));
        assertThat(dateTime3, isBetween(dateTime1, dateTime3));
    }

    @Test
    public void a_date_before_the_boundary_is_not_considered_between_two_dates() {
        assertThat(earlyDate, not(isBetween(dateTime1, dateTime3)));
    }

    @Test
    public void a_date_after_the_boundary_is_not_considered_between_two_dates() {
        assertThat(lateDate, not(isBetween(dateTime1, dateTime3)));
    }


    @Test
    public void a_collection_of_dates_can_be_matched_against_another_collection_of_dates() {
        Set<DateTime> someDates = NewSet.of(dateTime1, dateTime2, dateTime3);
        Set<DateTime> someOtherDates = NewSet.of(dateTime1, dateTime2, dateTime3);
        
        assertThat(someDates, containsSameDateTimesAs(someOtherDates));
    }

    @Test
    public void a_collection_of_dates_fails_to_match_against_another_collection_of_dates_if_the_dates_are_different() {
        Set<DateTime> someDates = NewSet.of(dateTime1, dateTime2, dateTime3);
        Set<DateTime> someOtherDates = NewSet.of(dateTime1, dateTime2, dateTime4);

        assertThat(someDates, not(containsSameDateTimesAs(someOtherDates)));
    }

    @Test
    public void a_collection_of_dates_fails_to_match_against_another_collection_of_dates_if_the_collection_sizes_are_different() {
        Set<DateTime> someDates = NewSet.of(dateTime1, dateTime2, dateTime3);
        Set<DateTime> someOtherDates = NewSet.of(dateTime1, dateTime2);

        assertThat(someDates, not(containsSameDateTimesAs(someOtherDates)));
    }

}
