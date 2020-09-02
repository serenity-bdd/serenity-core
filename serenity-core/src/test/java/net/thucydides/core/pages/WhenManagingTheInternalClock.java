package net.thucydides.core.pages;

import net.serenitybdd.core.time.InternalSystemClock;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertTrue;

public class WhenManagingTheInternalClock {

    @Test
    public void should_pause_for_requested_delay() {
        InternalSystemClock clock = new InternalSystemClock();

        long startTime = System.currentTimeMillis();
        clock.pauseFor(150);
        long pauseLength = System.currentTimeMillis() - startTime;
        assertThat(pauseLength, greaterThanOrEqualTo(100L));
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_runtime_exception_if_something_goes_wrong() {
        InternalSystemClock clock = new InternalSystemClock() {
            @Override
            protected void sleepFor(long timeInMilliseconds) throws InterruptedException {
                throw new InterruptedException("For testing purposes");
            }
        };
        clock.pauseFor(50);
    }

    @Test
    public void the_system_date_provider_uses_the_system_clock_to_find_the_current_date() {
        InternalSystemClock clock = new InternalSystemClock();

        ZonedDateTime before = ZonedDateTime.now().minusSeconds(1);
        ZonedDateTime systemDate = clock.getCurrentTime();
        ZonedDateTime after = ZonedDateTime.now().plusSeconds(1);

        assertTrue(before.isBefore(systemDate));
        assertTrue(after.isAfter(systemDate));
    }


}
