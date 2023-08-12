package net.thucydides.core.matchers.dates;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static net.thucydides.model.matchers.dates.DateMatchers.isCloseTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.joda.time.Period.minutes;

public class WhenMatchingTimes {

    Date januaryFirst2000At10_00am;
    Date januaryFirst2000At10_01am;
    Date januaryFirst2000At10_06am;

    DateTime januaryFirst2001At10_00am;
    DateTime januaryFirst2001At10_01am;
    DateTime januaryFirst2001At10_06am;

    @Before
    public void setupDates() {
        januaryFirst2000At10_00am = new DateTime(2000,1,1,10,0).toDate();
        januaryFirst2000At10_01am = new DateTime(2000,1,1,10,1).toDate();
        januaryFirst2000At10_06am = new DateTime(2000,1,1,10,6).toDate();

        januaryFirst2001At10_00am = new DateTime(2000,1,1,10,0);
        januaryFirst2001At10_01am = new DateTime(2000,1,1,10,1);
        januaryFirst2001At10_06am = new DateTime(2000,1, 1,10,6);

    }

    @Test
    public void should_be_able_to_check_whether_two_times_are_within_a_specified_margin() {
        assertThat(januaryFirst2000At10_00am, isCloseTo(januaryFirst2000At10_01am, minutes(5)));
    }

    @Test
    public void should_be_able_to_check_whether_two_times_are_far_apart() {
        assertThat(januaryFirst2000At10_00am, not(isCloseTo(januaryFirst2000At10_06am, minutes(5))));
    }

    @Test
    public void should_be_able_to_check_whether_two_datetimes_are_within_a_specified_margin() {
        assertThat(januaryFirst2001At10_00am, isCloseTo(januaryFirst2001At10_01am, minutes(5)));
    }

    @Test
    public void should_be_able_to_check_whether_two_datetimes_are_far_apart() {
        assertThat(januaryFirst2001At10_00am, not(isCloseTo(januaryFirst2001At10_06am, minutes(5))));
    }

}
