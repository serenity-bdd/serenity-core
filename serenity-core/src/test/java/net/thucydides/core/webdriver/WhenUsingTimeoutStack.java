package net.thucydides.core.webdriver;

import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class WhenUsingTimeoutStack {

    @Mock
    private WebDriver webDriver;

    private Duration duration = Duration.ZERO;

    private TimeoutStack timeoutStack = new TimeoutStack();

    @Test
    public void remove_timeouts_from_map() {
        timeoutStack.pushTimeoutFor(webDriver,duration);
        assertThat(timeoutStack.containsTimeoutFor(webDriver)).isTrue();

        timeoutStack.releaseTimeoutFor(webDriver);
        assertThat(timeoutStack.containsTimeoutFor(webDriver)).isFalse();
        assertThat(timeoutStack.timeouts).isEmpty();
    }

    @Test
    public void does_not_remove_different_driver() {
        timeoutStack.pushTimeoutFor(webDriver,duration);
        timeoutStack.releaseTimeoutFor(mock(WebDriver.class));

        assertThat(timeoutStack.containsTimeoutFor(webDriver)).isTrue();
    }
}
