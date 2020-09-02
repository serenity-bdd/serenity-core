package net.thucydides.core.scheduling;

import net.thucydides.core.scheduling.fluent.PollingSchedule;
import net.thucydides.core.scheduling.fluent.TimeoutSchedule;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Sleeper;

import java.time.Clock;

public class SerenityFluentWait extends FluentWait<WebDriver> {
    public SerenityFluentWait(WebDriver input) {
        super(input);
    }

    public SerenityFluentWait(WebDriver input, Clock clock, Sleeper sleeper) {
        super(input, clock, sleeper);
    }

    public TimeoutSchedule withTimeoutOf(int amount) {
        return new TimeoutSchedule(this, amount);
    }

    public PollingSchedule pollingEvery(int amount) {
        return new PollingSchedule(this, amount);
    }

}
