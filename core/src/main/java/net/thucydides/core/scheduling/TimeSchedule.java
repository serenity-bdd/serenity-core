package net.thucydides.core.scheduling;

import java.util.concurrent.TimeUnit;

public abstract class TimeSchedule<T> {
    protected final ThucydidesFluentWait<T> fluentWait;
    private final int amount;

    public TimeSchedule(ThucydidesFluentWait<T> fluentWait, int amount) {
        this.fluentWait = fluentWait;
        this.amount = amount;
    }

    abstract protected ThucydidesFluentWait<T> updateWaitBy(int amount, TimeUnit unit);

    public ThucydidesFluentWait<T> milliseconds() {
        return updateWaitBy(amount, TimeUnit.MILLISECONDS);
    }

    public ThucydidesFluentWait<T> second() {
        return updateWaitBy(amount, TimeUnit.SECONDS);
    }

    public ThucydidesFluentWait<T> seconds() {
        return updateWaitBy(amount, TimeUnit.SECONDS);
    }

    public ThucydidesFluentWait<T> minute() {
        return updateWaitBy(amount, TimeUnit.MINUTES);
    }

    public ThucydidesFluentWait<T> minutes() {
        return updateWaitBy(amount, TimeUnit.MINUTES);
    }

    public ThucydidesFluentWait<T> hour() {
        return updateWaitBy(amount, TimeUnit.HOURS);
    }

    public ThucydidesFluentWait<T> hours() {
        return updateWaitBy(amount, TimeUnit.HOURS);
    }

}