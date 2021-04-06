package net.thucydides.core.scheduling.fluent;

import net.thucydides.core.scheduling.SerenityFluentWait;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public abstract class TimeSchedule<T> {
    protected final SerenityFluentWait fluentWait;
    private final int amount;

    public TimeSchedule(SerenityFluentWait fluentWait, int amount) {
        this.fluentWait = fluentWait;
        this.amount = amount;
    }

    protected int getAmount() {
        return amount;
    }

    abstract protected SerenityFluentWait updateWaitBy(Duration duration);

    public SerenityFluentWait milliseconds() {
        return updateWaitBy(Duration.ofMillis(amount));
    }

    public SerenityFluentWait second() {
        return updateWaitBy(Duration.ofSeconds(amount));
    }

    public SerenityFluentWait seconds() {
        return updateWaitBy(Duration.ofSeconds(amount));
    }

    public SerenityFluentWait minute() {
        return updateWaitBy(Duration.ofMinutes(amount));
    }

    public SerenityFluentWait minutes() { return updateWaitBy(Duration.ofMinutes(amount)); }

    public SerenityFluentWait hour() {
        return updateWaitBy(Duration.ofHours(amount));
    }

    public SerenityFluentWait hours() {
        return updateWaitBy(Duration.ofHours(amount));
    }

}