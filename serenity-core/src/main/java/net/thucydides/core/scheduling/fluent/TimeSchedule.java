package net.thucydides.core.scheduling.fluent;

import net.thucydides.core.scheduling.SerenityFluentWait;

import java.util.concurrent.TimeUnit;

public abstract class TimeSchedule<T> {
    protected final SerenityFluentWait fluentWait;
    private final int amount;

    public TimeSchedule(SerenityFluentWait fluentWait, int amount) {
        this.fluentWait = fluentWait;
        this.amount = amount;
    }

    abstract protected SerenityFluentWait updateWaitBy(int amount, TimeUnit unit);

    public SerenityFluentWait milliseconds() {
        return updateWaitBy(amount, TimeUnit.MILLISECONDS);
    }

    public SerenityFluentWait second() {
        return updateWaitBy(amount, TimeUnit.SECONDS);
    }

    public SerenityFluentWait seconds() {
        return updateWaitBy(amount, TimeUnit.SECONDS);
    }

    public SerenityFluentWait minute() {
        return updateWaitBy(amount, TimeUnit.MINUTES);
    }

    public SerenityFluentWait minutes() {
        return updateWaitBy(amount, TimeUnit.MINUTES);
    }

    public SerenityFluentWait hour() {
        return updateWaitBy(amount, TimeUnit.HOURS);
    }

    public SerenityFluentWait hours() {
        return updateWaitBy(amount, TimeUnit.HOURS);
    }

}