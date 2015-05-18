package net.thucydides.core.scheduling;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.verify;

public class WhenSchedulingTestPolling {


    @Mock
    FluentWaitWithRefresh<WebDriver> fluentWait;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void should_be_able_to_poll_a_page_every_minute() {
        new PollingSchedule<WebDriver>(fluentWait, 1).minute();
        verify(fluentWait).pollingEvery(1, TimeUnit.MINUTES);
    }

    @Test
    public void should_be_able_to_poll_a_page_multiple_minutes() {
        new PollingSchedule<WebDriver>(fluentWait, 2).minutes();
        verify(fluentWait).pollingEvery(2, TimeUnit.MINUTES);
    }

    @Test
    public void should_be_able_to_poll_a_page_every_second() {
        new PollingSchedule<WebDriver>(fluentWait, 1).second();
        verify(fluentWait).pollingEvery(1, TimeUnit.SECONDS);
    }

    @Test
    public void should_be_able_to_poll_a_page_multiple_seconds() {
        new PollingSchedule<WebDriver>(fluentWait, 2).seconds();
        verify(fluentWait).pollingEvery(2, TimeUnit.SECONDS);
    }

    @Test
    public void should_be_able_to_poll_a_page_in_milliseconds() {
        new PollingSchedule<WebDriver>(fluentWait, 100).milliseconds();
        verify(fluentWait).pollingEvery(100, TimeUnit.MILLISECONDS);
    }


    @Test
    public void should_be_able_to_poll_a_page_every_hour() {
        new PollingSchedule<WebDriver>(fluentWait, 1).hour();
        verify(fluentWait).pollingEvery(1, TimeUnit.HOURS);
    }

    @Test
    public void should_be_able_to_poll_a_page_multiple_hours() {
        new PollingSchedule<WebDriver>(fluentWait, 2).hours();
        verify(fluentWait).pollingEvery(2, TimeUnit.HOURS);
    }
}
