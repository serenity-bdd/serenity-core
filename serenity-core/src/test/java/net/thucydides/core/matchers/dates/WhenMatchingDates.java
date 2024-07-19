package net.thucydides.core.matchers.dates;


import net.serenitybdd.model.collect.NewSet;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Set;

import static net.thucydides.model.matchers.dates.DateMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class WhenMatchingDates {

    Date firstOfJanuary2000;
    Date januaryFirst2000;
    Date januaryFirst2001;
    Date januaryFirst2002;
    Date januaryFirst2003;
    Date januaryFirst1900;
    Date januaryFirst2100;

    @Before
    public void setupDates() {
        firstOfJanuary2000 = new DateTime(2000,1,1,12,0).toDate();
        januaryFirst2000 = new DateTime(2000,1,1,12,0).toDate();
        januaryFirst2001 = new DateTime(2001,1,1,12,0).toDate();
        januaryFirst2002 = new DateTime(2002,1,1,12,0).toDate();
        januaryFirst2003 = new DateTime(2003,1,1,12,0).toDate();
        januaryFirst1900 = new DateTime(1900,1,1,12,0).toDate();
        januaryFirst2100 = new DateTime(2100,1,1,12,0).toDate();
    }

    @Test
    public void should_be_able_to_check_whether_two_dates_are_equal() {
        assertThat(firstOfJanuary2000, isSameAs(januaryFirst2000));
    }

    @Test
    public void should_be_able_to_check_whether_two_dates_are_not_equal() {
        assertThat(januaryFirst2000, not(isSameAs(januaryFirst2001)));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_before_another() {
        assertThat(januaryFirst2000, isBefore(januaryFirst2001));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_not_before_another() {
        assertThat(januaryFirst2001, not(isBefore(januaryFirst2000)));
    }


    @Test
    public void should_be_able_to_check_whether_a_date_is_after_another() {
        assertThat(januaryFirst2001, isAfter(januaryFirst2000));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_not_after_another() {
        assertThat(januaryFirst2000, not(isAfter(januaryFirst2001)));
    }

    @Test
    public void should_be_able_to_check_whether_a_date_is_between_two_dates() {
        assertThat(januaryFirst2001, isBetween(januaryFirst2000, januaryFirst2002));
    }

    @Test
    public void a_date_on_the_boundary_is_considered_between_two_dates() {
        assertThat(januaryFirst2000, isBetween(januaryFirst2000, januaryFirst2002));
        assertThat(januaryFirst2002, isBetween(januaryFirst2000, januaryFirst2002));
    }

    @Test
    public void a_date_before_the_boundary_is_not_considered_between_two_dates() {
        assertThat(januaryFirst1900, not(isBetween(januaryFirst2000, januaryFirst2002)));
    }

    @Test
    public void a_date_after_the_boundary_is_not_considered_between_two_dates() {
        assertThat(januaryFirst2100, not(isBetween(januaryFirst2000, januaryFirst2002)));
    }

    @Test
    public void a_collection_of_dates_can_be_matched_against_another_collection_of_dates() {
        Set<Date> someDates = NewSet.of(januaryFirst2000, januaryFirst2001, januaryFirst2002);
        Set<Date> someOtherDates = NewSet.of(januaryFirst2000, januaryFirst2001, januaryFirst2002);

        assertThat(someDates, containsSameDatesAs(someOtherDates));
    }

    @Test
    public void a_collection_of_dates_fails_to_match_against_another_collection_of_dates_if_the_dates_are_different() {
        Set<Date> someDates = NewSet.of(januaryFirst2000, januaryFirst2001, januaryFirst2002);
        Set<Date> someOtherDates = NewSet.of(januaryFirst2000, januaryFirst2001, januaryFirst2003);

        assertThat(someDates, not(containsSameDatesAs(someOtherDates)));
    }

    @Test
    public void a_collection_of_dates_fails_to_match_against_another_collection_of_dates_if_the_collection_sizes_are_different() {
        Set<Date> someDates = NewSet.of(januaryFirst2000, januaryFirst2001, januaryFirst2002);
        Set<Date> someOtherDates = NewSet.of(januaryFirst2000, januaryFirst2001);

        assertThat(someDates, not(containsSameDatesAs(someOtherDates)));
    }

    @Test
    public void should_be_able_to_check_whether_two_times_are_close_together() {
        assertThat(firstOfJanuary2000, isSameAs(januaryFirst2000));
    }

}
