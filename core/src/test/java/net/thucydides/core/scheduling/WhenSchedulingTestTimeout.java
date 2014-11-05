package net.thucydides.core.scheduling;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;

public class WhenSchedulingTestTimeout {


    @Mock
    FluentWaitWithRefresh<WebDriver> fluentWait;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_be_able_to_schedule_a_timeout_for_one_minute() {
        new TimeoutSchedule<WebDriver>(fluentWait, 1).minute();
        verify(fluentWait).withTimeout(1, TimeUnit.MINUTES);
    }

    @Test
    public void should_be_able_to_schedule_a_timeout_for_multiple_minutes() {
        new TimeoutSchedule<WebDriver>(fluentWait, 2).minutes();
        verify(fluentWait).withTimeout(2, TimeUnit.MINUTES);
    }

    @Test
    public void should_be_able_to_schedule_a_timeout_for_one_second() {
        new TimeoutSchedule<WebDriver>(fluentWait, 1).second();
        verify(fluentWait).withTimeout(1, TimeUnit.SECONDS);
    }

    @Test
    public void should_be_able_to_schedule_a_timeout_for_multiple_seconds() {
        new TimeoutSchedule<WebDriver>(fluentWait, 2).seconds();
        verify(fluentWait).withTimeout(2, TimeUnit.SECONDS);
    }

    @Test
    public void should_be_able_to_schedule_a_timeout_for_milliseconds() {
        new PollingSchedule<WebDriver>(fluentWait, 100).milliseconds();
        verify(fluentWait).pollingEvery(100, TimeUnit.MILLISECONDS);
    }
    
    @Test
    public void should_be_able_to_schedule_a_timeout_for_one_hour() {
        new TimeoutSchedule<WebDriver>(fluentWait, 1).hour();
        verify(fluentWait).withTimeout(1, TimeUnit.HOURS);
    }

    @Test
    public void should_be_able_to_schedule_a_timeout_for_multiple_hours() {
        new TimeoutSchedule<WebDriver>(fluentWait, 2).hours();
        verify(fluentWait).withTimeout(2, TimeUnit.HOURS);
    }
}
